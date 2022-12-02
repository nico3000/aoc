package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day01 {
    public static void main(String args[]) throws IOException {
        int[] depths;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/day01.txt")))) {
            depths = br.lines().mapToInt(Integer::valueOf).toArray();
        }

        int deeperCount = 0;
        for (int i = 1; i < depths.length; ++i) {
            if (depths[i - 1] < depths[i]) {
                ++deeperCount;
            }
        }
        System.out.printf("Deeper count: %d\n", deeperCount);

        int slidingWindowDeeperCount = 0;
        for (int i = 3; i < depths.length; ++i) {
            if (depths[i - 3] < depths[i]) {
                ++slidingWindowDeeperCount;
            }
        }
        System.out.printf("Sliding window deeper count: %d\n", slidingWindowDeeperCount);
    }
}