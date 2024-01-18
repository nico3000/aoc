package dev.nicotopia.aoc2018;

import java.util.HashSet;
import java.util.Set;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc2018.Machine.Operation;

public class Day21 extends DayBase {
    private int numberA;
    private int numberB;
    private int partOne;
    private int partTwo;

    private boolean processInput() {
        int[] numbers = this.getPrimaryPuzzleInput().stream().map(Operation::fromAsm).filter(op -> op != null)
                .mapToInt(op -> Util.largestOf(op.a(), op.b(), op.c())).distinct().sorted().toArray();
        this.numberA = numbers[numbers.length - 2];
        this.numberB = numbers[numbers.length - 3];
        return (numbers[numbers.length - 1] == 0xffffff && numbers[numbers.length - 4] == 0x10000)
                || Dialog.showYesNoWarning("Warning",
                        "Your puzzle input seems not to match what I expected. As I decompiled my own one\nby hand and made an educated guess on how others might look, my solution might\nnot work for you or the app might even crash or run infinitely. Continue anyway?");
    }

    private void execute() {
        Set<Integer> known = new HashSet<>();
        int r1 = 0;
        do {
            this.partTwo = r1;
            int r4 = r1 | 0x10000;
            r1 = this.numberA;
            while (r4 != 0) {
                r1 = ((r1 + (r4 & 0xff)) * this.numberB) & 0xffffff;
                r4 /= 256;
            }
            if (this.partTwo == 0) {
                this.partOne = r1;
            }
        } while (known.add(r1));
    }

    @Override
    public void run() {
        boolean bContinue = this.addSilentTask("Process input", this::processInput);
        if (bContinue) {
            this.addTask("Execution", this::execute);
            this.addTask("Part one", () -> this.partOne);
            this.addTask("Part two", () -> this.partTwo);
            this.addTask("Note",
                    () -> "I decompiled my puzzle input by hand and made an eductaed guess on how others\nmight look. Because of that the solutions might not work for you.");
        }
    }
}
