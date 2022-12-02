package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day06 {
    public static void main(String args[]) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day06.class.getResourceAsStream("/2016/day06.txt")))) {
            lines = br.lines().toList();
        }
        String mc = "";
        String lc = "";
        Map<Character, Integer> counts = new HashMap<>();
        for (int i = 0; i < lines.get(0).length(); ++i) {
            counts.clear();
            for (String line : lines) {
                char c = line.charAt(i);
                counts.put(c, counts.containsKey(c) ? counts.get(c) + 1 : 1);
            }
            List<Character> list = new ArrayList<>(counts.keySet());
            list.sort((left, right) -> Integer.compare(counts.get(right), counts.get(left)));
            mc += list.get(0);
            lc += list.get(list.size() - 1);
        }
        System.out.println("Most common: " + mc);
        System.out.println("Least common: " + lc);
    }
}
