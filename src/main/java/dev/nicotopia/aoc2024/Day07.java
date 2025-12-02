package dev.nicotopia.aoc2024;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.IntCombinationsIterator;

public class Day07 extends DayBase {
    private class Equation {
        public static final int ADD = 0;
        public static final int MUL = 1;
        public static final int CONCAT = 2;

        private final List<Long> values;
        private final long result;

        public Equation(String line) {
            String splitLine[] = line.split(": ");
            this.result = Long.valueOf(splitLine[0]);
            this.values = Arrays.stream(splitLine[1].split(" ")).map(Long::valueOf).toList();
        }

        public boolean check(int ops[]) {
            long r = this.values.getFirst();
            for (int i = 1; i < this.values.size(); ++i) {
                long next = this.values.get(i);
                r = switch (ops[i - 1]) {
                    case ADD -> r + next;
                    case MUL -> r * next;
                    case CONCAT -> Long.valueOf(String.valueOf(r) + String.valueOf(next));
                    default -> throw new AocException();
                };
            }
            return r == this.result;
        }

        public boolean isSolvable(int opCount) {
            return new IntCombinationsIterator(this.values.size() - 1, 0, opCount).stream(true).anyMatch(this::check);
        }
    }

    private List<Equation> equations;
    private List<Equation> remainingEquations;

    private long partOne() {
        Map<Boolean, List<Equation>> partitioned = this.equations.stream()
                .collect(Collectors.partitioningBy(e -> e.isSolvable(2)));
        this.remainingEquations = partitioned.get(false);
        return partitioned.get(true).stream().mapToLong(e -> e.result).sum();
    }

    private long partTwo() {
        return this.remainingEquations.stream().filter(e -> e.isSolvable(3)).mapToLong(e -> e.result).sum();
    }

    @Override
    public void run() {
        this.equations = this.getPrimaryPuzzleInput().stream().map(Equation::new).toList();
        long partOneResult = this.addTask("Part one", this::partOne);
        this.addTask("Part two", () -> this.partTwo() + partOneResult);
    }
}
