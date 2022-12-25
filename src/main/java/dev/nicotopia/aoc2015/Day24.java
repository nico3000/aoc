package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Day24 {
    public static void main(String[] args) throws IOException {
        List<Integer> packages;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day24.class.getResourceAsStream("/2015/day24.txt")))) {
            packages = new LinkedList<>(br.lines().map(Integer::valueOf).sorted().toList());
            Collections.reverse(packages);
        }
        int weightPerGroup = packages.stream().mapToInt(Integer::valueOf).sum() / 3;
        extractGroup(packages, new Stack<>(), 0, weightPerGroup, s -> {
            if (s.size() <= min) {
                LongAccumulator accu = new LongAccumulator((a, b) -> a * b, 1);
                s.stream().forEach(accu::accumulate);
                if (s.size() < min || accu.get() < qeMin) {
                    extractGroup(packages, new Stack<>(), 0, weightPerGroup, s2 -> {
                        System.out.printf("[%s], [%s], [%s], QE: %d\n",
                                String.join(", ", s.stream().map(p -> "" + p).toList()),
                                String.join(", ", s2.stream().map(p -> "" + p).toList()),
                                String.join(", ", packages.stream().map(p -> "" + p).toList()),
                                accu.get());
                        min = s.size();
                        qeMin = accu.get();
                    }, (_0, _1) -> qeMin != accu.get() || min != s.size());
                }
            }
        }, (stack, sum) -> stack.size() < min && weightPerGroup <= sum + packages
                .subList(0, Math.min(min - stack.size(), packages.size())).stream().mapToInt(Integer::valueOf).sum());
        System.out.println("Part one: " + qeMin);
        min = Integer.MAX_VALUE;
        qeMin = Long.MAX_VALUE;
        int weightPerGroup2 = packages.stream().mapToInt(Integer::valueOf).sum() / 4;
        extractGroup(packages, new Stack<>(), 0, weightPerGroup2, s -> {
            if (s.size() <= min) {
                LongAccumulator accu = new LongAccumulator((a, b) -> a * b, 1);
                s.stream().forEach(accu::accumulate);
                if (s.size() < min || accu.get() < qeMin) {
                    extractGroup(packages, new Stack<>(), 0, weightPerGroup2, s2 -> {
                        extractGroup(packages, new Stack<>(), 0, weightPerGroup2, s3 -> {
                            System.out.printf("[%s], [%s], [%s], [%s], QE: %d\n",
                                    String.join(", ", s.stream().map(p -> "" + p).toList()),
                                    String.join(", ", s2.stream().map(p -> "" + p).toList()),
                                    String.join(", ", s3.stream().map(p -> "" + p).toList()),
                                    String.join(", ", packages.stream().map(p -> "" + p).toList()),
                                    accu.get());
                            min = s.size();
                            qeMin = accu.get();
                        }, (_0, _1) -> qeMin != accu.get() || min != s.size());
                    }, (_0, _1) -> qeMin != accu.get() || min != s.size());
                }
            }
        }, (stack, sum) -> stack.size() < min && weightPerGroup2 <= sum + packages
                .subList(0, Math.min(min - stack.size(), packages.size())).stream().mapToInt(Integer::valueOf).sum());
        System.out.println("Part two: " + qeMin);
    }

    private static int min = Integer.MAX_VALUE;
    private static long qeMin = Long.MAX_VALUE;

    private static void extractGroup(List<Integer> base, Stack<Integer> current, int currentSum, int targetSum,
            Consumer<Stack<Integer>> onFound, BiFunction<Stack<Integer>, Integer, Boolean> shouldContinue) {
        if (currentSum == targetSum) {
            onFound.accept(current);
        } else if (shouldContinue.apply(current, currentSum)) {
            for (int i = 0; i < base.size(); ++i) {
                if (currentSum + base.get(i) <= targetSum) {
                    current.push(base.remove(i));
                    extractGroup(base, current, currentSum + current.peek(), targetSum, onFound, shouldContinue);
                    base.add(i, current.pop());
                }
            }
        }
    }
}