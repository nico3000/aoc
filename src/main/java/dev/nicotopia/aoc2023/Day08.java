package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.Pair;
import dev.nicotopia.Util;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day08 extends DayBase {
    private class Node {
        private final String name;
        private Node left;
        private Node right;
        private boolean endNode = false;

        public Node(String name) {
            this.name = name;
        }

        public void setChildren(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private final Map<String, Node> nodes = new HashMap<>();

    private void processInput() {
        Pattern p = Pattern.compile("(.{3}) = \\((.{3}), (.{3})\\)");
        Map<Node, Pair<String, String>> tempNodes = this.getPrimaryPuzzleInput().stream().map(p::matcher)
                .filter(Matcher::matches).map(m -> new Pair<>(new Node(m.group(1)), new Pair<>(m.group(2), m.group(3))))
                .collect(Collectors.toMap(Pair::first, Pair::second));
        tempNodes.keySet().stream().forEach(n -> this.nodes.put(n.name, n));
        this.nodes.values().stream().forEach(n -> n.setChildren(this.nodes.get(tempNodes.get(n).first()),
                this.nodes.get(tempNodes.get(n).second())));
    }

    private long partOne() {
        String instructions = this.getPrimaryPuzzleInput().getFirst();
        Node node = this.nodes.get("AAA");
        if (node == null) {
            throw new AocException("No AAA node found");
        }
        for (long steps = 0;; ++steps) {
            if (node.name.equals("ZZZ")) {
                return steps;
            }
            node = switch (instructions.charAt((int) (steps % instructions.length()))) {
                case 'L' -> node.left;
                case 'R' -> node.right;
                default -> throw new RuntimeException();
            };
        }
    }

    private record NodeInfo(Node node, long loopStart, long loopSize, List<Long> endNodeSteps) {
    }

    private NodeInfo createNodeInfo(Node startNode, String instructions) {
        List<Long> endNodeSteps = new ArrayList<>();
        Map<Node, List<Long>> endNodeOccurences = new HashMap<>();
        Node n = startNode;
        for (long step = 0;; ++step) {
            if (n.endNode) {
                if (!endNodeOccurences.containsKey(n)) {
                    endNodeOccurences.put(n, new ArrayList<>());
                }
                List<Long> occurences = endNodeOccurences.get(n);
                for (int i = 0; i < occurences.size(); ++i) {
                    if (occurences.get(i) % instructions.length() == step % instructions.length()) {
                        return new NodeInfo(startNode, i, step - occurences.get(i), endNodeSteps);
                    }
                }
                occurences.add(step);
                endNodeSteps.add(step);
            }
            n = switch (instructions.charAt((int) (step % instructions.length()))) {
                case 'L' -> n.left;
                case 'R' -> n.right;
                default -> throw new RuntimeException();
            };
        }
    }

    private NodeInfo simplify(NodeInfo ni) {
        if (ni.loopStart == 0 && !ni.endNodeSteps.isEmpty() && IntStream.range(0, ni.endNodeSteps.size())
                .allMatch(i -> ni.endNodeSteps.get(i) == (i + 1) * ni.endNodeSteps.getFirst())) {
            return new NodeInfo(ni.node, 0, ni.endNodeSteps.getFirst(), Arrays.asList(ni.endNodeSteps.getFirst()));
        }
        return ni;
    }

    private long partTwo() {
        String instructions = this.getPrimaryPuzzleInput().getFirst();
        this.nodes.values().forEach(n -> n.endNode = n.name.endsWith("Z"));
        List<NodeInfo> startNodeInfos = this.nodes.values().stream().filter(n -> n.name.endsWith("A"))
                .map(n -> this.createNodeInfo(n, instructions)).map(this::simplify).toList();
        if (startNodeInfos.stream().allMatch(sni -> sni.loopStart == 0 && sni.endNodeSteps.size() == 1
                && sni.endNodeSteps.getFirst() == sni.loopSize)) {
            return startNodeInfos.stream().mapToLong(sni -> sni.loopSize).reduce(Util::lcm)
                    .orElse(startNodeInfos.getFirst().loopSize);
        } else {
            throw new AocException(
                    "For now the only supported graph is when all start nodes lead directly into a\nloop that does not contain the start node and exactly one end point. Examples 1\nand 2 do not meet these requirements.");
        }
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}