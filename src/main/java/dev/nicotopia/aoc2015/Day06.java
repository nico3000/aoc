package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day06 {
    public static void main(String[] args) throws IOException {
        List<String> commands;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day06.class.getResourceAsStream("/2015/day06.txt")))) {
            commands = br.lines().toList();
        }
        System.out.println("Part one: " + execute(commands, (cmd, old) -> switch (cmd) {
            case "toggle" -> 1 - old;
            case "turn on" -> 1;
            case "turn off" -> 0;
            default -> throw new RuntimeException();
        }));
        System.out.println("Part two: " + execute(commands, (cmd, old) -> switch (cmd) {
            case "toggle" -> old + 2;
            case "turn on" -> old + 1;
            case "turn off" -> Math.max(0, old - 1);
            default -> throw new RuntimeException();
        }));
    }

    private static int execute(List<String> commands, BiFunction<String, Integer, Integer> changeFn) {
        int grid[][] = new int[1000][1000];
        Pattern p = Pattern.compile("^(turn off|turn on|toggle) ([0-9]+),([0-9]+) through ([0-9]+),([0-9]+)$");
        commands.stream().map(p::matcher).filter(Matcher::matches).forEach(m -> {
            int fromX = Integer.valueOf(m.group(2));
            int fromY = Integer.valueOf(m.group(3));
            int toX = Integer.valueOf(m.group(4));
            int toY = Integer.valueOf(m.group(5));
            for (int y = fromY; y <= toY; ++y) {
                for (int x = fromX; x <= toX; ++x) {
                    grid[y][x] = changeFn.apply(m.group(1), grid[y][x]);
                }
            }
        });
        return Arrays.stream(grid).mapToInt(r -> Arrays.stream(r).sum()).sum();
    }
}