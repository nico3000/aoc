package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day02 {
    public static void main(String[] args) throws IOException {
        List<List<Integer>> input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/2017/day02.txt")))) {
            input = br.lines().map(l -> Arrays.asList(l.split("\\s+")).stream().map(Integer::valueOf).toList())
                    .toList();
        }
        System.out.println(
                "Part one: " + input.stream().mapToInt(s -> s.stream().mapToInt(Integer::valueOf).max().getAsInt()
                        - s.stream().mapToInt(Integer::valueOf).min().getAsInt()).sum());
        int sum = 0;
        for (List<Integer> line : input) {
            for (int i = 0; i < line.size(); ++i) {
                for (int j = i + 1; j < line.size(); ++j) {
                    int min = Math.min(line.get(i), line.get(j));
                    int max = Math.max(line.get(i), line.get(j));
                    if (max % min == 0) {
                        sum += max / min;
                    }
                }
            }
        }
        System.out.println("Part two: " + sum);
    }
}