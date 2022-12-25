package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {
    public static void main(String[] args) throws IOException {
        Map<String, Map<String, Integer>> happinessChanges = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day13.class.getResourceAsStream("/2015/day13.txt")))) {
            Pattern p = Pattern.compile("(\\w+) would (gain|lose) (\\d+) happiness units by sitting next to (\\w+).");
            br.lines().map(p::matcher).filter(Matcher::matches).forEach(m -> {
                Map<String, Integer> map = happinessChanges.get(m.group(1));
                if (map == null) {
                    happinessChanges.put(m.group(1), map = new HashMap<>());
                }
                map.put(m.group(4), (m.group(2).equals("gain") ? 1 : -1) * Integer.valueOf(m.group(3)));
            });
        }
        String table[] = new String[happinessChanges.size()];
        table[0] = happinessChanges.keySet().stream().findAny().get();
        Set<String> remaining = new HashSet<>(
                happinessChanges.keySet().stream().filter(s -> !s.equals(table[0])).toList());
        System.out.println("Part one: " + findMaxHappiness(happinessChanges, table, 1, remaining));
        happinessChanges.put("nico3000", Collections.emptyMap());
        remaining.add("nico3000");
        String table2[] = new String[happinessChanges.size()];
        table2[0] = table[0];
        System.out.println("Part two: " + findMaxHappiness(happinessChanges, table2, 1, remaining));
    }

    public static int findMaxHappiness(Map<String, Map<String, Integer>> happinessChanges, String table[], int pos,
            Set<String> remaining) {
        int max = 0;
        for (String person : new LinkedList<>(remaining)) {
            table[pos] = person;
            String left = table[pos - 1];
            int change = happinessChanges.get(left).getOrDefault(person, 0)
                    + happinessChanges.get(person).getOrDefault(left, 0);
            if (remaining.size() != 1) {
                remaining.remove(person);
                change += findMaxHappiness(happinessChanges, table, pos + 1, remaining);
                remaining.add(person);
            } else {
                String right = table[0];
                change += happinessChanges.get(right).getOrDefault(person, 0)
                        + happinessChanges.get(person).getOrDefault(right, 0);
            }
            max = Math.max(max, change);
        }
        return max;
    }
}