package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day14 {
    public record Position(int x, int y) {
    }

    public static void main(String[] args) throws IOException {
        List<List<Position>> rocks;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day14.class.getResourceAsStream("/2022/day14.txt")))) {
            rocks = br.lines().map(l -> l.split(" -> ")).map(s -> Arrays.stream(s).map(s2 -> s2.split(","))
                    .map(s3 -> new Position(Integer.valueOf(s3[0]), Integer.valueOf(s3[1]))).toList()).toList();
        }
        System.out.println("Part one: " + run(rocks, false));
        System.out.println("Part two: " + run(rocks, true));
    }

    private static int run(List<List<Position>> rocks, boolean infiniteFloor) {
        int minX = rocks.stream().mapToInt(l -> l.stream().mapToInt(p -> p.x).min().getAsInt()).min().getAsInt();
        int maxX = rocks.stream().mapToInt(l -> l.stream().mapToInt(p -> p.x).max().getAsInt()).max().getAsInt();
        int maxY = rocks.stream().mapToInt(l -> l.stream().mapToInt(p -> p.y).max().getAsInt()).max().getAsInt();
        if (infiniteFloor) {
            maxY += 2;
            minX -= maxY; // generously chosen
            maxX += maxY; // generously chosen
        }
        char grid[][] = new char[maxY + 1][(maxX - minX) + 1];
        Arrays.stream(grid).forEach(r -> Arrays.fill(r, '.'));
        if (infiniteFloor) {
            Arrays.fill(grid[grid.length - 1], '#');
        }
        for (List<Position> rockLine : rocks) {
            for (int i = 1; i < rockLine.size(); ++i) {
                Position start = rockLine.get(i - 1);
                Position delta = new Position(rockLine.get(i).x - start.x, rockLine.get(i).y - start.y);
                int segLen = Math.abs(delta.x == 0 ? delta.y : delta.x);
                delta = new Position(delta.x / segLen, delta.y / segLen);
                for (int j = 0; j <= segLen; ++j) {
                    grid[start.y + j * delta.y][start.x + j * delta.x - minX] = '#';
                }
            }
        }
        Position source = new Position(500, 0);
        grid[source.y][source.x - minX] = '+';
        int count = 0;
        boolean done = false;
        while (!done) {
            int x = source.x - minX;
            for (int y = source.y; y < grid.length && !done; ++y) {
                if (y == grid.length - 1) {
                    done = true;
                } else if (grid[y + 1][x] != '.') {
                    if (x == 0) {
                        done = true;
                    } else if (grid[y + 1][x - 1] == '.') {
                        --x;
                    } else if (x == grid[y].length - 1) {
                        done = true;
                    } else if (grid[y + 1][x + 1] == '.') {
                        ++x;
                    } else if (grid[y][x] == '+') {
                        ++count;
                        done = true;
                    } else {
                        grid[y][x] = 'o';
                        ++count;
                        break;
                    }
                }
            }
        }
        // Arrays.stream(grid).forEach(r -> System.out.println(new String(r)));
        return count;
    }
}