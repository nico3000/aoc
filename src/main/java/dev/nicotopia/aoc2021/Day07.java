package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Day07 {
    public static void main(String args[]) throws IOException {
        List<Integer> crabs = Arrays.asList(16, 1, 2, 0, 4, 2, 7, 1, 2, 14);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day07.class.getResourceAsStream("/2021/day07.txt")))) {
            crabs = new ArrayList<>(
                    Arrays.stream(br.readLine().split(",")).mapToInt(Integer::valueOf).boxed().toList());
        }
        Collections.sort(crabs);
        int median = crabs.size() % 2 == 0 ? (crabs.get(crabs.size() / 2 - 1) + crabs.get(crabs.size() / 2)) / 2
                : crabs.get(crabs.size() / 2);
        int avg = (int) (crabs.stream().mapToInt(Integer::valueOf).sum() / (float) crabs.size());
        int simpleDistanceSum = 0;
        int complexDistanceSum = 0;
        for (int c : crabs) {
            int distanceToAvg = Math.abs(avg - c);
            simpleDistanceSum += Math.abs(median - c);
            complexDistanceSum += distanceToAvg * (distanceToAvg + 1);
        }
        System.out.printf("simple fuel for pos %d: %d\n", median, simpleDistanceSum);
        System.out.printf("complex fuel for pos %d: %d\n", avg, complexDistanceSum / 2);
    }
}