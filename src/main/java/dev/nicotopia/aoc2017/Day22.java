package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Day22 {
    public enum Status {
        CLEAN, WEAKENED, INFECTED, FLAGGED
    }

    public record Position(int x, int y) {
        public Position move(int dir) {
            return switch (dir) {
                case 0 -> new Position(this.x - 1, this.y);
                case 1 -> new Position(this.x, this.y - 1);
                case 2 -> new Position(this.x + 1, this.y);
                case 3 -> new Position(this.x, this.y + 1);
                default -> throw new RuntimeException();
            };
        }
    }

    public static void main(String[] args) throws IOException {
        Set<Position> infected = new HashSet<>();
        Map<Position, Status> stati = new HashMap<>();
        Position start;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day22.class.getResourceAsStream("/2017/day22.txt")))) {
            int y = 0;
            int width = 0;
            for (String line : br.lines().toList()) {
                width = Math.max(width, line.length());
                for (int x = 0; x < line.length(); ++x) {
                    if (line.charAt(x) == '#') {
                        infected.add(new Position(x, y));
                        stati.put(new Position(x, y), Status.INFECTED);
                    }
                }
                ++y;
            }
            start = new Position(width / 2, y / 2);
        }
        Position current = start;
        int dir = 1; // 0: left, 1: up, 2: right, 3: down
        int count = 0;
        for (int i = 0; i < 10000; ++i) {
            boolean isInfected = infected.contains(current);
            dir = (4 + dir + (isInfected ? 1 : -1)) % 4;
            if (isInfected) {
                infected.remove(current);
            } else {
                infected.add(current);
                ++count;
            }
            current = current.move(dir);
        }
        System.out.println("Part one: " + count);
        current = start;
        dir = 1;
        count = 0;
        for (int i = 0; i < 10000000; ++i) {
            Status status = stati.getOrDefault(current, Status.CLEAN);
            switch (status) {
                case CLEAN:
                    dir = dir != 0 ? dir - 1 : 3;
                    stati.put(current, Status.WEAKENED);
                    break;
                case WEAKENED:
                    stati.put(current, Status.INFECTED);
                    ++count;
                    break;
                case INFECTED:
                    dir = dir != 3 ? dir + 1 : 0;
                    stati.put(current, Status.FLAGGED);
                    break;
                case FLAGGED:
                    dir = (dir + 2) % 4;
                    stati.remove(current);
                    break;
            }
            current = current.move(dir);
        }
        System.out.println("Part two: " + count);
    }
}