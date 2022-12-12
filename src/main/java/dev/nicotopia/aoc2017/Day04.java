package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day04 {
    public static void main(String[] args) throws IOException {
        List<List<String>> passPhrases;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day04.class.getResourceAsStream("/2017/day04.txt")))) {
            passPhrases = br.lines().map(l -> Arrays.asList(l.split("\\s+"))).toList();
        }
        long validCount = passPhrases.stream().filter(l -> l.stream().distinct().count() == l.size()).count();
        System.out.println("Part one: " + validCount);
        validCount = passPhrases.stream().filter(l -> l.stream()
                .map(s -> new String(s.chars().sorted().toArray(), 0, s.length())).distinct().count() == l.size())
                .count();
        System.out.println("Part two: " + validCount);
    }
}