package dev.nicotopia.aoc2016;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

public class Day13 {
    private static final int luckyNumber = 1362;

    private static record Position(int x, int y) {
    }

    private static class Office {
        private int plan[][];
        private int shortestPaths[][];
        private Queue<Position> queue = new LinkedList<>();

        public Office(int width, int height) {
            this.plan = new int[width][height];
            this.shortestPaths = new int[width][height];
        }

        public int getTile(int x, int y) {
            if (this.plan[y][x] == 0) {
                this.plan[y][x] = 1 + Integer.bitCount(x * x + 3 * x + 2 * x * y + y + y * y + luckyNumber) % 2;
            }
            return this.plan[y][x];
        }

        public boolean isWall(int x, int y) {
            return this.getTile(x, y) == 2;
        }

        public int getShortestPathLength(int fromX, int fromY, int toX, int toY) {
            Arrays.stream(this.shortestPaths).forEach(r -> Arrays.fill(r, Integer.MAX_VALUE));
            this.shortestPaths[fromY][fromX] = 0;
            this.queue.add(new Position(fromX, fromY));
            while (!this.queue.isEmpty()) {
                Position p = this.queue.poll();
                this.neighborChanged(p.x + 1, p.y, this.shortestPaths[p.y][p.x]);
                this.neighborChanged(p.x, p.y + 1, this.shortestPaths[p.y][p.x]);
                this.neighborChanged(p.x - 1, p.y, this.shortestPaths[p.y][p.x]);
                this.neighborChanged(p.x, p.y - 1, this.shortestPaths[p.y][p.x]);
            }
            return this.shortestPaths[toY][toX];
        }

        public long count(int maxSteps) {
            List<Integer> tiles = Arrays.stream(this.shortestPaths).map(r -> IntStream.of(r).boxed().toList())
                    .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
            return tiles.stream().filter(i -> i <= 50).count();
        }

        private void neighborChanged(int x, int y, int neighborShortestPath) {
            if (0 <= x && x < this.plan[0].length && 0 <= y && y < this.plan.length && !this.isWall(x, y)
                    && neighborShortestPath + 1 < this.shortestPaths[y][x]) {
                this.shortestPaths[y][x] = neighborShortestPath + 1;
                this.queue.offer(new Position(x, y));
            }
        }

        public void print(Position... marks) {
            for (int y = 0; y < this.plan.length; ++y) {
                for (int x = 0; x < this.plan[0].length; ++x) {
                    boolean marked = false;
                    for (Position p : marks) {
                        marked |= p.x == x && p.y == y;
                    }
                    boolean wall = this.isWall(x, y);
                    System.out.print(marked ? (wall ? 'X' : 'O') : (wall ? '#' : '.'));
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void main(String args[]) {
        Office office = new Office(50, 50);
        office.print(new Position(1, 1), new Position(31, 39));
        System.out.println("Shortest path length: " + office.getShortestPathLength(1, 1, 31, 39));
        System.out.println("Number of reachable tiles with 50 or less steps: " + office.count(50));
    }
}
