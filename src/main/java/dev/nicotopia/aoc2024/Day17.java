package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.nicotopia.aoc.DayBase;

public class Day17 extends DayBase {
    private class Machine {
        private final List<Integer> instructions;
        private final List<Integer> output = new ArrayList<>();
        private long regA;
        private long regB;
        private long regC;
        private int ip;

        public Machine(String program) {
            this.instructions = Stream.of(program.split(",")).map(Integer::valueOf).toList();
        }

        public List<Integer> getInstructions() {
            return Collections.unmodifiableList(this.instructions);
        }

        public List<Integer> getOutput() {
            return Collections.unmodifiableList(this.output);
        }

        public void execute(long regA, long regB, long regC) {
            this.output.clear();
            this.regA = regA;
            this.regB = regB;
            this.regC = regC;
            this.ip = 0;
            while (this.ip < this.instructions.size()) {
                int instr = this.instructions.get(this.ip++);
                int litOp = this.instructions.get(this.ip++);
                this.executeInstruction(instr, litOp);
            }
        }

        private void executeInstruction(int instr, int literalOperand) {
            long comboOperand = switch (literalOperand) {
                case 4 -> this.regA;
                case 5 -> this.regB;
                case 6 -> this.regC;
                default -> literalOperand;
            };
            switch (instr) {
                case 0:
                    this.regA /= 1L << comboOperand;
                    break;
                case 1:
                    this.regB ^= literalOperand;
                    break;
                case 2:
                    this.regB = comboOperand % 8L;
                    break;
                case 3:
                    if (this.regA != 0L) {
                        this.ip = literalOperand;
                    }
                    break;
                case 4:
                    this.regB ^= this.regC;
                    break;
                case 5:
                    this.output.add((int) (comboOperand % 8L));
                    break;
                case 6:
                    this.regB = this.regA / (1L << comboOperand);
                    break;
                case 7:
                    this.regC = this.regA / (1L << comboOperand);
                    break;
            }
        }
    }

    private Machine machine;

    private final String partOne(long regA, long regB, long regC) {
        this.machine.execute(regA, regB, regC);
        return this.machine.getOutput().stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private final Long partTwo(long regB, long regC) {
        List<Long> candidates = new ArrayList<>();
        for (long regA = 0; regA < 1L << 10; ++regA) {
            this.machine.execute(regA, regB, regC);
            if (this.machine.getOutput().getFirst() == this.machine.getInstructions().getFirst()) {
                candidates.add(regA);
            }
        }
        for (int i = 2; i <= this.machine.getInstructions().size(); ++i) {
            List<Long> newCandidates = new ArrayList<>();
            for (long candidate : candidates) {
                for (int appendix = 0; appendix < 8; ++appendix) {
                    long regA = ((long) appendix << (10 + 3 * (i - 2))) | candidate;
                    this.machine.execute(regA, regB, regC);
                    boolean allMatch = i <= this.machine.getOutput().size()
                            && this.machine.getOutput().size() <= this.machine.getInstructions().size();
                    for (int j = 0; allMatch && j < i; ++j) {
                        allMatch &= this.machine.getOutput().get(j) == this.machine.getInstructions().get(j);
                    }
                    if (allMatch) {
                        newCandidates.add(regA);
                    }
                }
            }
            candidates = newCandidates;
        }
        return candidates.stream().mapToLong(Long::valueOf).min().stream().boxed().findAny().orElse(null);
    }

    @Override
    public void run() {
        List<String> input = this.getPrimaryPuzzleInput();
        long regA = Long.valueOf(input.get(0).split(" ")[2]);
        long regB = Long.valueOf(input.get(1).split(" ")[2]);
        long regC = Long.valueOf(input.get(2).split(" ")[2]);
        String program = input.getLast().split(" ")[1];
        this.machine = new Machine(program);
        this.addTask("Part one", () -> this.partOne(regA, regB, regC));
        this.addTask("Part two", () -> this.partTwo(regB, regC));
    }
}
