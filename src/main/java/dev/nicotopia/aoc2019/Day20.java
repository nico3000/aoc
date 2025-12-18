package dev.nicotopia.aoc2019;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.BasicGraph;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.HashedDijkstraDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day20 extends DayBase {
    private enum PortalType {
        BEGIN, END, INNER, OUTER,
    }

    private record Portal(String label, Vec2i pos, PortalType type) {
        public Portal(String label, Vec2i pos, PortalType type) {
            this.label = label;
            this.pos = pos;
            this.type = switch (label) {
                case "AA" -> PortalType.BEGIN;
                case "ZZ" -> PortalType.END;
                default -> type;
            };
        }

        public String toString() {
            return this.label + "(" + this.pos + ")," + this.type;
        }
    }

    private class Labyrinth {
        private final Map<Portal, Map<Portal, Long>> portals = new HashMap<>();

        public void addPortal(String label, Vec2i pos, PortalType type) {
            this.portals.put(new Portal(label, pos, type), new HashMap<>());
        }

        public Optional<Portal> getPortalDestination(Portal portal) {
            return this.portals.keySet().stream().filter(p -> portal != p && p.label().equals(portal.label()))
                    .findAny();
        }

        public void buildPaths(char map[][]) {
            for (Portal src : this.portals.keySet()) {
                HashedDijkstraDataStructure<Vec2i> dds = new HashedDijkstraDataStructure<>();
                Dijkstra.run(new BasicGraph<Vec2i>() {
                    @Override
                    public NodeDistancePair<Vec2i> getNeighbour(Vec2i node, int index) {
                        if (map[node.y() - 1][node.x()] == '.' && index-- == 0) {
                            return new NodeDistancePair<Vec2i>(new Vec2i(node.x(), node.y() - 1), 1);
                        } else if (map[node.y()][node.x() + 1] == '.' && index-- == 0) {
                            return new NodeDistancePair<Vec2i>(new Vec2i(node.x() + 1, node.y()), 1);
                        } else if (map[node.y() + 1][node.x()] == '.' && index-- == 0) {
                            return new NodeDistancePair<Vec2i>(new Vec2i(node.x(), node.y() + 1), 1);
                        } else if (map[node.y()][node.x() - 1] == '.' && index-- == 0) {
                            return new NodeDistancePair<Vec2i>(new Vec2i(node.x() - 1, node.y()), 1);
                        }
                        return null;
                    }
                }, src.pos(), dds);
                this.portals.keySet().stream().map(p -> new Pair<>(p, dds.getDistance(p.pos())))
                        .filter(p -> p.second() != null && p.second() != 0)
                        .forEach(p -> this.portals.get(src).put(p.first(), p.second()));
            }
        }

        public Portal getStart() {
            return this.portals.keySet().stream().filter(p -> p.type() == PortalType.BEGIN).findAny().get();
        }

        public Portal getEnd() {
            return this.portals.keySet().stream().filter(p -> p.type() == PortalType.END).findAny().get();
        }
    }

    private Labyrinth buildBaseLabyrinth() {
        Labyrinth l = new Labyrinth();
        char map[][] = this.getPrimaryPuzzleInputAs2DCharArray();
        for (int y = 2; y < map.length - 2; ++y) {
            for (int x = 2; x < map[0].length - 2; ++x) {
                PortalType type = x == 2 || y == 2 || x == map[0].length - 3 || y == map.length - 3 ? PortalType.OUTER
                        : PortalType.INNER;
                if (map[y][x] == '.' && Character.isLetter(map[y + 1][x])) {
                    l.addPortal("" + map[y + 1][x] + map[y + 2][x], new Vec2i(x, y), type);
                } else if (map[y][x] == '.' && Character.isLetter(map[y - 1][x])) {
                    l.addPortal("" + map[y - 2][x] + map[y - 1][x], new Vec2i(x, y), type);
                } else if (map[y][x] == '.' && Character.isLetter(map[y][x + 1])) {
                    l.addPortal("" + map[y][x + 1] + map[y][x + 2], new Vec2i(x, y), type);
                } else if (map[y][x] == '.' && Character.isLetter(map[y][x - 1])) {
                    l.addPortal("" + map[y][x - 2] + map[y][x - 1], new Vec2i(x, y), type);
                }
            }
        }
        l.buildPaths(map);
        return l;
    }

    private record PartOneGraph(Labyrinth labyrinth) implements BasicGraph<Portal> {
        @Override
        public NodeDistancePair<Portal> getNeighbour(Portal node, int index) {
            Map<Portal, Long> neighbours = this.labyrinth.portals.get(node);
            Iterator<Portal> iter = neighbours.keySet().iterator();
            while (iter.hasNext()) {
                Portal src = iter.next();
                if (index-- == 0) {
                    Optional<Portal> dst = labyrinth.getPortalDestination(src);
                    long dist = neighbours.get(src);
                    return dst.isPresent() ? new NodeDistancePair<>(dst.get(), dist + 1)
                            : new NodeDistancePair<>(src, dist);
                }
            }
            return null;
        }
    }

    private record DepthPortal(Portal portal, int depth) {
        public boolean isEnd() {
            return this.portal.type() == PortalType.END && this.depth == 0;
        }
    }

    private record PartTwoGraph(Labyrinth labyrinth) implements BasicGraph<DepthPortal> {
        @Override
        public NodeDistancePair<DepthPortal> getNeighbour(DepthPortal node, int index) {
            if (1000 < node.depth()) {
                throw new AocException("Probably no solution");
            }
            var destinations = labyrinth.portals.get(node.portal());
            for (var entry : destinations.entrySet()) {
                Portal nextPortal = entry.getKey();
                long nextDistance = entry.getValue();
                if (node.depth() == 0 && nextPortal.type() == PortalType.END && index-- == 0) {
                    return new NodeDistancePair<>(new DepthPortal(nextPortal, 0), nextDistance);
                } else if ((nextPortal.type() == PortalType.INNER
                        || (node.depth() != 0 && nextPortal.type() == PortalType.OUTER)) && index-- == 0) {
                    Portal nextPortalDst = labyrinth.getPortalDestination(nextPortal).get();
                    int nextDepth = node.depth() + switch (nextPortal.type()) {
                        case PortalType.INNER -> 1;
                        case PortalType.OUTER -> -1;
                        default -> throw new RuntimeException("can not happen");
                    };
                    return new NodeDistancePair<>(new DepthPortal(nextPortalDst, nextDepth), nextDistance + 1);
                }
            }
            return null;
        }
    }

    private long partOne(Labyrinth l) {
        HashedDijkstraDataStructure<Portal> dds = new HashedDijkstraDataStructure<>();
        Dijkstra.run(new PartOneGraph(l), l.getStart(), dds);
        return dds.getDistance(l.getEnd());
    }

    private long partTwo(Labyrinth l) {
        return AStar.run(new PartTwoGraph(l), new DepthPortal(l.getStart(), 0),
                new HashedAStarDataStructure<>(dp -> 0L, DepthPortal::isEnd)).distance();
    }

    @Override
    public void run() {
        Labyrinth l = this.addSilentTask("Build labyrinth", this::buildBaseLabyrinth);
        this.addTask("Part one", () -> this.partOne(l));
        this.addTask("Part two", () -> this.partTwo(l));
    }
}
