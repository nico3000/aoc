package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Day15 {
    private static class Grid {
        private final int risks[][];
        private final int lowestRisks[][];
        private final int[][] queue;
        private int queueFront;
        private int queueBack;

        public Grid(int risks[][]) {
            this.risks = risks;
            this.lowestRisks = new int[risks.length][risks[0].length];
            this.queue = new int[this.risks.length * this.risks[0].length][2];
        }

        public int getSizeX() {
            return this.risks[0].length;
        }

        public int getSizeY() {
            return this.risks.length;
        }

        public int getLowestRisk(int fromX, int fromY, int toX, int toY) {
            Arrays.stream(this.lowestRisks).forEach(row -> Arrays.fill(row, Integer.MAX_VALUE));
            this.lowestRisks[fromY][fromX] = 0;
            this.queueFront = 0;
            this.queue[0][0] = fromX;
            this.queue[0][1] = fromY;
            this.queueBack = 1;
            while (this.queueFront != this.queueBack) {
                int x = this.queue[this.queueFront % this.queue.length][0];
                int y = this.queue[this.queueFront % this.queue.length][1];
                ++this.queueFront;
                this.update(x + 1, y, this.lowestRisks[y][x]);
                this.update(x, y + 1, this.lowestRisks[y][x]);
                this.update(x, y - 1, this.lowestRisks[y][x]);
                this.update(x - 1, y, this.lowestRisks[y][x]);
            }
            return this.lowestRisks[toY][toX];
        }

        private void update(int x, int y, int newNeighborRisk) {
            if (0 <= x && x < this.getSizeX() && 0 <= y && y < this.getSizeY()
                    && newNeighborRisk + this.risks[y][x] < this.lowestRisks[y][x]) {
                this.lowestRisks[y][x] = newNeighborRisk + this.risks[y][x];
                this.queue[this.queueBack % this.queue.length][0] = x;
                this.queue[this.queueBack % this.queue.length][1] = y;
                ++this.queueBack;
            }
        }

        public Grid enlarge(int count) {
            int risks[][] = new int[count * this.getSizeY()][count * getSizeX()];
            for (int gy = 0; gy < count; ++gy) {
                for (int gx = 0; gx < count; ++gx) {
                    int offsetX = gx * this.getSizeX();
                    int offsetY = gy * this.getSizeY();
                    int delta = gx + gy;
                    for (int y = 0; y < this.getSizeY(); ++y) {
                        for (int x = 0; x < this.getSizeX(); ++x) {
                            risks[offsetY + y][offsetX + x] = 1 + (this.risks[y][x] + delta - 1) % 9;
                        }
                    }
                }
            }
            return new Grid(risks);
        }
    }

    public static void main(String args[]) throws IOException {
        Grid gridPartOne;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day15.class.getResourceAsStream("/2021/day15.txt")))) {
            gridPartOne = new Grid(br.lines().map(l -> l.chars().map(c -> c - '0').toArray()).toArray(int[][]::new));
        }
        Grid gridPartTwo = gridPartOne.enlarge(5);
        long beg = System.nanoTime();
        int partOne = gridPartOne.getLowestRisk(0, 0, gridPartOne.getSizeX() - 1, gridPartOne.getSizeY() - 1);
        int partTwo = gridPartTwo.getLowestRisk(0, 0, gridPartTwo.getSizeX() - 1, gridPartTwo.getSizeY() - 1);
        float ms = 1e-6f * (float) (System.nanoTime() - beg);
        System.out.printf("lowest risks; part one: %d, part two: %d, elapsed time: %.3f ms.\n", partOne, partTwo, ms);
    }
}