package dev.nicotopia.aoc2018;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.algebra.IntInterval;

public class Day04 {
    public record Guard(int id, Map<String, List<IntInterval>> sleepIntervals) {
        public Guard(int id) {
            this(id, new HashMap<>());
        }

        public void addSleepInterval(String date, IntInterval interval) {
            List<IntInterval> intervals = this.sleepIntervals.get(date);
            if (intervals == null) {
                this.sleepIntervals.put(date, intervals = new LinkedList<>());
            }
            intervals.add(interval);
        }

        public int getSleepMinuteCount() {
            return this.sleepIntervals.values().stream()
                    .mapToInt(intervals -> intervals.stream().mapToInt(i -> i.end() - i.beg()).sum()).sum();
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day04.class.getResourceAsStream("/2018/day04.txt")))) {
            lines = new LinkedList<>(br.lines().toList());
        }
        Map<Integer, Guard> guards = new HashMap<>();
        Collections.sort(lines);
        Iterator<String> lineIt = lines.iterator();
        String line = lineIt.next();
        while (lineIt.hasNext()) {
            int id = Integer.valueOf(line.substring(line.indexOf("#") + 1, line.length() - 13));
            Guard g = guards.get(id);
            if (g == null) {
                guards.put(id, g = new Guard(id));
            }
            while (lineIt.hasNext() && !(line = lineIt.next()).endsWith(" begins shift")) {
                String endLine = lineIt.next();
                g.addSleepInterval(endLine.substring(1, 11), new IntInterval(Integer.valueOf(line.substring(15, 17)),
                        Integer.valueOf(endLine.substring(15, 17))));
            }
        }
        SleepyMinute partOne = getGuardsMostSleepyMinute(guards.values().stream()
                .sorted((l, r) -> r.getSleepMinuteCount() - l.getSleepMinuteCount()).findFirst().get());
        System.out.println("Part one: " + partOne.guardId * partOne.minute);
        SleepyMinute partTwo = guards.values().stream().map(Day04::getGuardsMostSleepyMinute)
                .sorted((l, r) -> r.count - l.count).findFirst().get();
        System.out.println("Part two: " + partTwo.guardId * partTwo.minute);
    }

    private record SleepyMinute(int guardId, int minute, int count) {
    }

    private static SleepyMinute getGuardsMostSleepyMinute(Guard g) {
        int minutes[] = new int[60];
        g.sleepIntervals.values().stream().collect(LinkedList<IntInterval>::new, LinkedList::addAll, LinkedList::addAll)
                .stream().forEach(i -> IntStream.range(i.beg(), i.end()).forEach(j -> ++minutes[j]));
        int maxIdx = 0;
        for (int i = 1; i < minutes.length; ++i) {
            if (minutes[maxIdx] < minutes[i]) {
                maxIdx = i;
            }
        }
        return new SleepyMinute(g.id, maxIdx, minutes[maxIdx]);
    }
}