package dev.nicotopia.aoc2024;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.nicotopia.aoc.DayBase;

public class Day11 extends DayBase {
    private record State(long engraving, int numSteps) {
    }

    private final Map<State, Long> cache = new HashMap<>();

    public long simulate(List<String> original, int numSteps) {
        return original.stream().mapToLong(s -> this.lookUp(Long.valueOf(s), numSteps)).sum();
    }

    public long lookUp(long engraving, int numSteps) {
        if (numSteps == 0) {
            return 1;
        }
        State state = new State(engraving, numSteps);
        Long r = this.cache.get(state);
        if (r == null) {
            if (engraving == 0) {
                r = this.lookUp(1L, numSteps - 1);
            } else {
                String engravingStr = String.valueOf(engraving);
                if (engravingStr.length() % 2 == 0) {
                    long left = Long.valueOf(engravingStr.substring(0, engravingStr.length() / 2));
                    long right = Long.valueOf(engravingStr.substring(engravingStr.length() / 2));
                    r = this.lookUp(left, numSteps - 1) + this.lookUp(right, numSteps - 1);
                } else {
                    r = this.lookUp(2024 * engraving, numSteps - 1);
                }
            }
            this.cache.put(state, r);
        }
        return r;
    }

    @Override
    public void run() {
        List<String> stones = Arrays.asList(this.getPrimaryPuzzleInput().getFirst().split("\\s+"));
        this.addTask("Part one", () -> this.simulate(stones, 25));
        this.addTask("Part two", () -> this.simulate(stones, 75));
    }
}
