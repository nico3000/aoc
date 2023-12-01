package dev.nicotopia.aoc2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Day02 {
    public static void main(String[] args) throws IOException {
        List<String> boxes;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day02.class.getResourceAsStream("/2018/day02.txt")))) {
            boxes = br.lines().toList();
        }
        long twoLetterCount = boxes.stream()
                .filter(s -> IntStream.rangeClosed('a', 'z').anyMatch(c -> count(s, (char) c) == 2)).count();
        long threeLetterCount = boxes.stream()
                .filter(s -> IntStream.rangeClosed('a', 'z').anyMatch(c -> count(s, (char) c) == 3)).count();
        System.out.println("Part one: " + twoLetterCount * threeLetterCount);
        String partTwo = boxes.stream()
                .map(s -> boxes.stream().map(s2 -> getMatchIfClose(s, s2)).filter(m -> m != null).toList())
                .collect(LinkedList<String>::new, LinkedList<String>::addAll, LinkedList<String>::addAll).get(0);
        System.out.println("Part two: " + partTwo);
    }

    public static int count(String src, char c) {
        return (int) src.chars().filter(_c -> _c == c).count();
    }

    public static String getMatchIfClose(String a, String b) {
        List<Integer> notMatchIndices = a.length() == b.length()
                ? IntStream.range(0, a.length()).filter(i -> a.charAt(i) != b.charAt(i)).boxed().toList()
                : Collections.emptyList();
        return notMatchIndices.size() != 1 ? null
                : a.substring(0, notMatchIndices.get(0)) + a.substring(notMatchIndices.get(0) + 1);
    }
}
