package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day02 {
    public static void main(String args[]) throws IOException {
        int x = 0;
        int depth = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/day02.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                int pos = line.indexOf(' ');
                int value = Integer.valueOf(line.substring(pos + 1));
                switch (line.substring(0, pos)) {
                    case "forward":
                        x += value;
                        break;
                    case "down":
                        depth += value;
                        break;
                    case "up":
                        depth -= value;
                        break;
                }
            }
        }
        System.out.printf("x=%d, depth=%d, x*depth=%d\n", x, depth, x * depth);

        x = 0;
        depth = 0;
        int aim = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/day02.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                int pos = line.indexOf(' ');
                int value = Integer.valueOf(line.substring(pos + 1));
                switch (line.substring(0, pos)) {
                    case "forward":
                        x += value;
                        depth += aim * value;
                        break;
                    case "down":
                        aim += value;
                        break;
                    case "up":
                        aim -= value;
                        break;
                }
            }
        }
        System.out.printf("x=%d, depth=%d, aim=%d, x*depth=%d\n", x, depth, aim, x * depth);
    }
}