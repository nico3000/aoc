package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day03 {
    public record Position(int x, int y) {
    }

    public static void main(String[] args) throws IOException {
        String line;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day03.class.getResourceAsStream("/2015/day03.txt")))) {
            line = br.readLine();
        }
        int x = 0;
        int y = 0;
        Set<Position> visited = new HashSet<>(Arrays.asList(new Position(x, y)));
        for (char c : line.toCharArray()) {
            x += c == '<' ? -1 : c == '>' ? 1 : 0;
            y += c == '^' ? -1 : c == 'v' ? 1 : 0;
            visited.add(new Position(x, y));
        }
        System.out.println("Part one: " + visited.size());
        x = 0;
        y = 0;
        int roboX = 0;
        int roboY = 0;
        visited = new HashSet<>(Arrays.asList(new Position(x, y)));
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (i % 2 == 0) {
                x += c == '<' ? -1 : c == '>' ? 1 : 0;
                y += c == '^' ? -1 : c == 'v' ? 1 : 0;
                visited.add(new Position(x, y));
            } else {
                roboX += c == '<' ? -1 : c == '>' ? 1 : 0;
                roboY += c == '^' ? -1 : c == 'v' ? 1 : 0;
                visited.add(new Position(roboX, roboY));
            }
        }
        System.out.println("Part two: " + visited.size());
    }
}
