package dev.nicotopia.aoc2019;

import dev.nicotopia.aoc.DayBase;

public class Day02 extends DayBase {
    private IntcodeMachine machine;

    private void processInput() {
        this.machine = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
    }

    private long execute(int m1, int m2) {
        this.machine.reset();
        this.machine.set(1, m1);
        this.machine.set(2, m2);
        this.machine.execute();
        return this.machine.get(0);
    }

    private int partTwo() {
        for (int m1 = 0; m1 < 100; ++m1) {
            for (int m2 = 0; m2 < 100; ++m2) {
                this.execute(m1, m2);
                if (this.machine.get(0) == 19690720) {
                    return 100 * m1 + m2;
                }
            }
        }
        return -1;
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", () -> this.execute(12, 2));
        this.addTask("Part two", this::partTwo);
    }
}