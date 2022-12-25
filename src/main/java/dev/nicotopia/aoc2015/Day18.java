package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Day18 {
    public static void main(String[] args) throws IOException {
        char arena[][];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day18.class.getResourceAsStream("/2015/day18.txt")))) {
            arena = br.lines().map(String::toCharArray).toArray(char[][]::new);
        }
        char copy[][] = Arrays.stream(arena).map(String::new).map(String::toCharArray).toArray(char[][]::new);
        System.out.println("Part one: " + execute(arena, false));
        System.out.println("Part two: " + execute(copy, true));
    }

    private static long execute(char arena[][], boolean partTwo) {
        char working[][] = new char[arena.length][arena[0].length];
        for (int c = 0; c < 100; ++c) {
            char t[][][] = new char[][][] { arena, working };
            IntStream.range(0, arena.length)
                    .forEach(y -> IntStream.range(0, t[0][y].length).forEach(x -> update(t[0], t[1], x, y, partTwo)));
            working = t[0];
            arena = t[1];
        }
        return Arrays.stream(arena).map(String::new).mapToLong(r -> r.chars().filter(c -> c == '#').count()).sum();
    }

    private static void update(char[][] src, char[][] dst, int x, int y, boolean partTwo) {
        int neighbours = 0;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                if (0 <= y + dy && y + dy < src.length && 0 <= x + dx && x + dx < src[y].length
                        && src[y + dy][x + dx] == '#') {
                    ++neighbours;
                }
            }
        }
        if (partTwo && (x == 0 || x == src[y].length - 1) && (y == 0 || y == src.length - 1)) {
            dst[y][x] = '#';
        } else {
            dst[y][x] = (src[y][x] == '#' && (neighbours == 3 || neighbours == 4))
                    || (src[y][x] != '#' && neighbours == 3) ? '#' : '.';
        }
    }
}
