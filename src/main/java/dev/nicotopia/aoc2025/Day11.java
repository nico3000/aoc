package dev.nicotopia.aoc2025;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.DayBase;

public class Day11 extends DayBase {
    private final Map<String, Set<String>> graph = new HashMap<>();
    private final Map<String, Long> knownPathCounts = new HashMap<>();

    private long getPathCount(String from, String to, Set<String> mustVisit) {
        HashSet<String> newMustVisit = new HashSet<>(mustVisit);
        newMustVisit.remove(from);
        if (from.equals(to)) {
            return newMustVisit.isEmpty() ? 1 : 0;
        }
        String key = from + ";" + to + ";" + newMustVisit.stream().collect(Collectors.joining(";"));
        Long count = this.knownPathCounts.get(key);
        if (count == null) {
            count = this.graph.getOrDefault(from, Collections.emptySet()).stream()
                    .mapToLong(n -> this.getPathCount(n, to, newMustVisit)).sum();
            this.knownPathCounts.put(key, count);
        }
        return count;
    }

    @Override
    public void run() {
        for (String node : this.getPrimaryPuzzleInput()) {
            String split[] = node.split(": ");
            if (!this.graph.containsKey(split[0])) {
                this.graph.put(split[0], new HashSet<>());
            }
            this.graph.get(split[0]).addAll(Arrays.asList(split[1].split("\\s+")));
        }
        this.addTask("Part one", () -> this.getPathCount("you", "out", Collections.emptySet()));
        this.addTask("Part two", () -> this.getPathCount("svr", "out", new HashSet<>(Arrays.asList("dac", "fft"))));
    }
}
