package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Day13 {
    public static record Position(int x, int y) {
        public Position fold(Fold fold) {
            return switch (fold.axis) {
                case 'x' -> fold.coord < this.x ? new Position(2 * fold.coord - this.x, this.y) : this;
                case 'y' -> fold.coord < this.y ? new Position(this.x, 2 * fold.coord - this.y) : this;
                default -> throw new RuntimeException();
            };
        }
    }

    public static record Fold(char axis, int coord) {
    }

    public static void main(String args[]) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day13.class.getResourceAsStream("/2021/day13.txt")))) {
            lines = br.lines().toList();
        }
        Set<Position> dots = new HashSet<>(lines.stream().map(l -> l.split(",", 2)).filter(s -> s.length == 2)
                .map(s -> new Position(Integer.valueOf(s[0]), Integer.valueOf(s[1]))).toList());
        List<Fold> folds = new LinkedList<>(lines.stream().map(l -> l.split("\\s", 3)).filter(s -> s.length == 3)
                .map(s -> new Fold(s[2].charAt(0), Integer.valueOf(s[2].substring(2)))).toList());
        for (Fold f : folds) {
            dots = dots.stream().collect(HashSet::new, (s, d) -> s.add(d.fold(f)), HashSet::addAll);
            System.out.printf("dot count after fold %d: %d\n", folds.indexOf(f), dots.size());
        }
        int maxX = dots.stream().mapToInt(d -> d.x).max().getAsInt();
        int maxY = dots.stream().mapToInt(d -> d.y).max().getAsInt();
        for (int y = 0; y <= maxY; ++y) {
            for (int x = 0; x <= maxX; ++x) {
                System.out.print(dots.contains(new Position(x, y)) ? '#' : ' ');
            }
            System.out.println();
        }
    }
}