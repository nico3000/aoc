package dev.nicotopia.aoc2019;

import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day01 extends DayBase {
    private final int sum(ToIntFunction<Integer> f) {
        return this.getPrimaryPuzzleInput().stream().map(Integer::valueOf).mapToInt(f).sum();
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.sum(i -> i / 3 - 2));
        this.addTask("Part two", () -> this.sum(i -> IntStream.iterate(i / 3 - 2, j -> 0 < j, j -> j / 3 - 2).sum()));
    }
}
