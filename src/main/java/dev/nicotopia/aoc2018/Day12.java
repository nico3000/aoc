package dev.nicotopia.aoc2018;

import java.util.BitSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;

public class Day12 extends DayBase {
    public class Pots {
        private final BitSet negative = new BitSet();
        private final BitSet positive = new BitSet();

        public void set(int pos, boolean state) {
            (pos < 0 ? this.negative : this.positive).set(pos < 0 ? -pos - 1 : pos, state);
        }

        public int getFirstSetPotIdx() {
            return -this.negative.length();
        }

        public int getLastSetPotIdx() {
            return this.positive.length() - 1;
        }

        public boolean get(int pos) {
            return (pos < 0 ? this.negative : this.positive).get(pos < 0 ? -pos - 1 : pos);
        }

        @Override
        public String toString() {
            String left = IntStream.range(0, this.negative.length()).mapToObj(i -> this.negative.get(i) ? "#" : ".")
                    .collect(Collectors.joining());
            String right = IntStream.range(0, this.positive.length()).mapToObj(i -> this.positive.get(i) ? "#" : ".")
                    .collect(Collectors.joining());
            return left + right;
        }
    }

    private Pots initialPots = new Pots();
    private Set<Integer> growingStates;
    private int lastTailLength = 0;

    private void processInput() {
        String initial = this.getPrimaryPuzzleInput().getFirst().substring("initial state: ".length());
        for (int i = 0; i < initial.length(); ++i) {
            this.initialPots.set(i, initial.charAt(i) == '#');
        }
        this.growingStates = this.getPrimaryPuzzleInput().stream().filter(l -> l.endsWith(" => #"))
                .map(l -> (l.charAt(0) == '#' ? 16 : 0) + (l.charAt(1) == '#' ? 8 : 0) + (l.charAt(2) == '#' ? 4 : 0)
                        + (l.charAt(3) == '#' ? 2 : 0) + (l.charAt(4) == '#' ? 1 : 0))
                .collect(Collectors.toSet());
    }

    private Pots simulate(int iterations) {
        Pots currentPots = this.initialPots;
        for (long i = 0; i < iterations; ++i) {
            int firstChangedIdx = Integer.MAX_VALUE;
            Pots newPots = new Pots();
            for (int j = currentPots.getFirstSetPotIdx() - 2; j != currentPots.getLastSetPotIdx() + 2; ++j) {
                int state = (currentPots.get(j - 2) ? 16 : 0) + (currentPots.get(j - 1) ? 8 : 0)
                        + (currentPots.get(j) ? 4 : 0) + (currentPots.get(j + 1) ? 2 : 0)
                        + (currentPots.get(j + 2) ? 1 : 0);
                boolean oldValue = currentPots.get(j);
                boolean newValue = growingStates.contains(state);
                if (firstChangedIdx == Integer.MAX_VALUE && oldValue != newValue) {
                    firstChangedIdx = j;
                }
                newPots.set(j, growingStates.contains(state));
            }
            currentPots = newPots;
            this.lastTailLength = 1 + currentPots.getLastSetPotIdx() - firstChangedIdx;
        }
        return currentPots;
    }

    private int partOne() {
        Pots pots = this.simulate(20);
        return IntStream.rangeClosed(pots.getFirstSetPotIdx(), pots.getLastSetPotIdx()).filter(pots::get).sum();
    }

    private long partTwo() {
        final int tailLength = this.lastTailLength;
        final int simulatedIterations = 2000;
        final long actualIterations = 50000000000L;
        Pots pots = this.simulate(simulatedIterations);
        long baseSum = IntStream.rangeClosed(pots.getFirstSetPotIdx(), pots.getLastSetPotIdx() - tailLength)
                .filter(pots::get).sum();
        long tailSum = IntStream.rangeClosed(pots.getLastSetPotIdx() - tailLength + 1, pots.getLastSetPotIdx())
                .filter(pots::get).sum();
        long numSetPotsInTail = IntStream.rangeClosed(pots.getLastSetPotIdx() - tailLength + 1, pots.getLastSetPotIdx())
                .filter(pots::get).count();
        return baseSum + tailSum + (actualIterations - simulatedIterations) * numSetPotsInTail;
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2018/day12e.txt");
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        if (Dialog.showYesNoQuestion("Info",
                "The solution of part two was adjusted to work with my own puzzle input and might\nnot work with others. Do you want to try it anyway?")) {
            this.addTask("Part two", this::partTwo);
        }
    }
}