package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Day06 {
    public static void main(String[] args) throws IOException {
        byte banks[];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day06.class.getResourceAsStream("/2017/day06.txt")));
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Arrays.stream(br.readLine().split("\\s+")).mapToInt(Integer::valueOf).forEach(baos::write);
            banks = baos.toByteArray();
        }
        Map<String, Integer> statesFirstCycleCounts = new HashMap<>();
        int cycleCount = 0;
        Integer loopStartCycleCount;
        while ((loopStartCycleCount = statesFirstCycleCounts.put(new String(banks), cycleCount)) == null) {
            int maxIdx = 0;
            for (int i = 1; i < banks.length; ++i) {
                if (banks[maxIdx] < banks[i]) {
                    maxIdx = i;
                }
            }
            int c = banks[maxIdx];
            banks[maxIdx] = 0;
            while (c != 0) {
                ++banks[++maxIdx % banks.length];
                --c;
            }
            ++cycleCount;
        }
        System.out.printf("Loop start: %d, loop size: %d\n", cycleCount, cycleCount - loopStartCycleCount);
    }
}