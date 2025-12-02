package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day15 extends DayBase {
    private class Box {
        private final Map<String, Integer> focalLengths = new HashMap<>();
        private final List<String> lenses = new ArrayList<>();
    }

    private int hash(String s) {
        int h = 0;
        for (char c : s.toCharArray()) {
            h = 17 * (h + c) % 256;
        }
        return h;
    }

    private int partTwo(List<String> steps) {
        Box boxes[] = IntStream.range(0, 256).mapToObj(i -> new Box()).toArray(Box[]::new);
        for (String step : steps) {
            String[] split = step.split("=|-");
            Box relevantBox = boxes[this.hash(split[0])];
            if (split.length == 2 && relevantBox.focalLengths.put(split[0], Integer.valueOf(split[1])) == null) {
                relevantBox.lenses.add(split[0]);
            } else if (split.length == 1 && relevantBox.focalLengths.remove(split[0]) != null) {
                relevantBox.lenses.remove(split[0]);
            }
        }
        return IntStream.range(0, boxes.length).map(bi -> (bi + 1) * IntStream.range(0, boxes[bi].lenses.size())
                .map(i -> (i + 1) * boxes[bi].focalLengths.get(boxes[bi].lenses.get(i))).sum()).sum();
    }

    @Override
    public void run() {
        List<String> steps = this.addSilentTask("Process input",
                () -> this.getPrimaryPuzzleInput().stream().map(l -> l.split(",")).flatMap(Arrays::stream).toList());
        this.addTask("Part one", () -> steps.stream().mapToInt(this::hash).sum());
        this.addTask("Part two", () -> this.partTwo(steps));
    }
}