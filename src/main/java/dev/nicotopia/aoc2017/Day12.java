package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Day12 {
    public static void main(String[] args) throws IOException {
        Map<Integer, Set<Integer>> nodes = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day12.class.getResourceAsStream("/2017/day12.txt")))) {
            br.lines().map(l -> l.split(" <-> ")).forEach(s -> nodes.put(Integer.valueOf(s[0]),
                    new HashSet<>(Arrays.stream(s[1].split(", ")).map(Integer::valueOf).toList())));
        }
        Set<Integer> program0Group = collect(nodes, 0);
        System.out.println("Part one: " + program0Group.size());
        int groupCount = 0;
        while (!nodes.isEmpty()) {
            Set<Integer> group = collect(nodes, nodes.keySet().iterator().next());
            group.forEach(nodes::remove);
            nodes.values().forEach(l -> l.removeAll(group));
            ++groupCount;
        }
        System.out.println("Part two: " + groupCount);
    }

    private static Set<Integer> collect(Map<Integer, Set<Integer>> nodes, int node) {
        Stack<Integer> stack = new Stack<>();
        Set<Integer> visited = new HashSet<>();
        stack.push(node);
        visited.add(node);
        while (!stack.isEmpty()) {
            for (Integer n : nodes.get(stack.pop())) {
                if (visited.add(n)) {
                    stack.push(n);
                }
            }
        }
        return visited;
    }
}