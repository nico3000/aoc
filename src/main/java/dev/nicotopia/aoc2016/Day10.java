package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day10 {
    private static class ChipHolder {
        private static Set<Integer> alerts = new HashSet<>(Arrays.asList(17, 61));

        private final String name;
        private ChipHolder lowDest;
        private ChipHolder highDest;
        private int chip = -1;

        public ChipHolder(String name) {
            this.name = name;
        }

        public void receive(int chip) {
            if (this.chip != -1) {
                if (alerts.contains(this.chip) && alerts.contains(chip)) {
                    System.out.println("Alert: " + this.name);
                }
                this.lowDest.receive(Math.min(this.chip, chip));
                this.highDest.receive(Math.max(this.chip, chip));
                this.chip = -1;
            } else {
                if (this.name.startsWith("output")) {
                    System.out.println(this.name + " received " + chip);
                }
                this.chip = chip;
            }
        }

        public void setGivesTo(String lowDest, String highDest) {
            this.lowDest = getChipHolder(lowDest);
            this.highDest = getChipHolder(highDest);
        }
    }

    static Map<String, ChipHolder> chipHolders = new HashMap<>();

    private static ChipHolder getChipHolder(String name) {
        ChipHolder chipHolder = chipHolders.get(name);
        if (chipHolder == null) {
            chipHolders.put(name, chipHolder = new ChipHolder(name));
        }
        return chipHolder;
    }

    public static void main(String args[]) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day10.class.getResourceAsStream("/2016/day10.txt")))) {
            lines = br.lines().toList();
        }
        Pattern goesToPattern = Pattern.compile("^value (\\d+) goes to (bot \\d+)$");
        Pattern givesPattern = Pattern.compile("^(bot \\d+) gives low to (\\w+ \\d+) and high to (\\w+ \\d+)$");
        lines.stream().map(givesPattern::matcher).filter(Matcher::matches)
                .forEach(m -> getChipHolder(m.group(1)).setGivesTo(m.group(2), m.group(3)));
        lines.stream().map(goesToPattern::matcher).filter(Matcher::matches)
                .forEach(m -> getChipHolder(m.group(2)).receive(Integer.valueOf(m.group(1))));
        System.out.println(
                getChipHolder("output 0").chip * getChipHolder("output 1").chip * getChipHolder("output 2").chip);
    }
}
