package dev.nicotopia.aoc2024;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.DayBase;

public class Day03 extends DayBase {
    private long partOne(String input) {
        Pattern p = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        return p.matcher(input).results().mapToLong(mr -> Long.valueOf(mr.group(1)) * Long.valueOf(mr.group(2))).sum();
    }

    private long partTwo(String input) {
        Pattern p = Pattern.compile("do\\(\\)(.+?)don't\\(\\)");
        return p.matcher("do()" + input + "don't()").results().mapToLong(r -> this.partOne(r.group(1))).sum();
    }

    @Override
    public void run() {
        String input = this.getPrimaryPuzzleInput().stream().collect(Collectors.joining());
        this.addTask("Part one", () -> this.partOne(input));
        this.addTask("Part two", () -> this.partTwo(input));
    }
}
