package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Day02 {
    public static void main(String args[]) throws IOException {
        List<String> lines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/2016/day02.txt")))) {
            br.lines().forEach(lines::add);
        }
        int x = 1;
        int y = 1;
        for(String line : lines) {
            for(char c : line.toCharArray()) {
                switch(c) {
                    case 'U' -> y = Math.max(y - 1, 0);
                    case 'R' -> x = Math.min(x + 1, 2);
                    case 'D' -> y = Math.min(y + 1, 2);
                    case 'L' -> x = Math.max(x - 1, 0);
                }
            }
            System.out.print(1 + 3 * y + x);
        }
        System.out.println();

        x = 0;
        y = 0;
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                switch (c) {
                    case 'U' -> y = Math.abs(x) + Math.abs(y - 1) < 3 ? y - 1 : y;
                    case 'R' -> x = Math.abs(x + 1) + Math.abs(y) < 3 ? x + 1 : x;
                    case 'D' -> y = Math.abs(x) + Math.abs(y + 1) < 3 ? y + 1 : y;
                    case 'L' -> x = Math.abs(x - 1) + Math.abs(y) < 3 ? x - 1 : x;
                }
            }
            System.out.printf("%d/%d ", x, y);
        }
    }
}
