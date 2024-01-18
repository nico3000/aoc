package dev.nicotopia.aoc2019;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day04 extends DayBase {
    private long count(Predicate<String> additionalFilter) {
        String[] split = this.getPrimaryPuzzleInput().getFirst().split("-");
        return IntStream.rangeClosed(Integer.valueOf(split[0]), Integer.valueOf(split[1])).mapToObj(String::valueOf)
                .filter(s -> s.length() == 6 && IntStream.range(0, 5).allMatch(i -> s.charAt(i) <= s.charAt(i + 1))
                        && additionalFilter.test(s))
                .count();
    }

    private boolean partOneFilter(String s) {
        return IntStream.range(0, 5).anyMatch(i -> s.charAt(i) == s.charAt(i + 1));
    }

    private boolean partTwoFilter(String s) {
        return IntStream.rangeClosed(0, 9)
                .anyMatch(i -> s.indexOf("" + i + i) != -1 && s.indexOf("" + i + i + i) == -1);
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.count(this::partOneFilter));
        this.addTask("Part two", () -> this.count(this::partTwoFilter));
    }
}