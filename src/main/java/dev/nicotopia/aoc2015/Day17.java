package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class Day17 {
    public static void main(String[] args) throws IOException {
        int buckets[];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day17.class.getResourceAsStream("/2015/day17.txt")))) {
            buckets = br.lines().mapToInt(Integer::valueOf).toArray();
        }
        Map<Integer, Integer> counts = new HashMap<>();
        generate(buckets, 0, 0, 150, c -> counts.put(c, counts.getOrDefault(c, 0) + 1));
        System.out.println("Part one: " + counts.values().stream().mapToInt(Integer::valueOf).sum());
        System.out.println("Part two: " + counts.get(counts.keySet().stream().mapToInt(i -> i).min().getAsInt()));
    }

    public static void generate(int buckets[], int selected, int pos, int remainingVolume, IntConsumer consumer) {
        if (pos != buckets.length) {
            if (buckets[pos] <= remainingVolume) {
                generate(buckets, selected + 1, pos + 1, remainingVolume - buckets[pos], consumer);
            }
            generate(buckets, selected, pos + 1, remainingVolume, consumer);
        } else if (remainingVolume == 0) {
            consumer.accept(selected);
        }
    }
}