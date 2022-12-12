package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Day01 {
    public static void main(String[] args) throws Exception {
        String input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2017/day01.txt")))) {
            input = br.readLine();
        }
        int sumPartOne = 0;
        int sumPartTwo = 0;
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == input.charAt((i + 1) % input.length())) {
                sumPartOne += input.charAt(i) - '0';
            }
            if (input.charAt(i) == input.charAt((i + input.length() / 2) % input.length())) {
                sumPartTwo += input.charAt(i) - '0';
            }
        }
        System.out.printf("Part one: %d, part two: %d\n", sumPartOne, sumPartTwo);
    }
}