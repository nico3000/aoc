package dev.nicotopia.aoc2019;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day19 extends DayBase {
    public long partOne(IntcodeMachine machine) {
        return Vec2i.streamFromRectangle(0, 0, 50, 50)
                .filter(p -> machine.clone().execute(p.x(), p.y()).getFirst() == 1).count();
    }

    public long partTwo(IntcodeMachine machine) {
        int prevX = 0;
        for (int y = 0;; ++y) {
            int x = prevX;
            boolean found = false;
            while (!found && x < prevX + 50) {
                found = machine.executeAndReset(x, y).getFirst() == 1;
                ++x;
            }
            if (found) {
                prevX = --x;
                while (machine.executeAndReset(x + 99, y).getFirst() == 1) {
                    if (machine.executeAndReset(x, y + 99).getFirst() == 1) {
                        return 10000 * x + y;
                    }
                    ++x;
                }
            }
        }
    }

    @Override
    public void run() {
        IntcodeMachine machine = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
        this.addTask("Part one", () -> this.partOne(machine.clone()));
        this.addTask("Part two", () -> this.partTwo(machine.clone()));
    }
}