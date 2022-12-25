package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day15 {
    public record Position(int x, int y) {
        public int getManhattenDistanceTo(Position other) {
            return Math.abs(other.x - this.x) + Math.abs(other.y - this.y);
        }
    }

    public record Interval(int beg, int end) {
        public boolean contains(int i) {
            return this.beg <= i && i < this.end;
        }

        public boolean isDisjunctTo(Interval other) {
            return this.end <= other.beg || other.end <= this.beg;
        }

        public void ifNotEmpty(Consumer<Interval> consumer) {
            Optional.of(this).filter(i -> i.beg < i.end).ifPresent(consumer);
        }

        public IntStream stream() {
            return IntStream.range(this.beg, this.end);
        }
    }

    public static class IntervalSet {
        private final List<Interval> intervals = new LinkedList<>();

        public IntervalSet(Interval initial) {
            this.intervals.add(initial);
        }

        public void remove(Interval toRemove) {
            ListIterator<Interval> iter = intervals.listIterator();
            while (iter.hasNext()) {
                Interval interval = iter.next();
                if (!interval.isDisjunctTo(toRemove)) {
                    iter.remove();
                    if (!toRemove.contains(interval.beg)) {
                        iter.add(new Interval(interval.beg, toRemove.beg));
                    }
                    if (!toRemove.contains(interval.end)) {
                        iter.add(new Interval(toRemove.end, interval.end));
                    }
                }
            }
        }

        public IntStream stream() {
            return this.intervals.stream().mapMultiToInt((i, m) -> i.stream().forEach(m));
        }
    }

    public static void main(String[] args) throws IOException {
        Map<Position, Position> sensors = new HashMap<>();
        int partOneY = 2000000;
        int partTwoMax = 4000000;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day15.class.getResourceAsStream("/2022/day15.txt")))) {
            Pattern p = Pattern
                    .compile("Sensor at x=(-?[0-9]+), y=(-?[0-9]+): closest beacon is at x=(-?[0-9]+), y=(-?[0-9]+)");
            br.lines().map(p::matcher).filter(Matcher::matches)
                    .forEach(m -> sensors.put(new Position(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))),
                            new Position(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)))));
        }
        Set<Integer> partOneSet = new HashSet<>();
        collectNoBeaconIntervals(sensors, partOneY, i -> partOneSet.addAll(i.stream().boxed().toList()));
        partOneSet.removeAll(sensors.values().stream().filter(b -> b.y == partOneY).map(b -> b.x).toList());
        System.out.println("Part one: " + partOneSet.size());
        for (int y = 0; y <= partTwoMax; ++y) {
            IntervalSet row = new IntervalSet(new Interval(0, partTwoMax + 1));
            collectNoBeaconIntervals(sensors, y, i -> row.remove(i));
            final int fy = y;
            row.stream().forEach(x -> System.out.printf("Part two: y=%d, x=%d, frequency=%d", fy, x,
                    4000000L * (long) x + (long) fy));
        }
    }

    private static void collectNoBeaconIntervals(Map<Position, Position> sensors, int y, Consumer<Interval> collector) {
        sensors.forEach((s, b) -> {
            int maxDeltaX = s.getManhattenDistanceTo(b) - Math.abs(y - s.y);
            new Interval(s.x - maxDeltaX, s.x + maxDeltaX + 1).ifNotEmpty(collector);
        });
    }
}