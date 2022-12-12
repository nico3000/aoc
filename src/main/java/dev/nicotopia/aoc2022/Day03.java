package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.IntStream;

public class Day03 {
    public static void main(String[] args) throws IOException {
        List<String> rucksacks;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day03.class.getResourceAsStream("/2022/day03.txt")))) {
            rucksacks = br.lines().toList();
        }
        int sum = 0;
        for (IntStream rucksack[] : rucksacks.stream().map(
                l -> new IntStream[] { l.substring(0, l.length() / 2).chars(), l.substring(l.length() / 2).chars() })
                .toList()) {
            int dupl = rucksack[0].filter(rucksack[1].boxed().toList()::contains).findAny().getAsInt();
            sum += 1 + ('a' <= dupl && dupl <= 'z' ? dupl - 'a' : 26 + dupl - 'A');
        }
        System.out.println("Sum of wrong object priorities: " + sum);
        sum = 0;
        for (int i = 0; i < rucksacks.size(); i += 3) {
            List<Integer> rucksack1 = rucksacks.get(i + 1).chars().boxed().toList();
            List<Integer> rucksack2 = rucksacks.get(i + 2).chars().boxed().toList();
            int badge = rucksacks.get(i).chars().filter(c -> rucksack1.contains(c) && rucksack2.contains(c)).findAny()
                    .getAsInt();
            sum += 1 + ('a' <= badge && badge <= 'z' ? badge - 'a' : 26 + badge - 'A');
        }
        System.out.println("Sum of badge priorities: " + sum);
    }
}