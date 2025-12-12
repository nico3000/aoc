package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.algebra.IntInterval;
import dev.nicotopia.aoc.algebra.IntervalSet;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day15 {

    public static void main(String[] args) throws IOException {
        Map<Vec2i, Vec2i> sensors = new HashMap<>();
        int partOneY = 2000000;
        int partTwoMax = 4000000;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day15.class.getResourceAsStream("/2022/day15.txt")))) {
            Pattern p = Pattern
                    .compile("Sensor at x=(-?[0-9]+), y=(-?[0-9]+): closest beacon is at x=(-?[0-9]+), y=(-?[0-9]+)");
            br.lines().map(p::matcher).filter(Matcher::matches)
                    .forEach(m -> sensors.put(new Vec2i(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))),
                            new Vec2i(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)))));
        }
        Set<Integer> partOneSet = new HashSet<>();
        collectNoBeaconIntervals(sensors, partOneY, i -> partOneSet.addAll(i.stream().boxed().toList()));
        partOneSet.removeAll(sensors.values().stream().filter(b -> b.y() == partOneY).map(b -> b.x()).toList());
        System.out.println("Part one: " + partOneSet.size());
        for (int y = 0; y <= partTwoMax; ++y) {
            IntervalSet row = new IntervalSet(0, partTwoMax + 1);
            collectNoBeaconIntervals(sensors, y, i -> row.remove(i));
            final int fy = y;
            row.streamValues().forEach(x -> System.out.printf("Part two: y=%d, x=%d, frequency=%d", fy, x,
                    4000000L * (long) x + (long) fy));
        }
    }

    private static void collectNoBeaconIntervals(Map<Vec2i, Vec2i> sensors, int y, Consumer<IntInterval> collector) {
        sensors.forEach((s, b) -> {
            int maxDeltaX = s.manhattanDistanceTo(b) - Math.abs(y - s.y());
            Optional.of(new IntInterval(s.x() - maxDeltaX, s.x() + maxDeltaX + 1)).ifPresent(collector);
        });
    }
}