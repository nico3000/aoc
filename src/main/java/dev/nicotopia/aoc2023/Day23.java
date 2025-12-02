package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import dev.nicotopia.Compass;
import dev.nicotopia.Pair;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day23 extends DayBase {
    private class Node {
        private final Vec2i pos;
        private final Map<Node, Integer> neighbours = new HashMap<>();

        public Node(Vec2i pos) {
            this.pos = pos;
        }

        public Node(int x, int y) {
            this.pos = new Vec2i(x, y);
        }

        @Override
        public String toString() {
            return this.pos.toString();
        }
    }

    private final Set<Node> nodes = new HashSet<>();
    private Node start;
    private Node end;

    private void buildGraph() {
        char[][] map = this.getPrimaryPuzzleInputAs2DCharArray();
        Function<Vec2i, Integer> getNonWallNeighbours = p -> (map[p.y() - 1][p.x()] != '#' ? 1 : 0)
                + (map[p.y()][p.x() + 1] != '#' ? 1 : 0) + (map[p.y() + 1][p.x()] != '#' ? 1 : 0)
                + (map[p.y()][p.x() - 1] != '#' ? 1 : 0);
        Predicate<Vec2i> isCrossing = p -> map[p.y()][p.x()] != '#' && 3 <= getNonWallNeighbours.apply(p);
        Predicate<Vec2i> isSlope = p -> map[p.y()][p.x()] != '.' && map[p.y()][p.x()] != '#';
        if (Vec2i.streamFromRectangle(1, 1, map[0].length - 1, map.length - 1).filter(isSlope)
                .anyMatch(p -> !isCrossing.test(new Vec2i(p.x(), p.y() - 1))
                        && !isCrossing.test(new Vec2i(p.x() + 1, p.y()))
                        && !isCrossing.test(new Vec2i(p.x(), p.y() + 1))
                        && !isCrossing.test(new Vec2i(p.x() - 1, p.y())))) {
            throw new AocException("Not supported: All slopes must have crossings as neighbours");
        }
        if (Vec2i.streamFromRectangle(1, 1, map[0].length - 1, map.length - 1).filter(isCrossing)
                .anyMatch(p -> map[p.y() - 1][p.x()] == '.' || map[p.y()][p.x() + 1] == '.'
                        || map[p.y() + 1][p.x()] == '.' || map[p.y()][p.x() - 1] == '.')) {
            throw new AocException("Not supported: All neighbours of a crossing must be slopes.");
        }

        Map<Vec2i, Node> nodeMap = new HashMap<>();
        this.start = new Node(String.valueOf(map[0]).indexOf('.'), 0);
        this.end = new Node(String.valueOf(map[map.length - 1]).indexOf('.'), map.length - 1);
        nodeMap.put(this.start.pos, this.start);
        nodeMap.put(this.end.pos, this.end);
        Stack<Pair<Node, Compass>> stack = new Stack<>();
        stack.add(new Pair<>(start, Compass.S));
        while (!stack.isEmpty()) {
            Pair<Node, Compass> origin = stack.pop();
            Compass dir = origin.second();
            Vec2i p = new Vec2i(origin.first().pos.x() + switch (dir) {
                case E -> 1;
                case W -> -1;
                default -> 0;
            }, origin.first().pos.y() + switch (dir) {
                case N -> -1;
                case S -> 1;
                default -> 0;
            });
            for (int i = 1;; ++i) {
                if (nodeMap.containsKey(p)) {
                    origin.first().neighbours.put(nodeMap.get(p), i);
                    break;
                } else if (isCrossing.test(p)) {
                    Node c = new Node(p);
                    nodeMap.put(p, c);
                    origin.first().neighbours.put(c, i);
                    if (map[p.y() - 1][p.x()] == '^') {
                        stack.push(new Pair<>(c, Compass.N));
                    }
                    if (map[p.y()][p.x() + 1] == '>') {
                        stack.push(new Pair<>(c, Compass.E));
                    }
                    if (map[p.y() + 1][p.x()] == 'v') {
                        stack.push(new Pair<>(c, Compass.S));
                    }
                    if (map[p.y()][p.x() - 1] == '<') {
                        stack.push(new Pair<>(c, Compass.W));
                    }
                    break;
                } else if (dir != Compass.S && map[p.y() - 1][p.x()] != '#') {
                    p = new Vec2i(p.x(), p.y() - 1);
                    dir = Compass.N;
                } else if (dir != Compass.W && map[p.y()][p.x() + 1] != '#') {
                    p = new Vec2i(p.x() + 1, p.y());
                    dir = Compass.E;
                } else if (dir != Compass.N && map[p.y() + 1][p.x()] != '#') {
                    p = new Vec2i(p.x(), p.y() + 1);
                    dir = Compass.S;
                } else if (dir != Compass.E && map[p.y()][p.x() - 1] != '#') {
                    p = new Vec2i(p.x() - 1, p.y());
                    dir = Compass.W;
                } else {
                    break;
                }
            }
        }
        this.nodes.addAll(nodeMap.values());
    }

    public int longestPath() {
        List<List<Node>> paths = new ArrayList<>();
        Stack<List<Node>> stack = new Stack<>();
        stack.push(new ArrayList<>(Arrays.asList(this.start)));
        while (!stack.isEmpty()) {
            List<Node> path = stack.pop();
            if (path.getLast() == this.end) {
                paths.add(path);
            } else {
                path.getLast().neighbours.keySet().stream().filter(n -> !path.contains(n))
                        .forEach(n -> stack.push(new ArrayList<>(path)).add(n));
            }
        }
        return paths.stream()
                .mapToInt(p -> IntStream.range(0, p.size() - 1).map(i -> p.get(i).neighbours.get(p.get(i + 1))).sum())
                .max().getAsInt();
    }

    public int partTwo() {
        this.nodes.forEach(n -> n.neighbours.entrySet().forEach(e -> e.getKey().neighbours.put(n, e.getValue())));
        return this.longestPath();
    }

    @Override
    public void run() {
        this.addTask("Build graph", this::buildGraph);
        this.addTask("Part one", this::longestPath);
        this.addTask("Part two", this::partTwo);
    }
}