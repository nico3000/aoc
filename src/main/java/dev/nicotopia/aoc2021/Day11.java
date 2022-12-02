package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day11 {
    private static class OctopusGrid {
        private final int[][] octopuses;
        private int flashCount = 0;

        public OctopusGrid(int[][] octopuses) {
            this.octopuses = octopuses;
        }

        public int getSizeX() {
            return this.octopuses[0].length;
        }

        public int getSizeY() {
            return this.octopuses.length;
        }

        public void increase(int x, int y) {
            if (0 <= x && x < this.getSizeX() && 0 <= y && y < this.getSizeY() && ++this.octopuses[y][x] == 10) {
                ++this.flashCount;
                for (int i = 0; i < 8; ++i) {
                    this.increase(x - 1 + (i + i / 4) % 3, y - 1 + (i + i / 4) / 3);
                }
            }
        }

        public int getAndResetFlashedOctpusses() {
            int count = 0;
            for (int y = 0; y < this.getSizeY(); ++y) {
                for (int x = 0; x < this.getSizeX(); ++x) {
                    if (9 < this.octopuses[y][x]) {
                        this.octopuses[y][x] = 0;
                        ++count;
                    }
                }
            }
            return count;
        }

        public void print() {
            for(int y = 0; y < this.getSizeY(); ++y) {
                for(int x = 0; x < this.getSizeX(); ++x) {
                    System.out.print(this.octopuses[y][x]);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void main(String args[]) throws IOException {
        OctopusGrid grid;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day11.class.getResourceAsStream("/2021/day11schmae.txt")))) {
            grid = new OctopusGrid(br.lines().map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
        }
        grid.print();
        boolean allFlashed = false;
        for (int step = 0; step < 100 || !allFlashed; ++step) {
            for (int y = 0; y < grid.getSizeY(); ++y) {
                for (int x = 0; x < grid.getSizeX(); ++x) {
                    grid.increase(x, y);
                }
            }
            if(grid.getAndResetFlashedOctpusses() == grid.getSizeX() * grid.getSizeY()) {
                System.out.printf("Step %d: all flashed at once.\n", step + 1);
                allFlashed = true;
            }
            if(step == 99) {
                System.out.println("Flash count: " + grid.flashCount);
            }
            grid.print();
        }
    }
}