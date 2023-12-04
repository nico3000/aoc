package dev.nicotopia.aoc2018;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day07 extends DayBase {
    private final Map<Character, Set<Character>> edgesPartOne = new HashMap<>();
    private final Map<Character, Set<Character>> edgesPartTwo = new HashMap<>();

    private class Worker {
        private char node = 0;
        private int remaining = 0;

        public char proceed(int steps) {
            if (this.node != 0 && (this.remaining -= steps) == 0) {
                char n = this.node;
                this.node = 0;
                return n;
            }
            return 0;
        }
    }

    private void processInput() {
        Pattern p = Pattern.compile("Step (.) must be finished before step (.) can begin\\.");
        this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).forEach(m -> {
            this.edgesPartOne.putIfAbsent(m.group(1).charAt(0), new HashSet<>());
            this.edgesPartOne.putIfAbsent(m.group(2).charAt(0), new HashSet<>());
            this.edgesPartOne.get(m.group(2).charAt(0)).add(m.group(1).charAt(0));
        });
        this.edgesPartTwo.putAll(this.edgesPartOne.keySet().stream().collect(HashMap<Character, Set<Character>>::new,
                (m, n) -> m.put(n, new HashSet<>(this.edgesPartOne.get(n))), HashMap::putAll));
    }

    private String partOne() {
        String order = "";
        while (!this.edgesPartOne.isEmpty()) {
            var node = this.edgesPartOne.keySet().stream().filter(n -> this.edgesPartOne.get(n).isEmpty()).sorted()
                    .findFirst().get();
            this.edgesPartOne.remove(node);
            this.edgesPartOne.values().stream().forEach(e -> e.remove(node));
            order += node;
        }
        return order;
    }

    private int partTwo(int numWorkers, int minTime) {
        var workers = IntStream.range(0, numWorkers).mapToObj(i -> new Worker()).toList();
        int stepSum = 0;
        while (!this.edgesPartTwo.isEmpty()) {
            var readyNodes = new LinkedList<>(this.edgesPartTwo.keySet().stream()
                    .filter(n -> this.edgesPartTwo.get(n).isEmpty()).sorted().toList());
            var freeWorkers = new LinkedList<>(workers.stream().filter(w -> w.remaining == 0).toList());
            while (!readyNodes.isEmpty() && !freeWorkers.isEmpty()) {
                Worker w = freeWorkers.remove(0);
                w.node = readyNodes.remove(0);
                w.remaining = minTime + w.node - 'A' + 1;
                this.edgesPartTwo.remove(w.node);
            }
            var steps = workers.stream().mapToInt(w -> w.remaining).filter(i -> i != 0).min();
            if (steps.isPresent()) {
                workers.stream().map(w -> w.proceed(steps.getAsInt())).filter(n -> n != 0).sorted().forEach(n -> {
                    this.edgesPartTwo.values().stream().forEach(e -> e.remove(n));
                });
                stepSum += steps.getAsInt();
            }
        }
        return stepSum;
    }

    @Override
    public void run() {
        this.registerSecondaryInputs("numWorkers", "minSeconds");
        this.addPresetFromResource("Example", "/2018/day07e.txt", 2, 0);
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", () -> partTwo(this.getIntInput("numWorkers"), this.getIntInput("minSeconds")));
    }
}