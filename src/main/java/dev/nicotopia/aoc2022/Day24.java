package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.AStarDataStructure;
import dev.nicotopia.aoc.graphlib.BasicGraph;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day24 {
    private record Blizzard(char dir, Vec2i initialPos) {
        public Vec2i getVec2i(int timestamp, int valleyWidth, int valleyHeight) {
            return switch (this.dir) {
                case '<' -> new Vec2i((valleyWidth + (this.initialPos.x() - timestamp) % valleyWidth) % valleyWidth,
                        this.initialPos.y());
                case '^' -> new Vec2i(this.initialPos.x(),
                        (valleyHeight + (this.initialPos.y() - timestamp) % valleyHeight) % valleyHeight);
                case '>' -> new Vec2i((this.initialPos.x() + timestamp) % valleyWidth, this.initialPos.y());
                case 'v' -> new Vec2i(this.initialPos.x(), (this.initialPos.y() + timestamp) % valleyHeight);
                default -> throw new RuntimeException();
            };
        }
    }

    private record State(Vec2i p, int t) {
    }

    private static AStarDataStructure<State> createAStarDS(List<Blizzard> blizzards, int valleyWidth,
            int valleyHeight, Vec2i end, List<Set<Vec2i>> valleyStates) {
        return new HashedAStarDataStructure<>(s -> (long) Math.abs(end.x() - s.p.x()) + Math.abs(end.y() - s.p.y()),
                s -> s.p.equals(end));
    }

    private static BasicGraph<State> createGraph(List<Blizzard> blizzards, int valleyWidth, int valleyHeight,
            Vec2i end, List<Set<Vec2i>> valleyStates) {
        return (s, i) -> {
            if (valleyStates.size() == s.t + 1) {
                valleyStates.add(new HashSet<>(
                        blizzards.stream().map(b -> b.getVec2i(s.t + 1, valleyWidth, valleyHeight)).toList()));
            }
            Set<Vec2i> valleyState = valleyStates.get(s.t + 1);
            for (Vec2i n : Arrays
                    .asList(new Vec2i(s.p.x() + 1, s.p.y()), new Vec2i(s.p.x(), s.p.y() + 1),
                            new Vec2i(s.p.x() - 1, s.p.y()), new Vec2i(s.p.x(), s.p.y() - 1), s.p)
                    .stream().filter(p -> (0 <= p.x() && p.x() < valleyWidth && 0 <= p.y() && p.y() < valleyHeight)
                            || p.equals(end) || p.equals(s.p))
                    .toList()) {
                if (!valleyState.contains(n) && i-- == 0) {
                    return new NodeDistancePair<>(new State(n, s.t + 1), 1);
                }
            }
            return null;
        };
    }

    public static void main(String[] args) throws IOException {
        List<Blizzard> blizzards = new LinkedList<>();
        Vec2i start;
        Vec2i end;
        int valleyWidth;
        int valleyHeight;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day24.class.getResourceAsStream("/2022/day24.txt")))) {
            start = new Vec2i(br.readLine().indexOf('.') - 1, -1);
            int y = 0;
            String line;
            while (!(line = br.readLine()).startsWith("##") && !line.endsWith("##")) {
                final String fLine = line;
                final int fY = y;
                IntStream.range(0, line.length() - 2).filter(x -> fLine.charAt(x + 1) != '.')
                        .forEach(x -> blizzards.add(new Blizzard(fLine.charAt(x + 1), new Vec2i(x, fY))));
                ++y;
            }
            valleyWidth = line.length() - 2;
            valleyHeight = y;
            end = new Vec2i(line.indexOf('.') - 1, valleyHeight);
        }
        List<Set<Vec2i>> valleyStates = new ArrayList<>();
        valleyStates.add(new HashSet<>(blizzards.stream().map(Blizzard::initialPos).toList()));

        AStarDataStructure<State> asdsToE = createAStarDS(blizzards, valleyWidth, valleyHeight, end, valleyStates);
        BasicGraph<State> graphToE = createGraph(blizzards, valleyWidth, valleyHeight, end, valleyStates);
        long minA = AStar.run(graphToE, new State(start, 0), asdsToE).distance();
        System.out.println("Part one: " + minA);

        AStarDataStructure<State> asdsToS = createAStarDS(blizzards, valleyWidth, valleyHeight, start, valleyStates);
        BasicGraph<State> graphToS = createGraph(blizzards, valleyWidth, valleyHeight, start, valleyStates);
        long minB = AStar.run(graphToS, new State(end, (int) minA), asdsToS).distance();
        long minC = AStar.run(graphToE, new State(start, (int) (minA + minB)), asdsToE).distance();
        System.out.println("Part two: " + (minA + minB + minC));
    }
}