package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Day12 {
    private static record Edge(String nodeA, String nodeB) {
        public String getOther(String node) {
            return !this.nodeA.equals(node) ? !this.nodeB.equals(node) ? null : this.nodeA : this.nodeB;
        }
    }

    private static class Graph {
        public static interface PathRestriction {
            public boolean isValidNextNode(List<String> subPath, String nextNode);
        }

        private final List<Edge> edges = new LinkedList<>();
        private final Stack<String> currentPath = new Stack<>();
        private final List<List<String>> paths = new LinkedList<>();

        public List<List<String>> findPaths(String from, String to, PathRestriction pathRestriction) {
            this.paths.clear();
            this.currentPath.clear();
            this.currentPath.push(from);
            this.findPaths(to, pathRestriction);
            return Collections.unmodifiableList(new LinkedList<>(this.paths));
        }

        private void findPaths(String to, PathRestriction pathRestriction) {
            if (this.currentPath.peek().equals(to)) {
                this.paths.add(Collections.unmodifiableList(new LinkedList<>(this.currentPath)));
            } else {
                this.edges.stream().map(e -> e.getOther(this.currentPath.peek()))
                        .filter(n -> n != null && pathRestriction.isValidNextNode(this.currentPath, n)).forEach(n -> {
                            this.currentPath.push(n);
                            this.findPaths(to, pathRestriction);
                            this.currentPath.pop();
                        });
            }
        }
    }

    public static void main(String args[]) throws IOException {
        Graph g = new Graph();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day11.class.getResourceAsStream("/2021/day12.txt")))) {
            g.edges.addAll(br.lines().map(l -> l.split("-")).map(split -> new Edge(split[0], split[1])).toList());
        }
        List<List<String>> partOnePaths = g.findPaths("start", "end",
                (p, n) -> !n.toLowerCase().equals(n) || !p.contains(n));
        List<List<String>> partTwoPaths = g.findPaths("start", "end",
                (p, n) -> {
                    if (!n.toLowerCase().equals(n) || !p.contains(n)) {
                        return true;
                    } else if (n.equals("start") || n.equals("end")) {
                        return false;
                    } else {
                        List<String> smallCaves = p.stream().filter(_n -> _n.toLowerCase().equals(_n)).toList();
                        return smallCaves.stream().distinct().count() == smallCaves.size();
                    }
                });
        System.out.printf("Path count, part one: %d; part two: %d\n", partOnePaths.size(), partTwoPaths.size());
    }
}