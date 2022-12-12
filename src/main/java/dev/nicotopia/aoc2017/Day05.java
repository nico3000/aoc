package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day05 {
    public static void main(String[] args) throws IOException {
        int offsets[];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day05.class.getResourceAsStream("/2017/day05.txt")))) {
            offsets = br.lines().mapToInt(Integer::valueOf).toArray();
        }
        int offsetsPartTwo[] = Arrays.copyOf(offsets, offsets.length);
        int pos = 0;
        int count = 0;
        while (0 <= pos && pos < offsets.length) {
            pos += offsets[pos]++;
            ++count;
        }
        System.out.println("Part one: " + count);
        pos = 0;
        count = 0;
        while (0 <= pos && pos < offsetsPartTwo.length) {
            int oldPos = pos;
            pos += offsetsPartTwo[pos];
            offsetsPartTwo[oldPos] += offsetsPartTwo[oldPos] < 3 ? 1 : -1;
            ++count;
        }
        System.out.println("Part two: " + count);
    }
}
