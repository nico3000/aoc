package dev.nicotopia.aoc2019;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.BasicGraph;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.HashedDijkstraDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day20 extends DayBase {
    private class Labyrinth {
        private final char[][] layout;
        private final Vec2i start;
        private final Vec2i end;
        private final Map<Vec2i, Vec2i> teleporters = new HashMap<>();

        public Labyrinth(char[][] layout) {
            this.layout = new char[layout.length - 4][layout[0].length - 4];
            Map<String, Vec2i> pendingTeleporters = new HashMap<>();
            for (int x = 2; x < layout[0].length - 2; ++x) {
                if (Character.isLetter(layout[0][x])) {
                    this.onTeleporter(pendingTeleporters, "" + layout[0][x] + layout[1][x], new Vec2i(x - 2, 0));
                }
                if (Character.isLetter(layout[layout.length - 1][x])) {
                    this.onTeleporter(pendingTeleporters,
                            "" + layout[layout.length - 2][x] + layout[layout.length - 1][x],
                            new Vec2i(x - 2, layout.length - 5));
                }
            }
            for (int y = 2; y < layout.length - 2; ++y) {
                if (Character.isLetter(layout[y][0])) {
                    this.onTeleporter(pendingTeleporters, "" + layout[y][0] + layout[y][1], new Vec2i(0, y - 2));
                }
                if (Character.isLetter(layout[y][layout[y].length - 1])) {
                    this.onTeleporter(pendingTeleporters,
                            "" + layout[y][layout[y].length - 2] + layout[y][layout[y].length - 1],
                            new Vec2i(layout[y].length - 5, y - 2));
                }
            }
            for (int y = 0; y < this.layout.length; ++y) {
                for (int x = 0; x < this.layout[y].length; ++x) {
                    char c = layout[y + 2][x + 2];
                    if (Character.isLetter(c)) {
                        this.layout[y][x] = '#';
                        String label = String.valueOf(c);
                        if (Character.isLetter(layout[y + 3][x + 2])) {
                            label += layout[y + 3][x + 2];
                            Vec2i pos = new Vec2i(x, layout[y + 1][x + 2] == '.' ? y - 1 : y + 2);
                            this.onTeleporter(pendingTeleporters, label, pos);
                        } else if (Character.isLetter(layout[y + 2][x + 3])) {
                            label += layout[y + 2][x + 3];
                            Vec2i pos = new Vec2i(layout[y + 2][x + 1] == '.' ? x - 1 : x + 2, y);
                            this.onTeleporter(pendingTeleporters, label, pos);
                        }
                    } else {
                        this.layout[y][x] = c == '.' ? '.' : '#';
                    }
                }
            }
            this.start = pendingTeleporters.remove("AA");
            this.end = pendingTeleporters.remove("ZZ");
            if (!pendingTeleporters.isEmpty()) {
                throw new AocException("Pending teleporters remain");
            }
        }

        private void onTeleporter(Map<String, Vec2i> pendingTeleporters, String tag, Vec2i pos) {
            Vec2i otherPos = pendingTeleporters.remove(tag);
            if (otherPos != null) {
                this.teleporters.put(pos, otherPos);
                this.teleporters.put(otherPos, pos);
            } else {
                pendingTeleporters.put(tag, pos);
            }
        }

        public char getTile(int x, int y) {
            char tile = 0 <= y && y < this.layout.length && 0 <= x && x < this.layout[y].length ? this.layout[y][x]
                    : '#';
            return tile == ' ' ? '#' : tile;
        }

        @Override
        public String toString() {
            return IntStream.range(0, this.layout.length).mapToObj(y -> String.valueOf(this.layout[y]))
                    .collect(Collectors.joining("\n"));
        }
    }

    private int partOne(Labyrinth labyrinth) {
        HashedDijkstraDataStructure<Vec2i> dds = new HashedDijkstraDataStructure<>();
        BasicGraph<Vec2i> graph = (Vec2i p, int index) -> {
            if (4 < index) {
                return null;
            } else if (labyrinth.getTile(p.x() - 1, p.y()) == '.' && index-- == 0) {
                return new NodeDistancePair<>(new Vec2i(p.x() - 1, p.y()), 1);
            } else if (labyrinth.getTile(p.x() + 1, p.y()) == '.' && index-- == 0) {
                return new NodeDistancePair<>(new Vec2i(p.x() + 1, p.y()), 1);
            } else if (labyrinth.getTile(p.x(), p.y() - 1) == '.' && index-- == 0) {
                return new NodeDistancePair<>(new Vec2i(p.x(), p.y() - 1), 1);
            } else if (labyrinth.getTile(p.x(), p.y() + 1) == '.' && index-- == 0) {
                return new NodeDistancePair<>(new Vec2i(p.x(), p.y() + 1), 1);
            } else if (index == 0) {
                Vec2i dst = labyrinth.teleporters.get(p);
                if (dst != null) {
                    return new NodeDistancePair<>(dst, 1);
                }
            }
            return null;
        };
        Dijkstra.run(graph, labyrinth.start, dds);
        return dds.getDistance(labyrinth.end);
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        Labyrinth labyrinth = new Labyrinth(this.getPrimaryPuzzleInputAs2DCharArray());
        this.addTask("Part one", () -> this.partOne(labyrinth));
    }
}
