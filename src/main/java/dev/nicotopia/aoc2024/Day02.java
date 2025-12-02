package dev.nicotopia.aoc2024;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day02 extends DayBase {
    private boolean isSafe(int levels[]) {
        boolean increasing = IntStream.range(0, levels.length - 1).allMatch(i -> levels[i] < levels[i + 1]);
        boolean decreasing = IntStream.range(0, levels.length - 1).allMatch(i -> levels[i + 1] < levels[i]);
        boolean shallow = IntStream.range(0, levels.length - 1)
                .allMatch(i -> Math.abs(levels[i] - levels[i + 1]) < 4);
        return shallow && (increasing || decreasing);
    }

    private boolean isAlmostSafe(int levels[]) {
        return IntStream.range(0, levels.length).anyMatch(toRemove -> this
                .isSafe(IntStream.range(0, levels.length).filter(j -> toRemove != j).map(j -> levels[j]).toArray()));
    }

    private long partOne(List<int[]> reports) {
        return reports.stream().filter(this::isSafe).count();
    }

    private long partTwo(List<int[]> reports) {
        return reports.stream().filter(levels -> this.isSafe(levels) || this.isAlmostSafe(levels)).count();
    }

    @Override
    public void run() {
        List<int[]> reports = this.getPrimaryPuzzleInput().stream()
                .map(line -> Arrays.stream(line.split("\\s+")).mapToInt(Integer::valueOf).toArray()).toList();
        this.addTask("Part one", () -> this.partOne(reports));
        this.addTask("Part two", () -> this.partTwo(reports));
    }
}
