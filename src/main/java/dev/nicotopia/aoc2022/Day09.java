package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day09 {
    private record Position(int x, int y) {
        public int getManhattanDistance(Position other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }

        public int getSupremumDistance(Position other) {
            return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
        }

        public Set<Position> getNeighbours() {
            return new HashSet<>(
                    IntStream.range(0, 9).mapToObj(i -> new Position(this.x - 1 + i % 3, this.y - 1 + i / 3))
                            .filter(p -> !this.equals(p)).toList());
        }
    }

    private static class Knot {
        private Position pos;

        public Knot(int x, int y) {
            this.pos = new Position(x, y);
        }
    }

    private record Rope(List<Knot> knots) {
        public Rope(int knotCount) {
            this(new ArrayList<>(knotCount));
            this.knots.addAll(IntStream.range(0, knotCount).mapToObj(i -> new Knot(0, 0)).toList());
        }

        public void move(char dir) {
            Knot head = knots.get(0);
            head.pos = switch (dir) {
                case 'L' -> new Position(head.pos.x - 1, head.pos.y);
                case 'R' -> new Position(head.pos.x + 1, head.pos.y);
                case 'U' -> new Position(head.pos.x, head.pos.y - 1);
                case 'D' -> new Position(head.pos.x, head.pos.y + 1);
                default -> throw new IllegalArgumentException();
            };
            for (int i = 1; i < this.knots.size(); ++i) {
                Knot prev = this.knots.get(i - 1);
                Knot knot = this.knots.get(i);
                if (1 < prev.pos.getSupremumDistance(knot.pos)) {
                    Set<Position> prevNeighbours = prev.pos.getNeighbours();
                    Set<Position> currentNeighbours = knot.pos.getNeighbours();
                    currentNeighbours.removeIf(n -> !prevNeighbours.contains(n));
                    knot.pos = currentNeighbours.stream().min((l, r) -> Integer
                            .compare(l.getManhattanDistance(prev.pos), r.getManhattanDistance(prev.pos))).get();
                }
            }
        }

        public Position getTailPos() {
            return this.knots.get(this.knots.size() - 1).pos;
        }
    }

    private record Move(char dir, int count) {
    }

    public static void main(String[] args) throws IOException {
        List<Move> moves;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day09.class.getResourceAsStream("/2022/day09.txt")))) {
            moves = br.lines().map(s -> s.split("\\s+")).map(s -> new Move(s[0].charAt(0), Integer.valueOf(s[1])))
                    .toList();
        }
        System.out.println("Part one: " + execute(new Rope(2), moves).size());
        System.out.println("Part two: " + execute(new Rope(10), moves).size());
    }

    public static Set<Position> execute(Rope rope, List<Move> moves) {
        Set<Position> uniqueTailPositions = new HashSet<>(Arrays.asList(rope.getTailPos()));
        for (Move move : moves) {
            for (int i = 0; i < move.count; ++i) {
                rope.move(move.dir);
                uniqueTailPositions.add(rope.getTailPos());
            }
        }
        //printPositions(uniqueTailPositions);
        return uniqueTailPositions;
    }

    public static void printPositions(Set<Position> positions) {
        int minX = positions.stream().mapToInt(p -> p.x).min().getAsInt();
        int maxX = positions.stream().mapToInt(p -> p.x).max().getAsInt();
        int minY = positions.stream().mapToInt(p -> p.y).min().getAsInt();
        int maxY = positions.stream().mapToInt(p -> p.y).max().getAsInt();
        for (int y = minY; y <= maxY; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                System.out.print(positions.contains(new Position(x, y)) ? '#' : '.');
            }
            System.out.println();
        }
        //positions.forEach(p -> System.out.println(p.x + "," + p.y));
    }
}