package dev.nicotopia.aoc2018;

import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day12 extends DayBase {
    public class Pots {
        private final BitSet negative = new BitSet();
        private final BitSet positive = new BitSet();

        public void set(int pos, boolean state) {
            (pos < 0 ? this.negative : this.positive).set(pos < 0 ? -pos - 1 : pos, state);
        }

        public int getMin() {
            return -this.negative.length();
        }

        public int getMax() {
            return this.positive.length() + 1;
        }

        public boolean get(int pos) {
            return (pos < 0 ? this.negative : this.positive).get(pos < 0 ? -pos - 1 : pos);
        }

        @Override
        public String toString() {
            String left = IntStream.range(0, this.negative.length()).mapToObj(i -> this.negative.get(i) ? "#" : ".")
                    .reduce("", (a, b) -> b + a);
            String right = IntStream.range(0, this.positive.length()).mapToObj(i -> this.positive.get(i) ? "#" : ".")
                    .reduce("", String::concat);
            return left + right;
        }
    }

    private Pots pots = new Pots();
    private List<Integer> growingStates;

    private void processInput() {
        String initial = this.getPrimaryPuzzleInput().getFirst().substring("initial state: ".length());
        for (int i = 0; i < initial.length(); ++i) {
            this.pots.set(i, initial.charAt(i) == '#');
        }
        this.growingStates = this.getPrimaryPuzzleInput().stream().filter(l -> l.endsWith(" => #"))
                .map(l -> (l.charAt(0) == '#' ? 16 : 0) + (l.charAt(1) == '#' ? 8 : 0) + (l.charAt(2) == '#' ? 4 : 0)
                        + (l.charAt(3) == '#' ? 2 : 0) + (l.charAt(4) == '#' ? 1 : 0))
                .toList();
    }

    private int simulate(long iterations) {
        for (long i = 0; i < iterations; ++i) {
            Pots newPots = new Pots();
            for (int j = pots.getMin() - 2; j != pots.getMax() + 2; ++j) {
                int state = (pots.get(j - 2) ? 16 : 0) + (pots.get(j - 1) ? 8 : 0) + (pots.get(j) ? 4 : 0)
                        + (pots.get(j + 1) ? 2 : 0) + (pots.get(j + 2) ? 1 : 0);
                newPots.set(j, growingStates.contains(state));
            }
            pots = newPots;
        }
        return IntStream.rangeClosed(this.pots.getMin(), this.pots.getMax()).map(i -> this.pots.get(i) ? i : 0).sum();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2018/day12e.txt");
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", () -> this.simulate(20));
        this.addTask("Part two", () -> this.simulate(50000000000L));
    }
}
