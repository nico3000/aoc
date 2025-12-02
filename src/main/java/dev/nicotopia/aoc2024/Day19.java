package dev.nicotopia.aoc2024;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.nicotopia.aoc.DayBase;

public class Day19 extends DayBase {
    private List<String> available;
    private List<String> needed;
    private final Map<String, Long> cache = new HashMap<>();

    private long getNumPossibilities(String pattern) {
        if (pattern.isEmpty()) {
            return 1;
        }
        Long n = this.cache.get(pattern);
        if (n != null) {
            return n;
        }
        n = this.available.stream().filter(p -> pattern.startsWith(p))
                .mapToLong(p -> this.getNumPossibilities(pattern.substring(p.length()))).sum();
        this.cache.put(pattern, n);
        return n;
    }

    private int partOne() {
        return (int) this.needed.stream().mapToLong(this::getNumPossibilities).filter(l -> l != 0).count();
    }

    private long partTwo() {
        return this.needed.stream().mapToLong(this::getNumPossibilities).sum();
    }

    @Override
    public void run() {
        List<String> input = this.getPrimaryPuzzleInput();
        this.available = Arrays.asList(input.getFirst().split(",\\s*"));
        this.needed = this.getPrimaryPuzzleInput().subList(2, input.size());
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
