package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {
    public static void main(String[] args) throws IOException {
        String molecule = null;
        Map<String, List<String>> transformations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day19.class.getResourceAsStream("/2015/day19.txt")))) {
            Pattern p = Pattern.compile("(\\w+) => (\\w+)");
            for (String l : br.lines().toList()) {
                Matcher m = p.matcher(l);
                if (m.matches()) {
                    List<String> replacements = transformations.get(m.group(1));
                    if (replacements == null) {
                        transformations.put(m.group(1), replacements = new LinkedList<>());
                    }
                    replacements.add(m.group(2));
                } else if (!l.isEmpty()) {
                    molecule = l;
                }
            }
        }
        final String fMolecule = molecule;
        Set<String> known = new HashSet<>();
        transformations.forEach((k, v) -> {
            int pos = 0;
            while ((pos = fMolecule.indexOf(k, pos)) != -1) {
                for (String repl : v) {
                    known.add(fMolecule.substring(0, pos) + repl + fMolecule.substring(pos + k.length()));
                }
                pos += k.length();
            }
        });
        System.out.println("Part one: " + known.size());
        findCollapseMinSteps(transformations, molecule, "e", 0);
        System.out.println("Part two: " + currentMin);
    }

    private static int currentMin = Integer.MAX_VALUE;

    public static void findCollapseMinSteps(Map<String, List<String>> transformations, String current, String target,
            int count) {
        if (count < currentMin) {
            if (current.equals(target)) {
                currentMin = count;
                System.out.println("New minimum: " + count);
            } else {
                transformations.forEach((key, l) -> l.forEach(repl -> {
                    int pos = 0;
                    while ((pos = current.indexOf(repl, pos)) != -1) {
                        String t = current.substring(0, pos) + key + current.substring(pos + repl.length());
                        findCollapseMinSteps(transformations, t, target, count + 1);
                        ++pos;
                    }
                }));
            }
        }
    }
}
