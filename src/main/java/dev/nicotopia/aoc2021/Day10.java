package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day10 {
    public static void main(String args[]) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day10.class.getResourceAsStream("/2021/day10.txt")))) {
            lines = br.lines().toList();
        }
        int syntaxScore = 0;
        List<Long> autoCompleteScores = new LinkedList<>();
        for (String line : lines) {
            while (!line.equals(line = line.replaceAll("\\(\\)|\\[\\]|\\{\\}|<>", ""))) {
            }
            Matcher m = Pattern.compile("[\\)\\]\\}>]").matcher(line);
            if (m.find()) {
                syntaxScore += switch (m.group(0).charAt(0)) {
                    case ')' -> 3;
                    case ']' -> 57;
                    case '}' -> 1197;
                    case '>' -> 25137;
                    default -> throw new RuntimeException();
                };
            } else {
                long score = 0;
                for(int i = 0; i < line.length(); ++i) {
                    score = 5 * score + switch (line.charAt(line.length() - 1 - i)) {
                        case '(' -> 1;
                        case '[' -> 2;
                        case '{' -> 3;
                        case '<' -> 4;
                        default -> throw new RuntimeException();
                    };
                }
                autoCompleteScores.add(score);
            }
        }
        Collections.sort(autoCompleteScores);
        System.out.printf("Syntax score: %d, auto complete score: %d\n", syntaxScore,
                autoCompleteScores.get(autoCompleteScores.size() / 2));
    }
}