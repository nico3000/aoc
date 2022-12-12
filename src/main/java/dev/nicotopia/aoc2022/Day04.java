package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day04 {
    private record Range(int beg, int end) {
        public Range(String desc) {
            this(desc.split("-"));
        }

        private Range(String splitDesc[]) {
            this(Integer.valueOf(splitDesc[0]), Integer.valueOf(splitDesc[1]) + 1);
        }

        public boolean contains(Range other) {
            return this.beg <= other.beg && other.end <= this.end;
        }

        public boolean overlaps(Range other) {
            return other.contains(this) || (this.beg <= other.beg && other.beg < this.end)
                    || (this.beg < other.end && other.end <= this.end);
        }
    }

    public static void main(String[] args) throws IOException {
        List<Range[]> rangePairs;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day04.class.getResourceAsStream("/2022/day04.txt")))) {
            rangePairs = br.lines().map(l -> l.split(","))
                    .map(desc -> new Range[] { new Range(desc[0]), new Range(desc[1]) }).toList();
        }
        long containCount = rangePairs.stream().filter(p -> p[0].contains(p[1]) || p[1].contains(p[0])).count();
        long overlapCount = rangePairs.stream().filter(p -> p[0].overlaps(p[1])).count();
        System.out.printf("Contain count: %d, overlap count: %d\n", containCount, overlapCount);
    }
}