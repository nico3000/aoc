package dev.nicotopia.aoc2024;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.HashedDijkstraDataStructure;
import dev.nicotopia.aoc.graphlib.Node;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day16 extends DayBase {
    private class CharMap2DNode implements Node<CharMap2DNode> {
        private final Vec2i pos;
        private final Compass4 dir;

        public CharMap2DNode(Vec2i pos, Compass4 dir) {
            this.pos = pos;
            this.dir = dir;
        }

        @Override
        public NodeDistancePair<CharMap2DNode> getNeighbour(int idx) {
            if (idx == 0) {
                return new NodeDistancePair<>(new CharMap2DNode(this.pos, switch (this.dir) {
                    case N -> Compass4.E;
                    case E -> Compass4.S;
                    case S -> Compass4.W;
                    case W -> Compass4.N;
                }), 1000);
            } else if (idx == 1) {
                return new NodeDistancePair<>(new CharMap2DNode(this.pos, switch (this.dir) {
                    case N -> Compass4.W;
                    case E -> Compass4.N;
                    case S -> Compass4.E;
                    case W -> Compass4.S;
                }), 1000);
            } else if (idx == 2 && Day16.this.map.get(this.pos.getNeighbour(this.dir)) != '#') {
                return new NodeDistancePair<>(new CharMap2DNode(this.pos.getNeighbour(this.dir), this.dir), 1);
            }
            return null;
        }

        @Override
        public boolean equals(Object other) {
            return other == this
                    || (other instanceof CharMap2DNode o && o.pos.equals(this.pos) && o.dir.equals(this.dir));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.pos, this.dir);
        }
    }

    private CharMap2D map;
    private final HashedDijkstraDataStructure<CharMap2DNode> dds = new HashedDijkstraDataStructure<>();

    private int partOne() {
        CharMap2DNode startNode = new CharMap2DNode(this.map.findAnyPositionOf('S').get(), Compass4.E);
        Dijkstra.run(startNode, this.dds);
        Vec2i end = this.map.findAnyPositionOf('E').get();
        return Stream.of(Compass4.values()).map(d -> new CharMap2DNode(end, d)).map(this.dds::getDistance)
                .mapToInt(Integer::valueOf).min().getAsInt();
    }

    private int partTwo(int min) {
        Set<Vec2i> pathNodes = new HashSet<>();
        Vec2i end = this.map.findAnyPositionOf('E').get();
        Set<NodeDistancePair<CharMap2DNode>> current = this.dds.nodeDistancePairs(true)
                .filter(e -> e.node().pos.equals(end) && e.distance() == min).collect(Collectors.toSet());
        current.forEach(n -> pathNodes.add(n.node().pos));
        while (!current.isEmpty()) {
            Set<NodeDistancePair<CharMap2DNode>> newSet = new HashSet<>();
            for (var original : current) {
                this.dds.nodeDistancePairs(true).filter(
                        nd -> (nd.distance() + 1 == original.distance() || nd.distance() + 1000 == original.distance())
                                && IntStream.range(0, 3).mapToObj(i -> nd.node().getNeighbour(i))
                                        .anyMatch(n -> n != null && n.node().equals(original.node())
                                                && nd.distance() + n.distance() == original.distance()))
                        .forEach(newSet::add);
            }
            current = newSet;
            current.forEach(n -> pathNodes.add(n.node().pos));
        }
        return pathNodes.size();
    }

    @Override
    public void run() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        int min = this.addTask("Part one", this::partOne);
        this.addTask("Part two", () -> this.partTwo(min));
    }
}
