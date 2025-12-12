package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.LongInterval;

public class Day05 extends DayBase {
    private final List<LongInterval> intervals = new ArrayList<>();
    private List<Long> ids;

    private void addInterval(LongInterval interval) {
        int idx = Collections.binarySearch(this.intervals, interval, (a, b) -> Long.compare(a.beg(), b.beg()));
        if (idx < 0) {
            idx = -(idx + 1);
            if (idx != 0 && interval.beg() <= this.intervals.get(idx - 1).end()) {
                LongInterval left = this.intervals.get(--idx);
                interval = new LongInterval(left.beg(), Math.max(left.end(), interval.end()));
                this.intervals.set(idx, interval);
            } else {
                this.intervals.add(idx, interval);
            }
        } else {
            LongInterval current = this.intervals.get(idx);
            interval = new LongInterval(interval.beg(), Math.max(interval.end(), current.end()));
            this.intervals.set(idx, interval);
        }
        while (idx + 1 < this.intervals.size()) {
            LongInterval right = this.intervals.get(idx + 1);
            if (right.beg() <= interval.end()) {
                this.intervals.remove(idx + 1);
                interval = new LongInterval(interval.beg(), Math.max(interval.end(), right.end()));
                this.intervals.set(idx, interval);
            } else {
                break;
            }
        }
    }

    private int partOne() {
        return (int) this.ids.stream().filter(id -> this.intervals.stream().anyMatch(interval -> interval.contains(id)))
                .count();
    }

    private long partTwo() {
        return this.intervals.stream().mapToLong(LongInterval::getSize).sum();
    }

    @Override
    public void run() {
        Map<Boolean, List<String>> split = this.getPrimaryPuzzleInput().stream().filter(line -> !line.isBlank())
                .collect(Collectors.partitioningBy(line -> line.contains("-")));
        split.get(true).stream().map(line -> line.split("-"))
                .map(ia -> new LongInterval(Long.valueOf(ia[0]), Long.valueOf(ia[1]) + 1)).forEach(this::addInterval);
        this.ids = split.get(false).stream().map(Long::valueOf).toList();
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
