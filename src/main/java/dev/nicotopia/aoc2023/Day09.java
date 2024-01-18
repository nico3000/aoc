package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day09 extends DayBase {
    private int extrapolate(int[] h, BiFunction<int[], Integer, Integer> merge) {
        if (Arrays.stream(h).allMatch(i -> i == 0)) {
            return 0;
        }
        int ext = this.extrapolate(IntStream.range(0, h.length - 1).map(i -> h[i + 1] - h[i]).toArray(), merge);
        return merge.apply(h, ext);
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        List<int[]> histories = this.addSilentTask("Process input", () -> this.getPrimaryPuzzleInput().stream()
                .map(l -> Arrays.stream(l.split("\\s+")).mapToInt(Integer::valueOf).toArray()).toList());
        BiFunction<int[], Integer, Integer> partOneMerge = (h, e) -> e + h[h.length - 1];
        BiFunction<int[], Integer, Integer> partTwoMerge = (h, e) -> h[0] - e;
        this.addTask("Part one", () -> histories.stream().mapToInt(h -> this.extrapolate(h, partOneMerge)).sum());
        this.addTask("Part two", () -> histories.stream().mapToInt(h -> this.extrapolate(h, partTwoMerge)).sum());
    }
}