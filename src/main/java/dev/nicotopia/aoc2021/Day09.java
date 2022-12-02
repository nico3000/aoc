package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

public class Day09 {
    private static record HeightField(int[][] heights) {
        public int getSizeX() {
            return this.heights.length == 0 ? 0 : this.heights[0].length;
        }

        public int getSizeY() {
            return this.heights.length;
        }

        public int get(int x, int y) {
            return x < 0 || y < 0 || this.getSizeX() <= x || this.getSizeY() <= y ? 9 : this.heights[y][x];
        }

        public void print(Function<Position, Boolean> marked) {
            for (int y = 0; y < this.getSizeY(); ++y) {
                for (int x = 0; x < this.getSizeX(); ++x) {
                    System.out.print((marked.apply(new Position(x, y)) ? "\u001b[31m" : "\u001b[0m") + this.get(x, y));
                }
                System.out.println();
            }
        }
    }

    private static record Position(int x, int y) {
    }

    public static void main(String args[]) throws IOException {
        HeightField hf;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day09.class.getResourceAsStream("/2021/day09.txt")))) {
            hf = new HeightField(br.lines().map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
        }
        int sum = 0;
        Queue<Set<Position>> basins = new PriorityQueue<>((left, right) -> Integer.compare(right.size(), left.size()));
        for (int y = 0; y < hf.getSizeY(); ++y) {
            for (int x = 0, v = hf.get(0, y); x < hf.getSizeX(); v = hf.get(++x, y)) {
                if (v < hf.get(x - 1, y) && v < hf.get(x + 1, y) && v < hf.get(x, y - 1) && v < hf.get(x, y + 1)) {
                    sum += v + 1;
                    basins.add(getBasin(hf, new Position(x, y)));
                }
            }
        }
        hf.print(p -> basins.stream().anyMatch(b -> b.contains(p)));
        System.out.println("Sum of risk levels: " + sum);
        System.out.printf("Product of three largest basins' sizes: %d\n",
                basins.poll().size() * basins.poll().size() * basins.poll().size());

    }

    private static Set<Position> getBasin(HeightField hf, Position lowPoint) {
        Set<Position> basin = new HashSet<>();
        Stack<Position> stack = new Stack<>();
        stack.push(lowPoint);
        while (!stack.isEmpty()) {
            Position p = stack.pop();
            if (basin.add(p)) {
                if (p.x != 0 && hf.get(p.x - 1, p.y) != 9) {
                    stack.add(new Position(p.x - 1, p.y));
                }
                if (p.y != 0 && hf.get(p.x, p.y - 1) != 9) {
                    stack.add(new Position(p.x, p.y - 1));
                }
                if (p.x != hf.getSizeX() - 1 && hf.get(p.x + 1, p.y) != 9) {
                    stack.add(new Position(p.x + 1, p.y));
                }
                if (p.y != hf.getSizeY() - 1 && hf.get(p.x, p.y + 1) != 9) {
                    stack.add(new Position(p.x, p.y + 1));
                }
            }
        }
        return basin;
    }
}