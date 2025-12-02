package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day01 extends DayBase {
    private int partOne(List<Integer> a, List<Integer> b) {
        return IntStream.range(0, a.size()).map(i -> Math.abs(a.get(i) - b.get(i))).sum();
    }

    private long partTwo(List<Integer> a, List<Integer> b) {
        return a.stream().mapToLong(i -> i * b.stream().filter(j -> j.equals(i)).count()).sum();
    }

    @Override
    public void run() {
        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d+)\\s+(\\d+)");
        this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).forEach(m -> {
            a.add(Integer.valueOf(m.group(1)));
            b.add(Integer.valueOf(m.group(2)));
        });
        Collections.sort(a);
        Collections.sort(b);
        this.addTask("Part one", () -> this.partOne(a, b));
        this.addTask("Part two", () -> this.partTwo(a, b));
    }
}
