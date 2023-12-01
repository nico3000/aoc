package dev.nicotopia.aoc2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day01 {
    public static void main(String[] args) throws IOException {
        List<Integer> values;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2018/day01.txt")))) {
            values = br.lines().map(Integer::valueOf).toList();
        }
        System.out.println("Part one: " + values.stream().mapToInt(i -> i).sum());
        Set<Integer> frequencies = new HashSet<>();
        int f = 0;
        for (int i = 0; !frequencies.contains(f); ++i) {
            frequencies.add(f);
            f += values.get(i % values.size());
        }
        System.out.println("Part two: " + f);
    }
}
