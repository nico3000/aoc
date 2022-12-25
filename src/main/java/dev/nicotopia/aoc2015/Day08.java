package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day08 {
    public static void main(String[] args) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day08.class.getResourceAsStream("/2015/day08.txt")))) {
            lines = br.lines().toList();
        }
        int diff1 = lines.stream()
                .mapToInt(l -> l.length() - l.replaceAll("\\\\\"|\\\\\\\\|\\\\x[0-9a-f]{2}", ".").length() + 2).sum();
        System.out.println("Part one: " + diff1);
        int diff2 = lines.stream()
                .mapToInt(l -> l.replace("\\", "\\\\").replace("\"", "\\\"").length() + 2 - l.length()).sum();
        System.out.println("Part two: " + diff2);
    }
}