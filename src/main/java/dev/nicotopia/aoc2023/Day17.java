package dev.nicotopia.aoc2023;

import java.util.function.BiFunction;
import java.util.function.Function;

import dev.nicotopia.Compass;
import dev.nicotopia.GraphUtil;
import dev.nicotopia.GraphUtil.HashedAStarInterface;
import dev.nicotopia.GraphUtil.NodeDistancePair;
import dev.nicotopia.Vec2i;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day17 extends DayBase {
    private record Node(Vec2i pos, Compass lastDir, int lastDirCount) {
        public Node move(Compass dir) {
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
        if (y != 0 && (node.lastDir != Compass.N || node.lastDirCount != 3) && node.lastDir != Compass.S && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass.N), this.map[y - 1][x]);
        }
        if (x != this.map[y].length - 1 && (node.lastDir != Compass.E || node.lastDirCount != 3)
                && node.lastDir != Compass.W && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass.E), this.map[y][x + 1]);
        }
        if (y != this.map.length - 1 && (node.lastDir != Compass.S || node.lastDirCount != 3)
                && node.lastDir != Compass.N && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass.S), this.map[y + 1][x]);
        }
        if (x != 0 && (node.lastDir != Compass.E || node.lastDirCount != 3) && node.lastDir != Compass.W && i-- == 0) {
            return new NodeDistancePair<>(node.move(Compass.W), this.map[y][x - 1]);
        }
        return null;
    }

    private NodeDistancePair<Node> getNeighbourPartTwo(Node node, int i) {
        if (3 <= i) {
            return null;
        }
        if (node.lastDir != Compass.S && (node.lastDir == null || (node.lastDir != Compass.N && 4 <= node.lastDirCount)
                || (node.lastDir == Compass.N && node.lastDirCount < 10))) {
            Node north = node.move(Compass.N);
            if (this.isInside(north.pos) && i-- == 0) {
                return new NodeDistancePair<>(north, this.get(north));
            }
        }
        if (node.lastDir != Compass.W && (node.lastDir == null || (node.lastDir != Compass.E && 4 <= node.lastDirCount)
                || (node.lastDir == Compass.E && node.lastDirCount < 10))) {
            Node east = node.move(Compass.E);
            if (this.isInside(east.pos) && i-- == 0) {
                return new NodeDistancePair<>(east, this.get(east));
            }
        }
        if (node.lastDir != Compass.N && (node.lastDir == null || (node.lastDir != Compass.S && 4 <= node.lastDirCount)
                || (node.lastDir == Compass.S && node.lastDirCount < 10))) {
            Node south = node.move(Compass.S);
            if (this.isInside(south.pos) && i-- == 0) {
                return new NodeDistancePair<>(south, this.get(south));
            }
        }
        if (node.lastDir != Compass.E && (node.lastDir == null || (node.lastDir != Compass.W && 4 <= node.lastDirCount)
                || (node.lastDir == Compass.W && node.lastDirCount < 10))) {
            Node west = node.move(Compass.W);
            if (this.isInside(west.pos) && i-- == 0) {
                return new NodeDistancePair<>(west, this.get(west));
            }
        }
        return null;
    }

    private int estimate(Node node) {
        return node.pos.manhattanDistanceTo(new Vec2i(this.map[this.map.length - 1].length - 1, this.map.length - 1));
    }

    private int execute(BiFunction<Node, Integer, NodeDistancePair<Node>> neighbourGetter,
            Function<Node, Boolean> isFinal) {
        NodeDistancePair<Node> result = GraphUtil.aStar(
                new HashedAStarInterface<Node>(neighbourGetter, this::estimate, isFinal),
                new Node(new Vec2i(0, 0), null, 0));
        return result == null ? 0 : result.distance();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example 1", "/2023/day17e1.txt");
        this.addPresetFromResource("Example 2", "/2023/day17e2.txt");
        this.map = this.addSilentTask("Process input", () -> this.getPrimaryPuzzleInput().stream()
                .map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
        Vec2i dest = new Vec2i(map[map.length - 1].length - 1, map.length - 1);
        this.addTask("Part one", () -> this.execute(this::getNeighbourPartOne, node -> node.pos.equals(dest)));
        this.addTask("Part two",
                () -> this.execute(this::getNeighbourPartTwo, node -> 4 <= node.lastDirCount && node.pos.equals(dest)));
    }
}