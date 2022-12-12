package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day11 {
    public static void main(String[] args) throws IOException {
        List<String> directions;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day11.class.getResourceAsStream("/2017/day11.txt")))) {
            directions = Arrays.asList(br.readLine().split(","));
        }
        int x = 0;
        int y = 0;
        int furthest = 0;
        for (String d : directions) {
            y += switch (d) {
                case "nw", "ne" -> x % 2 == 0 ? 0 : -1;
                case "n" -> 1;
                case "se", "sw" -> x % 2 == 0 ? 1 : 0;
                case "s" -> -1;
                default -> 0;
            };
            x += switch (d) {
                case "nw", "sw" -> -1;
                case "n", "s" -> 0;
                case "ne", "se" -> 1;
                default -> 0;
            };
            furthest = Math.max(furthest, Math.max(Math.abs(x), Math.abs(y)));
        }
        System.out.printf("Part one: %d, part two: %d\n", Math.max(Math.abs(x), Math.abs(y)), furthest);
    }
}