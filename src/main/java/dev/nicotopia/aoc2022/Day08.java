package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

public class Day08 {
    public static void main(String[] args) throws IOException {
        int grid[][];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day08.class.getResourceAsStream("/2022/day08.txt")))) {
            grid = br.lines().map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new);
        }
        int count = 2 * (grid.length + grid[0].length - 2);
        for (int y = 1; y < grid.length - 1; ++y) {
            for (int x = 1; x < grid[y].length - 1; ++x) {
                int x0 = x;
                int y0 = y;
                boolean v0 = IntStream.range(0, x).boxed().noneMatch(i -> grid[y0][x0] <= grid[y0][i]);
                boolean v1 = IntStream.range(x + 1, grid[y].length).boxed().noneMatch(i -> grid[y0][x0] <= grid[y0][i]);
                boolean v2 = IntStream.range(0, y).boxed().noneMatch(i -> grid[y0][x0] <= grid[i][x0]);
                boolean v3 = IntStream.range(y + 1, grid.length).boxed().noneMatch(i -> grid[y0][x0] <= grid[i][x0]);
                if (v0 || v1 || v2 || v3) {
                    ++count;
                }
            }
        }
        System.out.println("Part one: " + count);
        int maxScenicScore = 0;
        for (int y = 1; y < grid.length - 1; ++y) {
            for (int x = 1; x < grid[y].length - 1; ++x) {
                int x0 = 0;
                if (x != 0) {
                    x0 = 1;
                    while (0 < x - x0 && grid[y][x - x0] < grid[y][x]) {
                        ++x0;
                    }
                }
                int x1 = 0;
                if (x != grid[y].length - 1) {
                    x1 = 1;
                    while (x + x1 < grid[y].length - 1 && grid[y][x + x1] < grid[y][x]) {
                        ++x1;
                    }
                }
                int y0 = 0;
                if (y != 0) {
                    y0 = 1;
                    while (0 < y - y0 && grid[y - y0][x] < grid[y][x]) {
                        ++y0;
                    }
                }
                int y1 = 0;
                if (y != grid.length - 1) {
                    y1 = 1;
                    while (y + y1 < grid.length - 1 && grid[y + y1][x] < grid[y][x]) {
                        ++y1;
                    }
                }
                int scenicScore = x0 * x1 * y0 * y1;
                maxScenicScore = Math.max(maxScenicScore, scenicScore);
            }
        }
        System.out.println("Part two: " + maxScenicScore);
    }
}