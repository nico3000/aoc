package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day05 extends DayBase {
    private record Rule(int before, int after) {
        public boolean check(int update[]) {
            OptionalInt beforePos = IntStream.range(0, update.length).filter(i -> update[i] == before).findAny();
            OptionalInt afterPos = IntStream.range(0, update.length).filter(i -> update[i] == after).findAny();
            return beforePos.isEmpty() || afterPos.isEmpty() || beforePos.getAsInt() < afterPos.getAsInt();
        }

        public boolean applies(int update[]) {
            boolean foundBefore = false;
            boolean foundAfter = false;
            for (int i = 0; i < update.length && (!foundBefore || !foundAfter); ++i) {
                foundBefore |= update[i] == this.before;
                foundAfter |= update[i] == this.after;
            }
            return foundBefore && foundAfter;
        }
    }

    private Map<Boolean, List<int[]>> partition(List<Rule> rules, List<int[]> updates) {
        return updates.stream().collect(Collectors.partitioningBy(u -> rules.stream().allMatch(r -> r.check(u))));
    }

    private int partOne(List<int[]> validUpdates) {
        return validUpdates.stream().mapToInt(u -> u[u.length / 2]).sum();
    }

    private void reorder(List<Rule> rules, int invalidUpdate[]) {
        List<Rule> activeRules = rules.stream().filter(r -> r.applies(invalidUpdate)).collect(Collectors.toList());
        List<Integer> remaining = Arrays.stream(invalidUpdate).mapToObj(Integer::valueOf).collect(Collectors.toList());
        for (int i = 0; i < invalidUpdate.length; ++i) {
            int v = remaining.stream().filter(j -> activeRules.stream().noneMatch(r -> r.after() == j)).findAny().get();
            invalidUpdate[i] = v;
            remaining.remove(Integer.valueOf(v));
            activeRules.removeIf(r -> r.before() == v);
        }
    }

    private int partTwo(List<Rule> rules, List<int[]> invalidUpdates) {
        invalidUpdates.stream().forEach(u -> this.reorder(rules, u));
        return this.partOne(invalidUpdates);
    }

    @Override
    public void run() {
        List<Rule> rules = new ArrayList<>();
        List<int[]> updates = new ArrayList<>();
        Pattern p = Pattern.compile("((\\d+)\\|(\\d+))|(\\d+(,\\d+)*)");
        this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).forEach(m -> {
            if (m.group(1) != null) {
                rules.add(new Rule(Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3))));
            } else if (m.group(5) != null) {
                updates.add(Arrays.stream(m.group().split(",")).mapToInt(Integer::valueOf).toArray());
            }
        });
        Map<Boolean, List<int[]>> partitioned = this.addSilentTask("Partition", () -> this.partition(rules, updates));
        this.addTask("Part one", () -> this.partOne(partitioned.get(true)));
        this.addTask("Part two", () -> this.partTwo(rules, partitioned.get(false)));
    }
}
