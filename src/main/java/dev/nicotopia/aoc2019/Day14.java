package dev.nicotopia.aoc2019;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;

public class Day14 extends DayBase {
    private record Edge(String from, int fromAmount, int toAmount, String to) {
    }

    private final List<Edge> edges = new LinkedList<>();

    private void processInput() {
        for (String line : this.getPrimaryPuzzleInput()) {
            String[] fromTo = line.split(" => ");
            int toAmount = Integer.valueOf(fromTo[1].split(" ")[0]);
            String to = fromTo[1].split(" ")[1];
            Arrays.stream(fromTo[0].split(", ")).map(s -> s.split(" "))
                    .map(s -> new Edge(s[1], Integer.valueOf(s[0]), toAmount, to)).forEach(this.edges::add);
        }
    }

    private long partOne(long desiredFuelAmount) {
        List<Edge> localEdges = new LinkedList<>(this.edges);
        Map<String, Long> required = new HashMap<>();
        required.put("FUEL", desiredFuelAmount);
        while (!localEdges.isEmpty()) {
            Set<String> set = localEdges.stream().map(Edge::from).collect(Collectors.toSet());
            String chemical = localEdges.stream().map(Edge::to).filter(s -> !set.contains(s)).findAny().get();
            long chemicalAmount = required.getOrDefault(chemical, 0L);
            localEdges.removeIf(e -> {
                if (e.to.equals(chemical)) {
                    long t = (chemicalAmount + e.toAmount - 1) / e.toAmount;
                    required.put(e.from, required.getOrDefault(e.from, 0L) + e.fromAmount * t);
                    return true;
                }
                return false;
            });
        }
        return required.get("ORE");
    }

    private long partTwo() {
        return Util.binarySearch(v -> this.partOne(v) < 1000000000000L) - 1;
    }

    @Override
    public void run() {
        this.addTask("Process inpit", this::processInput);
        this.addTask("Part one", () -> this.partOne(1));
        this.addTask("Part two", this::partTwo);
    }
}
