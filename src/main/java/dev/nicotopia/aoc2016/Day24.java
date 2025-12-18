package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiFunction;

import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;
import dev.nicotopia.aoc.graphlib.TravelingSalesman;

public class Day24 {
    private record Position(int x, int y) {
        public Position getNeighbour(int idx) {
            return switch (idx) {
                case 0 -> new Position(this.x - 1, this.y);
                case 1 -> new Position(this.x + 1, this.y);
                case 2 -> new Position(this.x, this.y - 1);
                case 3 -> new Position(this.x, this.y + 1);
                default -> null;
            };
        }
    }

    private record LabeledPosition(char label, Position pos) {
        public LabeledPosition(char label, int x, int y) {
            this(label, new Position(x, y));
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day24.class.getResourceAsStream("/2016/day24.txt")))) {
            lines = br.lines().toList();
        }
        int width = lines.stream().mapToInt(String::length).findAny().getAsInt();
        long arena[][] = new long[lines.size()][width];
        List<LabeledPosition> toVisit = new LinkedList<>();
        for (int y = 0; y < arena.length; ++y) {
            for (int x = 0; x < arena[y].length; ++x) {
                char tile = lines.get(y).charAt(x);
                if (tile == '#') {
                    arena[y][x] = -1;
                } else if (tile != '.') {
                    toVisit.add(new LabeledPosition(tile, x, y));
                }
            }
        }
        long shortestDistances[][] = new long[toVisit.size()][toVisit.size()];
        BiFunction<Position, Integer, NodeDistancePair<Position>> neighbourGetter = (n, i) -> {
            Position p = n.getNeighbour(i);
            return p == null ? null : new NodeDistancePair<>(p, 1);
        };
        for (LabeledPosition from : toVisit) {
            Dijkstra.run(p -> arena[p.y][p.x], (p, d) -> arena[p.y][p.x] = d, () -> reset(arena),
                    neighbourGetter,
                    from.pos);
            for (LabeledPosition to : toVisit) {
                shortestDistances[from.label - '0'][to.label - '0'] = arena[to.pos.y][to.pos.x];
            }
        }
        System.out.println("Part one: " + TravelingSalesman.run(shortestDistances, OptionalInt.of(0)));
        System.out.println("Part two: " + TravelingSalesman.run(shortestDistances));
    }

    private static void reset(long arena[][]) {
        for (int y = 0; y < arena.length; ++y) {
            for (int x = 0; x < arena[y].length; ++x) {
                if (arena[y][x] != -1) {
                    arena[y][x] = Integer.MAX_VALUE;
                }
            }
        }
    }
}