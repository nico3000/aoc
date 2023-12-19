package dev.nicotopia.aoc2018;

import java.util.function.Supplier;

import dev.nicotopia.aoc.DayBase;

public class Day14 extends DayBase {
    private final StringBuilder recipes = new StringBuilder("37");
    private int elfA = 0;
    private int elfB = 1;

    private void stepWhile(Supplier<Boolean> pred) {
        while (pred.get()) {
            int sum = this.recipes.charAt(this.elfA) - '0' + this.recipes.charAt(this.elfB) - '0';
            if (10 <= sum) {
                this.recipes.append((char) ('0' + sum / 10));
            }
            this.recipes.append((char) ('0' + sum % 10));
            this.elfA = (this.elfA + 1 + this.recipes.charAt(this.elfA) - '0') % this.recipes.length();
            this.elfB = (this.elfB + 1 + this.recipes.charAt(this.elfB) - '0') % this.recipes.length();
        }
    }

    private String partOne(int count) {
        this.stepWhile(() -> recipes.length() < count + 10);
        return this.recipes.substring(count, count + 10);
    }

    private long partTwo(String sequence) {
        this.recipes.setLength(2);
        this.elfA = 0;
        this.elfB = 1;
        this.stepWhile(() -> this.recipes.indexOf(sequence, this.recipes.length() - sequence.length() - 1) == -1);
        return this.recipes.indexOf(sequence);
    }

    @Override
    public void run() {
        this.addPreset("Example 1", "2018");
        this.addPreset("Example 2", "59414");
        String input = this.getPrimaryPuzzleInput().getFirst();
        this.addTask("Part one", () -> this.partOne(Integer.valueOf(input)));
        this.addTask("Part two", () -> this.partTwo(input));
    }
}