package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Day01 {
    public static void main(String[] args) throws IOException {
        List<Integer> elves = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day01.class.getResourceAsStream("/2022/day01.txt")))) {
            int sum = 0;
            for (Integer v : br.lines().map(l -> l.isBlank() ? null : Integer.valueOf(l)).toList()) {
                if (v == null) {
                    elves.add(sum);
                    sum = 0;
                } else {
                    sum += v;
                }
            }
            elves.add(sum);
        }
        Collections.sort(elves);
        System.out.println("Max: " + elves.get(elves.size() - 1));
        System.out.println("Sum top 3: "
                + (elves.get(elves.size() - 1) + elves.get(elves.size() - 2) + elves.get(elves.size() - 3)));
    }
}