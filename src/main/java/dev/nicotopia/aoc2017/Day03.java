package dev.nicotopia.aoc2017;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Day03 {
    private record Position(int x, int y) {
    }

    public static void main(String[] args) {
        int input = 368078;
        int x = 0;
        int y = 0;
        int pos = 1;
        for (int i = 0; pos != input; ++i) {
            int steps = Math.min((i + 2) / 2, input - pos);
            pos += steps;
            x += switch (i % 4) {
                case 0 -> steps;
                case 2 -> -steps;
                default -> 0;
            };
            y += switch (i % 4) {
                case 1 -> -steps;
                case 3 -> steps;
                default -> 0;
            };
        }
        System.out.println("Part one: " + (Math.abs(x) + Math.abs(y)));
        x = 0;
        y = 0;
        Map<Position, Integer> mem = new HashMap<>();
        mem.put(new Position(0, 0), 1);
        int lastValue = 1;
        for (int i = 0; lastValue <= input; ++i) {
            int steps = (i + 2) / 2;
            for (int j = 0; j < steps && lastValue <= input; ++j) {
                x += switch (i % 4) {
                    case 0 -> 1;
                    case 2 -> -1;
                    default -> 0;
                };
                y += switch (i % 4) {
                    case 1 -> -1;
                    case 3 -> 1;
                    default -> 0;
                };
                lastValue = Arrays
                        .asList(new Position(x - 1, y - 1), new Position(x, y - 1), new Position(x + 1, y - 1),
                                new Position(x - 1, y), new Position(x + 1, y), new Position(x - 1, y + 1),
                                new Position(x, y + 1), new Position(x + 1, y + 1))
                        .stream().mapToInt(p -> mem.containsKey(p) ? mem.get(p) : 0).sum();
                mem.put(new Position(x, y), lastValue);
            }
        }
        System.out.println("Part two: " + lastValue);
    }
}