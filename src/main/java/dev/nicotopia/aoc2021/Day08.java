package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day08 {
    private static class Seg7 {
        private final List<String> allDigits = new LinkedList<>();
        private final List<String> interestingDigits = new LinkedList<>();

        public Seg7(String allDigits[], String interestingDigits[]) {
            Arrays.stream(allDigits).forEach(d -> this.allDigits.add(this.normalize(d)));
            Arrays.stream(interestingDigits).forEach(d -> this.interestingDigits.add(this.normalize(d)));
            Collections.reverse(this.interestingDigits);
        }

        private String normalize(String digit) {
            char arr[] = digit.toCharArray();
            Arrays.sort(arr);
            return new String(arr);
        }

        public Stream<String> interestingDigits() {
            return this.interestingDigits.stream();
        }

        public int solve() {
            String digits[] = new String[10];
            Iterator<String> iter = this.allDigits.iterator();
            while (iter.hasNext()) {
                String digit = iter.next();
                switch (digit.length()) {
                    case 2 -> digits[1] = digit;
                    case 3 -> digits[7] = digit;
                    case 4 -> digits[4] = digit;
                    case 7 -> digits[8] = digit;
                }
                if (digit.length() != 1 && digit.length() != 5 && digit.length() != 6) {
                    iter.remove();
                }
            }
            digits[3] = this.allDigits.stream()
                    .filter(s -> s.chars().filter(c -> digits[1].indexOf(c) == -1).count() == 3).findFirst().get();
            this.allDigits.remove(digits[3]);
            digits[9] = this.normalize((digits[3] + digits[4]).chars().distinct()
                    .collect(StringBuffer::new, (sb, c) -> sb.append((char) c), StringBuffer::append).toString());
            this.allDigits.remove(digits[9]);
            digits[2] = this.allDigits.stream()
                    .filter(s -> s.length() == 5 && s.chars().filter(c -> digits[9].indexOf(c) == -1).count() == 1)
                    .findFirst().get();
            this.allDigits.remove(digits[2]);
            digits[5] = this.allDigits.stream().filter(d -> d.length() == 5).findFirst().get();
            this.allDigits.remove(digits[5]);
            digits[6] = this.allDigits.stream()
                    .filter(d -> d.chars().filter(c -> digits[5].indexOf(c) == -1).count() == 1).findFirst().get();
            this.allDigits.remove(digits[6]);
            digits[0] = this.allDigits.get(0);
            this.allDigits.clear();
            this.allDigits.addAll(Arrays.asList(digits));
            
            int number = 0;
            int factor = 1;
            for(String d : this.interestingDigits) {
                number += this.allDigits.indexOf(d) * factor;
                factor *= 10;
            }
            return number;
        }
    }

    public static void main(String args[]) throws IOException {
        List<Seg7> displays;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day08.class.getResourceAsStream("/2021/day08.txt")))) {
            displays = br.lines().map(Pattern.compile("^([a-g ]+) \\| ([a-g ]+)$")::matcher)
                    .filter(Matcher::matches).map(m -> new Seg7(m.group(1).split("\\s"), m.group(2).split("\\s")))
                    .toList();
        }
        long uniques = displays.stream().mapToLong(
                d -> d.interestingDigits().mapToInt(String::length).filter(l -> l != 1 && l != 5 && l != 6).count())
                .sum();
        System.out.println("count of unique numbers: " + uniques);
        System.out.println("sum: " + displays.stream().mapToInt(Seg7::solve).sum());
    }
}