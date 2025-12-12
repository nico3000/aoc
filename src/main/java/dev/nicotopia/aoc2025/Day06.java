package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.List;

import dev.nicotopia.aoc.DayBase;

public class Day06 extends DayBase {
    private class Task {
        private char operator;
        private final List<Long> numbers = new ArrayList<>();

        public long getResult() {
            switch (this.operator) {
                case '+':
                    return this.numbers.stream().mapToLong(v -> v).sum();
                case '*':
                    return this.numbers.stream().mapToLong(v -> v).reduce(1, (a, b) -> a * b);
            }
            throw new UnsupportedOperationException();
        }
    }

    private List<String> getTransposedInput() {
        char src[][] = this.getPrimaryPuzzleInputAs2DCharArray();
        List<String> dst = new ArrayList<>();
        for (int i = 0; i < src[0].length; ++i) {
            dst.add("");
            for (int j = 0; j < src.length; ++j) {
                dst.set(i, dst.get(i) + src[j][i]);
            }
        }
        dst.add(" ");
        return dst;
    }

    private long partOne() {
        List<Task> tasks = new ArrayList<>();
        for (String line : this.getPrimaryPuzzleInput()) {
            String split[] = line.trim().split("\s+");
            for (int i = 0; i < split.length; ++i) {
                if (tasks.size() <= i) {
                    tasks.add(new Task());
                }
                try {
                    tasks.get(i).numbers.add(Long.valueOf(split[i]));
                } catch (NumberFormatException ex) {
                    tasks.get(i).operator = split[i].charAt(0);
                }
            }
        }
        return tasks.stream().mapToLong(Task::getResult).sum();
    }

    private long partTwo() {
        long result = 0;
        Task task = null;
        for (String line : this.getTransposedInput()) {
            if (line.endsWith("+") || line.endsWith("*")) {
                task = new Task();
                task.operator = line.charAt(line.length() - 1);
            }
            String number = line.substring(0, line.length() - 1).trim();
            if (number.isEmpty()) {
                result += task.getResult();
            } else {
                task.numbers.add(Long.valueOf(number));
            }
        }
        return result;
    }

    @Override
    public void run() {
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
