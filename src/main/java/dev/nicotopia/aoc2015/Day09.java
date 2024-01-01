package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.graphlib.TravelingSalesman;

public class Day09 {
        public static void main(String[] args) throws IOException {
                Map<Set<String>, Integer> edges = new HashMap<>();
                try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(Day09.class.getResourceAsStream("/2015/day09.txt")))) {
                        Pattern p = Pattern.compile("([a-zA-Z]+) to ([a-zA-Z]+) = ([0-9]+)");
                        br.lines().map(p::matcher).filter(Matcher::matches).forEach(
                                        m -> edges.put(new HashSet<>(Arrays.asList(m.group(1), m.group(2))),
                                                        Integer.valueOf(m.group(3))));
                }
                List<String> nodes = edges.keySet().stream()
                                .mapMulti((Set<String> s, Consumer<String> m) -> s.forEach(m))
                                .distinct().toList();
                int shortestRoute = nodes.stream()
                                .mapToInt(n -> TravelingSalesman.run(nodes,
                                                (a, b) -> edges.get(new HashSet<>(Arrays.asList(a, b))),
                                                Optional.of(n)))
                                .min().getAsInt();
                int longestRoute = -nodes.stream()
                                .mapToInt(n -> TravelingSalesman.run(nodes, (a, b) -> Optional
                                                .ofNullable(edges.get(new HashSet<>(Arrays.asList(a, b)))).map(i -> -i)
                                                .orElse(null), Optional.of(n)))
                                .min().getAsInt();
                System.out.printf("Part one: %d\nPart two: %d\n", shortestRoute, longestRoute);
        }
}