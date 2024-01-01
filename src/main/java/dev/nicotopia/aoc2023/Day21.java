package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day21 extends DayBase {
    private char[][] map;
    private Vec2i start;

    private OptionalInt getMinSteps(Vec2i from, Vec2i to) {
        NodeDistancePair<Vec2i> result = AStar.run((p, i) -> {
            if (4 <= i) {
                return null;
            } else if (this.isInBoundsAndFree(p.x(), p.y() - 1) && i-- == 0) {
                return new NodeDistancePair<Vec2i>(new Vec2i(p.x(), p.y() - 1), 1);
            } else if (this.isInBoundsAndFree(p.x() + 1, p.y()) && i-- == 0) {
                return new NodeDistancePair<Vec2i>(new Vec2i(p.x() + 1, p.y()), 1);
            } else if (this.isInBoundsAndFree(p.x(), p.y() + 1) && i-- == 0) {
                return new NodeDistancePair<Vec2i>(new Vec2i(p.x(), p.y() + 1), 1);
            } else if (this.isInBoundsAndFree(p.x() - 1, p.y()) && i-- == 0) {
                return new NodeDistancePair<Vec2i>(new Vec2i(p.x() - 1, p.y()), 1);
            }
            return null;
        }, from, new HashedAStarDataStructure<Vec2i>(to::manhattanDistanceTo, to::equals));
        OptionalInt r = result == null ? OptionalInt.empty() : OptionalInt.of(result.distance());
        return r;
    }

    private boolean isInBoundsAndFree(int x, int y) {
        return 0 <= x && x < this.map[0].length && 0 <= y && y < this.map.length && this.map[y][x] == '.';
    }

    private void processInput() {
        this.map = this.getPrimaryPuzzleInput().stream().map(String::toCharArray).toArray(char[][]::new);
        this.start = Vec2i.streamFromRectangle(0, 0, this.map.length, this.map[0].length)
                .filter(p -> this.map[p.y()][p.x()] == 'S').findAny().get();
        this.map[this.start.y()][this.start.x()] = '.';
    }

    private Map<Vec2i, Integer> getMinStepsToAll(Vec2i from) {
        return Vec2i.streamFromRectangle(0, 0, this.map.length, this.map.length).parallel()
                .filter(p -> this.map[p.y()][p.x()] == '.')
                .collect(Collectors.toMap(p -> p, p -> this.getMinSteps(from, p))).entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getAsInt()));
    }

    private int partOne() {
        int numSteps = this.getIntInput("numStepsPartOne");
        return this.getNumReachablesInSingleSectionFromStart(numSteps);
    }

    private int getNumReachablesInSingleSectionFromStart(int numSteps) {
        return (int) this.getMinStepsToAll(this.start).entrySet().stream()
                .filter(e -> (e.getKey().x() + e.getKey().y()) % 2 == numSteps % 2 && e.getValue() <= numSteps).count();
    }

    private long partTwo() {
        if (this.map.length != this.map[0].length) {
            throw new AocException("Not supported: Map must be a square.");
        }
        if (!Vec2i.streamFromRectangle(0, 0, this.map.length, this.map.length)
                .filter(v -> v.x() == 0 || v.y() == 0 || v.x() == this.map.length - 1 || v.y() == this.map.length - 1)
                .allMatch(v -> this.map[v.y()][v.x()] == '.')) {
            throw new AocException("Not supported: All border positions must be garden plots (.)");
        }
        if (!IntStream.range(0, this.map.length)
                .allMatch(i -> this.map[i][this.start.x()] == '.' && this.map[this.start.y()][i] == '.')) {
            throw new AocException(
                    "Not supported: Both, the start column and the start row must constist of garden plots (.) only.\nAnd yes, the example does not meet that requirement.");
        }
        int numSteps = this.getIntInput("numStepsPartTwo");
        long numReachablesOnAxes = Arrays
                .asList(new Vec2i(this.start.x(), 0), new Vec2i(this.start.x(), this.map.length - 1),
                        new Vec2i(0, this.start.y()), new Vec2i(this.map.length - 1, this.start.y()))
                .stream().mapToLong(p -> this.getNumReachablesOnAxis(p, numSteps)).sum();
        long numReachablesInQuadrants = Arrays
                .asList(new Vec2i(0, 0), new Vec2i(0, this.map.length - 1), new Vec2i(this.map.length - 1, 0),
                        new Vec2i(this.map.length - 1, this.map.length - 1))
                .stream().mapToLong(p -> this.getNumReachablesInQuadrant(p, numSteps)).sum();
        return numReachablesOnAxes + numReachablesInQuadrants + this.getNumReachablesInSingleSectionFromStart(numSteps);
    }

    private long getNumReachablesInQuadrant(Vec2i base, int numSteps) {
        Vec2i startSectionCorner = new Vec2i(this.map.length - 1 - base.x(), this.map.length - 1 - base.y());
        int numStepsStartToBase = this.getMinSteps(this.start, startSectionCorner).getAsInt() + 2;
        return this.getNumReachables(base, numStepsStartToBase, numSteps - numStepsStartToBase, true);
    }

    private long getNumReachablesOnAxis(Vec2i base, int numSteps) {
        int numStepsStartToBase = this.map.length / 2 + 1;
        return this.getNumReachables(base, numStepsStartToBase, numSteps - numStepsStartToBase, false);
    }

    private long getNumReachables(Vec2i base, int numStepsStartToBase, int numRemSteps, boolean quadrant) {
        if (numRemSteps < 0) {
            return 0;
        }
        Predicate<Vec2i> isEven = p -> (p.x() + p.y()) % 2 == (base.x() + base.y()) % 2;
        Map<Vec2i, Integer> minStepsBaseToAll = this.getMinStepsToAll(base);

        int minStepsForAllEven = minStepsBaseToAll.entrySet().stream().filter(e -> isEven.test(e.getKey()))
                .mapToInt(e -> e.getValue()).max().getAsInt();
        int minStepsForAllOdd = minStepsBaseToAll.entrySet().stream().filter(e -> !isEven.test(e.getKey()))
                .mapToInt(e -> e.getValue()).max().getAsInt();
        long numEven = minStepsBaseToAll.keySet().stream().filter(isEven).count();
        long numOdd = minStepsBaseToAll.keySet().stream().filter(isEven.negate()).count();

        long result = 0;
        for (int i = 1; 0 <= numRemSteps; ++i) {
            long numReachables;
            if (numRemSteps % 2 == 0 && minStepsForAllEven <= numRemSteps) {
                numReachables = numEven;
            } else if (numRemSteps % 2 == 1 && minStepsForAllOdd <= numRemSteps) {
                numReachables = numOdd;
            } else {
                final int n = numRemSteps;
                numReachables = minStepsBaseToAll.entrySet().stream()
                        .filter(e -> isEven.test(e.getKey()) == (n % 2 == 0) && e.getValue() <= n).count();
            }
            result += (quadrant ? i : 1) * numReachables;
            numRemSteps -= this.map.length;
        }
        return result;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("numStepsPartOne", 64);
        this.pushSecondaryInput("numStepsPartTwo", 26501365);
        this.addPresetFromResource("Example", "/2023/day21e.txt", 6, 5000);
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}