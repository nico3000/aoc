package dev.nicotopia.aoc2019;

import dev.nicotopia.aoc.DayBase;

public class Day05 extends DayBase {
    private long execute(int input) {
        return new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst()).execute(input).getLast();
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.execute(1));
        this.addTask("Part two", () -> this.execute(5));
    }
}