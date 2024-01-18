package dev.nicotopia.aoc2019;

import dev.nicotopia.aoc.DayBase;

public class Day09 extends DayBase {
    private long execute(long input) {
        return new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst()).execute(input).getFirst();
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.execute(1));
        this.addTask("Part two", () -> this.execute(2));
    }
}