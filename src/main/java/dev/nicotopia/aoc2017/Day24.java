package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Day24 {
    private interface BridgeConsumer {
        public void onBridgeDone(int length, int strength);
    }

    public record Component(int portA, int portB) {
    }

    public static void main(String[] args) throws IOException {
        Set<Component> components;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day24.class.getResourceAsStream("/2017/day24.txt")))) {
            components = new HashSet<>(br.lines().map(l -> l.split("/"))
                    .map(s -> new Component(Integer.valueOf(s[0]), Integer.valueOf(s[1]))).toList());
        }
        System.out.println("Part one: " + findMaxStrength(components, 0, 0, 0, (l, s) -> {
            if (Day24.longest < l) {
                Day24.longestStrength = 0;
            }
            Day24.longest = Math.max(Day24.longest, l);
            if (Day24.longest == l) {
                Day24.longestStrength = Math.max(Day24.longestStrength, s);
            }
        }));
        System.out.println("Part two: " + Day24.longestStrength);
    }

    private static int longest = 0;
    private static int longestStrength = 0;

    public static int findMaxStrength(Set<Component> comps, int portType, int length, int strength,
            BridgeConsumer bridgeConsumer) {
        bridgeConsumer.onBridgeDone(length, strength);
        return comps.stream().filter(c -> c.portA == portType || c.portB == portType).toList().stream().mapToInt(c -> {
            comps.remove(c);
            int s = findMaxStrength(comps, c.portA == portType ? c.portB : c.portA, length + 1,
                    strength + c.portA + c.portB, bridgeConsumer);
            comps.add(c);
            return s;
        }).max().orElse(strength);
    }
}