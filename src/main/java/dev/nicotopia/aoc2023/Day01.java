package dev.nicotopia.aoc2023;

import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;

public class Day01 extends DayBase {
    private static int process(List<String> lines, String digits[]) {
        int sum = 0;
        for (String line : lines) {
            var b = IntStream.range(0, digits.length).mapToObj(i -> new Pair<>(line.indexOf(digits[i]), 1 + i % 9))
                    .filter(o -> o.first() != -1).min((p1, p2) -> Integer.compare(p1.first(), p2.first()));
            var e = IntStream.range(0, digits.length).mapToObj(i -> new Pair<>(line.lastIndexOf(digits[i]), 1 + i % 9))
                    .filter(o -> o.first() != -1).max((p1, p2) -> Integer.compare(p1.first(), p2.first()));
            if (b.isPresent() && e.isPresent()) {
                sum += 10 * b.get().second() + e.get().second();
            } else {
                System.err.printf("This line gives me a headache: %s\n", line);
            }
        }
        return sum;
    }

    @Override
    public void run() {
        List<String> lines = this.getPrimaryPuzzleInput();
        String partOneDigits[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        String partTwoDigits[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "one", "two", "three", "four", "five",
                "six", "seven", "eight", "nine" };
        this.addTask("Part one", () -> process(lines, partOneDigits));
        this.addTask("Part two", () -> process(lines, partTwoDigits));
    }
}