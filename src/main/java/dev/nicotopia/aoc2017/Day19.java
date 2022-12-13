package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day19 {
    public static void main(String[] args) throws IOException {
        char arena[][];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day19.class.getResourceAsStream("/2017/day19.txt")))) {
            arena = br.lines().map(l -> l.toCharArray()).toArray(char[][]::new);
        }
        StringBuilder sb = new StringBuilder();
        int x = new String(arena[0]).indexOf('|');
        int y = 0;
        int dir = 3; // 0: left, 1: up, 2: right, 3: down
        int count = 0;
        while (arena[y][x] != ' ') {
            if (arena[y][x] == '+') {
                if (dir == 0 || dir == 2) {
                    if (y != 0 && arena[y - 1][x] != ' ') {
                        --y;
                        dir = 1;
                    } else {
                        ++y;
                        dir = 3;
                    }
                } else {
                    if (x != 0 && arena[y][x - 1] != ' ') {
                        --x;
                        dir = 0;
                    } else {
                        ++x;
                        dir = 2;
                    }
                }
            } else {
                if ('A' <= arena[y][x] && arena[y][x] <= 'Z') {
                    sb.append(arena[y][x]);
                }
                switch (dir) {
                    case 0 -> --x;
                    case 1 -> --y;
                    case 2 -> ++x;
                    case 3 -> ++y;
                }
            }
            ++count;
        }
        System.out.printf("Part one: %s, part two: %d\n", sb.toString(), count);
    }
}