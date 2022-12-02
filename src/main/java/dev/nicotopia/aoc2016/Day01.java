package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Day01 {
    private static class Position {
        private int x = 0;
        private int y = 0;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x + y;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Position p && this.x == p.x && this.y == p.y;
        }
    }

    public static void main(String args[]) throws IOException {
        String line;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2016/day01.txt")))) {
            line = br.readLine();
        }
        String split[] = line.split(", ");

        int x = 0;
        int y = 0;
        int head = 0;
        boolean found = false;
        Set<Position> visited = new HashSet<>();
        for (String s : split) {
            switch (s.charAt(0)) {
                case 'L':
                    ++head;
                    break;
                case 'R':
                    head += 3;
                    break;
            }
            int distance = Integer.valueOf(s.substring(1));
            for (int i = 0; i < distance; ++i) {
                switch (head % 4) {
                    case 0:
                        y += 1;
                        break;
                    case 1:
                        x -= 1;
                        break;
                    case 2:
                        y -= 1;
                        break;
                    case 3:
                        x += 1;
                        break;
                }
                Position pos = new Position(x, y);
                if (!found && !visited.add(pos)) {
                    System.out.printf("First visited twice: %d\n", pos.x + pos.y);
                    found = true;
                }
            }
        }
        System.out.println(x + y);
    }
}
