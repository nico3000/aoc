package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {
    private static record Disc(int positions, int p0) {
        private Disc combine(Disc other) {
            int n = this.p0 == 0 ? 0 : this.positions - this.p0;
            while((other.p0 + n) % other.positions != 0) {
                n += this.positions;
            }
            int kgv = this.positions * other.positions / ggT(this.positions, other.positions);
            return new Disc(kgv, kgv - n);
        }

        private static int ggT(int a, int b) {
            return a == 0 ? b : b == 0 ? a : a < b ? ggT(a, b % a) : ggT(a % b, b);
        }
    }

    public static void main(String args[]) throws IOException {
        LinkedList<Disc> discs;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day15.class.getResourceAsStream("/2016/day15.txt")))) {
            Pattern p = Pattern.compile("Disc #(\\d+) has (\\d+) positions; at time=0, it is at position (\\d+)\\.");
            discs = new LinkedList<>(
                    br.lines().map(p::matcher).filter(Matcher::matches).map(m -> new Disc(Integer.valueOf(m.group(2)),
                            Integer.valueOf(m.group(1)) + Integer.valueOf(m.group(3)))).toList());
        }
        long begin = System.nanoTime();
        Disc combined = null;
        for(Disc disc : discs) {
            combined = combined == null ? disc : combined.combine(disc);
        }
        int partOne = combined.p0 == 0 ? 0 : combined.positions - combined.p0;
        combined = combined.combine(new Disc(11, discs.size() + 1));
        int partTwo = combined.p0 == 0 ? 0 : combined.positions - combined.p0;
        long end = System.nanoTime();
        System.out.printf("part one: %d, part two %d, time: %.3f ms\n", partOne, partTwo,
                1e-6f * (float) (end - begin));
    }
}