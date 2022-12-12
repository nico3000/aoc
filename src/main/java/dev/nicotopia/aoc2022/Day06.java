package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day06 {
    public static void main(String[] args) throws IOException {
        String input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day06.class.getResourceAsStream("/2022/day06.txt")))) {
            input = br.readLine();
        }
        System.out.printf("Part one: %d, part two: %d\n", getFirstMarkerEnd(input, 4), getFirstMarkerEnd(input, 14));
    }

    private static int getFirstMarkerEnd(String signal, int markerSize) {
        for (int i = 0; i < signal.length() - markerSize; ++i) {
            if (signal.substring(i, i + markerSize).chars().distinct().count() == markerSize) {
                return i + markerSize;
            }
        }
        return -1;
    }
}