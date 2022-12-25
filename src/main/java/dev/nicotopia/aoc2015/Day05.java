package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day05 {
    public static void main(String[] args) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day05.class.getResourceAsStream("/2015/day05.txt")))) {
            lines = br.lines().toList();
        }
        long niceCount = lines.stream().filter(
                l -> 3 <= l.chars().filter(c -> c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u').count()
                        && IntStream.range(0, 26).mapToObj(i -> ("" + (char) ('a' + i)) + (char) ('a' + i))
                                .anyMatch(s -> l.contains(s))
                        && Arrays.asList("ab", "cd", "pq", "xy").stream().noneMatch(l::contains))
                .count();
        System.out.println("Part one: " + niceCount);
        niceCount = 0;
        for (String line : lines) {
            boolean first = false;
            boolean second = false;
            for (int i = 0; (!first || !second) && i < line.length() - 2; ++i) {
                first |= line.indexOf(line.substring(i, i + 2), i + 2) != -1;
                second |= line.charAt(i) == line.charAt(i + 2);
            }
            if (first && second) {
                ++niceCount;
            }
        }
        System.out.println("Part two: " + niceCount);
    }
}