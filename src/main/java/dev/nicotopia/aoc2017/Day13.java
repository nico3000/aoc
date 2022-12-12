package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Day13 {
    public static void main(String[] args) throws IOException {
        Map<Integer, Integer> layers;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day13.class.getResourceAsStream("/2017/day13.txt")))) {
            layers = br.lines().map(l -> l.split(": ")).collect(HashMap::new,
                    (m, s) -> m.put(Integer.valueOf(s[0]), Integer.valueOf(s[1])), HashMap::putAll);
        }

        int firewallRanges[] = IntStream
                .range(0, layers.keySet().stream().mapToInt(Integer::valueOf).max().getAsInt() + 1)
                .map(i -> layers.getOrDefault(i, 0)).toArray();
        int severity = 0;
        for (int n = 0; n < firewallRanges.length; ++n) {
            if (firewallRanges[n] != 0 && n % (2 * firewallRanges[n] - 2) == 0) {
                severity += n * firewallRanges[n];
            }
        }
        System.out.println("Part one: " + severity);
        int delay = 0;
        for (;;) {
            boolean hit = false;
            for (int n = 0; !hit && n < firewallRanges.length; ++n) {
                hit = firewallRanges[n] != 0 && (n + delay) % (2 * firewallRanges[n] - 2) == 0;
            }
            if (!hit) {
                break;
            }
            ++delay;
        }
        System.out.println("Part two: " + delay);
    }
}