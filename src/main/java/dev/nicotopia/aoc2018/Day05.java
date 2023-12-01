package dev.nicotopia.aoc2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day05 {
    public static void main(String[] args) throws IOException {
        String input;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day05.class.getResourceAsStream("/2018/day05.txt")))) {
            input = br.readLine();
        }
        String reduced = reduce(input);
        System.out.println("Part one: " + reduced.length());
        int min = IntStream.rangeClosed('a', 'z')
                .map(c -> reduce(reduced.replaceAll("" + (char) c + "|" + (char) (c - 'a' + 'A'), "")).length()).min()
                .getAsInt();
        System.out.println("Part two: " + min);
    }

    private static String reduce(String input) {
        String regex = IntStream.range(0, 26)
                .mapToObj(i -> "" + (char) ('a' + i) + (char) ('A' + i) + '|' + (char) ('A' + i) + (char) ('a' + i))
                .collect(Collectors.joining("|"));
        String replaced;
        while ((replaced = input.replaceAll(regex, "")).length() != input.length()) {
            input = replaced;
        }
        return input;
    }
}