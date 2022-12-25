package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day01 {
    public static void main(String[] args) throws IOException {
        String input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2015/day01.txt")))) {
            input = br.readLine();
        }
        System.out.println("Part one: " + (2 * input.chars().filter(c -> c == '(').count() - input.length()));
        int lvl = 0;
        int i = 0;
        while (lvl != -1) {
            lvl += input.charAt(i++) == '(' ? 1 : -1;
        }
        System.out.println("Part two: " + i);
    }
}
