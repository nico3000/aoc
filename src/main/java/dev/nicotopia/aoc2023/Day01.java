package dev.nicotopia.aoc2023;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class Day01 {
    private static record Occurence(int pos, int value) {
        public boolean isValid() {
            return this.pos != -1;
        }

        public int compareByPos(Occurence other) {
            return Integer.compare(this.pos, other.pos);
        }
    }

    public static void main(String args[]) throws IOException {
        String digits[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "one", "two", "three", "four", "five", "six",
                "seven", "eight", "nine" };
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2023/day01.txt")))) {
            var lines = br.lines().toList();
            System.out.printf("Part one: %d\n", process(lines, Arrays.copyOf(digits, 9)));
            System.out.printf("Part two: %d\n", process(lines, digits));
        }
    }

    private static int process(Collection<String> lines, String digits[]) {
        int sum = 0;
        for (String line : lines) {
            var b = IntStream.range(0, digits.length).mapToObj(i -> new Occurence(line.indexOf(digits[i]), 1 + i % 9))
                    .filter(Occurence::isValid).min(Occurence::compareByPos);
            var e = IntStream.range(0, digits.length)
                    .mapToObj(i -> new Occurence(line.lastIndexOf(digits[i]), 1 + i % 9)).filter(Occurence::isValid)
                    .max(Occurence::compareByPos);
            if (b.isPresent() && e.isPresent()) {
                sum += 10 * b.get().value() + e.get().value();
            } else {
                System.err.printf("This line gives me a headache: %s\n", line);
            }
        }
        return sum;
    }
}