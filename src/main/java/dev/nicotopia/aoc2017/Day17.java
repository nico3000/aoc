package dev.nicotopia.aoc2017;

import java.util.ArrayList;
import java.util.List;

public class Day17 {
    public static void main(String[] args) {
        int input = 356;
        List<Integer> buffer = new ArrayList<>();
        buffer.add(0);
        int current = 0;
        for (int i = 1; i <= 2017; ++i) {
            current = (current + input) % buffer.size();
            buffer.add(++current, i);
            if (i == 2017) {
            }
            if (i % 100000 == 0) {
                System.out.println(i);
            }
        }
        System.out.println("Part one: " + buffer.get((current + 1) % buffer.size()));
        current = 0;
        int size = 1;
        int afterZero = 0;
        for (int i = 1; i <= 50000000; ++i) {
            current = (current + input) % size;
            if (current++ == 0) {
                afterZero = i;
            }
            ++size;
        }
        System.out.println("Part two: " + afterZero);
    }
}
