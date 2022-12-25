package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Day20 {
    public static void main(String[] args) throws IOException {
        List<Long> original;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day20.class.getResourceAsStream("/2022/day20.txt")))) {
            original = br.lines().map(Long::valueOf).toList();
        }
        System.out.println("Part one: " + mix(original, 1));
        long decryptKey = 811589153L;
        original = original.stream().map(v -> (long) v * decryptKey).toList();
        System.out.println("Part two: " + mix(original, 10));
    }

    public static long mix(List<Long> original, int count) {
        List<Integer> l = new LinkedList<>(IntStream.range(0, original.size()).boxed().toList());
        for (int c = 0; c < count; ++c) {
            for (int i = 0; i < original.size(); ++i) {
                long v = original.get(i);
                if (v != 0) {
                    int idxOfI = l.indexOf(i);
                    int newIdxOfI = (int) ((idxOfI + v) % (l.size() - 1) + l.size() - 1) % (l.size() - 1);
                    if (newIdxOfI == 0 && v < 0) {
                        newIdxOfI = l.size() - 1;
                    }
                    l.add(newIdxOfI, l.remove(idxOfI));
                }
            }
        }
        int idxOfZero = l.indexOf(original.indexOf(0L));
        long v1000 = original.get(l.get((idxOfZero + 1000) % l.size()));
        long v2000 = original.get(l.get((idxOfZero + 2000) % l.size()));
        long v3000 = original.get(l.get((idxOfZero + 3000) % l.size()));
        return v1000 + v2000 + v3000;
    }
}