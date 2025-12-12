package dev.nicotopia.aoc2023;

import java.util.function.Predicate;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.BasicGraph;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day17 extends DayBase {
    private record Node(Vec2i pos, Compass4 lastDir, int lastDirCount) {
        public Node move(Compass4 dir) {
            return new Node(switch (dir) {
                case N -> new Vec2i(this.pos.x(), this.pos.y() - 1);
                case E -> new Vec2i(this.pos.x() + 1, this.pos.y());
                case S -> new Vec2i(this.pos.x(), this.pos.y() + 1);
                case W -> new Vec2i(this.pos.x() - 1, this.pos.y());
                default -> throw new AocException("Illegal direction %s", dir);
            }, dir, this.lastDir == dir ? this.lastDirCount + 1 : 1);
        }
    }

    private int[][] map;

    private boolean isInside(Vec2i pos) {
        return 0 <= pos.y() && pos.y() < this.map.length && 0 <= pos.x() && pos.x() < this.map[pos.y()].length;
    }

    private int get(Node node) {
        return this.map[node.pos.y()][node.pos.x()];
    }

    private NodeDistancePair<Node> getNeighbourPartOne(Node node, int i) {
        if (3 <= i) {
            return null;
        }
        int x = node.pos.x();
        int y = node.pos.y();
        if (y != 0 && (node.lastDir != Compass4.N || node.lastDirCount != 3) && node.lastDir != Compass4.S
                && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass4.N), this.map[y - 1][x]);
        }
        if (x != this.map[y].length - 1 && (node.lastDir != Compass4.E || node.lastDirCount != 3)
                && node.lastDir != Compass4.W && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass4.E), this.map[y][x + 1]);
        }
        if (y != this.map.length - 1 && (node.lastDir != Compass4.S || node.lastDirCount != 3)
                && node.lastDir != Compass4.N && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass4.S), this.map[y + 1][x]);
        }
        if (x != 0 && (node.lastDir != Compass4.E || node.lastDirCount != 3) && node.lastDir != Compass4.W
                && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass4.W), this.map[y][x - 1]);
        }
        return null;
    }

    private NodeDistancePair<Node> getNeighbourPartTwo(Node node, int i) {
        if (3 <= i) {
            return null;
        }
        if (node.lastDir != Compass4.S
                && (node.lastDir == null || (node.lastDir != Compass4.N && 4 <= node.lastDirCount)
                        || (node.lastDir == Compass4.N && node.lastDirCount < 10))) {
            Node north = node.move(Compass4.N);
            if (this.isInside(north.pos) && i-- == 0) {
                return new NodeDistancePair<>(north, this.get(north));
            }
        }
        if (node.lastDir != Compass4.W
                && (node.lastDir == null || (node.lastDir != Compass4.E && 4 <= node.lastDirCount)
                        || (node.lastDir == Compass4.E && node.lastDirCount < 10))) {
            Node east = node.move(Compass4.E);
            if (this.isInside(east.pos) && i-- == 0) {
                return new NodeDistancePair<>(east, this.get(east));
            }
        }
        if (node.lastDir != Compass4.N
                && (node.lastDir == null || (node.lastDir != Compass4.S && 4 <= node.lastDirCount)
                        || (node.lastDir == Compass4.S && node.lastDirCount < 10))) {
            Node south = node.move(Compass4.S);
            if (this.isInside(south.pos) && i-- == 0) {
                return new NodeDistancePair<>(south, this.get(south));
            }
        }
        if (node.lastDir != Compass4.E
                && (node.lastDir == null || (node.lastDir != Compass4.W && 4 <= node.lastDirCount)
                        || (node.lastDir == Compass4.W && node.lastDirCount < 10))) {
            Node west = node.move(Compass4.W);
            if (this.isInside(west.pos) && i-- == 0) {
                return new NodeDistancePair<>(west, this.get(west));
            }
        }
        return null;
    }

    private int estimate(Node node) {
        return node.pos.manhattanDistanceTo(new Vec2i(this.map[this.map.length - 1].length - 1, this.map.length - 1));
    }

    private int execute(BasicGraph<Node> graph, Predicate<Node> isFinal) {
        NodeDistancePair<Node> result = AStar.run(graph, new Node(new Vec2i(0, 0), null, 0),
                new HashedAStarDataStructure<Node>(this::estimate, isFinal));
        return result == null ? 0 : result.distance();
    }

    @Override
    public void run() {
        this.map = this.addSilentTask("Process input", () -> this.getPrimaryPuzzleInput().stream()
                .map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
        Vec2i dest = new Vec2i(map[map.length - 1].length - 1, map.length - 1);
        this.addTask("Part one", () -> this.execute(this::getNeighbourPartOne, node -> node.pos.equals(dest)));
        this.addTask("Part two",
                () -> this.execute(this::getNeighbourPartTwo, node -> 4 <= node.lastDirCount && node.pos.equals(dest)));
    }
}