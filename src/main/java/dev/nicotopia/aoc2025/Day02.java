package dev.nicotopia.aoc2025;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import dev.nicotopia.aoc.DayBase;

public class Day02 extends DayBase {
    private long sumInvalidIds(String invalidIdRegex) {
        LongStream ids = Arrays.stream(this.getPrimaryPuzzleInput().getFirst().split(",")).map(s -> s.split("-"))
                .flatMapToLong(s -> LongStream.rangeClosed(Long.parseLong(s[0]), Long.parseLong(s[1]))).parallel();
        Pattern p = Pattern.compile(invalidIdRegex);
        return ids.filter(v -> p.matcher(Long.toString(v)).matches()).sum();
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.sumInvalidIds("^(.+)\\1$"));
        this.addTask("Part two", () -> this.sumInvalidIds("^(.+)\\1+$"));
    }
}
