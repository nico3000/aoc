package dev.nicotopia.aoc2015;

public class Day25 {
    public static void main(String[] args) {
        int row = 3010;
        int col = 3019;
        int idx = 1 + ((col + row) * (col + row - 1) / 2) + ((row - 1) - (row + 1)) * row / 2;
        long v = 20151125L;
        for (int i = 1; i < idx; ++i) {
            v = (252533L * v) % 33554393L;
        }
        System.out.println("Part one: " + v);
    }
}