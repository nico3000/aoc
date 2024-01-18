package dev.nicotopia.aoc2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.Pair;
import dev.nicotopia.Util;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Axis3D;
import dev.nicotopia.aoc.algebra.Vec3i;

public class Day12 extends DayBase {
    private class State {
        int values[];

        public State(List<Integer> values) {
            this.values = values.stream().mapToInt(Integer::valueOf).toArray();
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.values);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof State o && Arrays.equals(this.values, o.values);
        }
    }

    private final List<Vec3i> positions = new ArrayList<>();
    private final List<Vec3i> velocities = new ArrayList<>();

    private State getState(Axis3D axis) {
        ToIntFunction<Vec3i> get = switch (axis) {
            case X -> v -> v.x();
            case Y -> v -> v.y();
            case Z -> v -> v.z();
        };
        List<Integer> values = new ArrayList<>(2 * this.positions.size());
        for (int i = 0; i < this.positions.size(); ++i) {
            values.add(get.applyAsInt(this.positions.get(i)));
            values.add(get.applyAsInt(this.velocities.get(i)));
        }
        return new State(values);
    }

    private void processInput() {
        Pattern p = Pattern.compile("<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>");
        this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).map(
                m -> new Vec3i(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3))))
                .forEach(this.positions::add);
        IntStream.range(0, this.positions.size()).forEach(i -> this.velocities.add(Vec3i.ORIGIN));
    }

    private void step() {
        for (int i = 0; i < this.positions.size(); ++i) {
            Vec3i p = this.positions.get(i);
            Vec3i v = this.velocities.get(i);
            for (int j = 0; j < this.positions.size(); ++j) {
                Vec3i q = this.positions.get(j);
                v = v.add(new Vec3i(p.x() < q.x() ? 1 : p.x() == q.x() ? 0 : -1,
                        p.y() < q.y() ? 1 : p.y() == q.y() ? 0 : -1, p.z() < q.z() ? 1 : p.z() == q.z() ? 0 : -1));
            }
            this.velocities.set(i, v);
        }
        for (int i = 0; i < this.positions.size(); ++i) {
            this.positions.set(i, this.positions.get(i).add(this.velocities.get(i)));
        }
    }

    private int partOne() {
        for (int i = 0; i < this.getIntInput("numSteps"); ++i) {
            this.step();
        }
        return IntStream.range(0, this.positions.size()).map(i -> this.positions.get(i).manhattanDistance(Vec3i.ORIGIN)
                * this.velocities.get(i).manhattanDistance(Vec3i.ORIGIN)).sum();
    }

    private long partTwo() {
        Map<State, Integer> statesX = new HashMap<>();
        Map<State, Integer> statesY = new HashMap<>();
        Map<State, Integer> statesZ = new HashMap<>();
        Optional<Pair<Integer, Integer>> loopX = Optional.empty();
        Optional<Pair<Integer, Integer>> loopY = Optional.empty();
        Optional<Pair<Integer, Integer>> loopZ = Optional.empty();
        for (int i = 0; loopX.isEmpty() || loopY.isEmpty() || loopZ.isEmpty(); ++i) {
            if (loopX.isEmpty()) {
                Integer prev = statesX.put(this.getState(Axis3D.X), i);
                if (prev != null) {
                    loopX = Optional.of(new Pair<>(prev, i));
                }
            }
            if (loopY.isEmpty()) {
                Integer prev = statesY.put(this.getState(Axis3D.Y), i);
                if (prev != null) {
                    loopY = Optional.of(new Pair<>(prev, i));
                }
            }
            if (loopZ.isEmpty()) {
                Integer prev = statesZ.put(this.getState(Axis3D.Z), i);
                if (prev != null) {
                    loopZ = Optional.of(new Pair<>(prev, i));
                }
            }
            this.step();
        }
        if (loopX.get().first() != 0 || loopY.get().first() != 0 || loopZ.get().first() != 0) {
            throw new AocException("Not supported. The loop must begin with the initial state.");
        }
        return Util.lcm(Util.lcm((long) loopX.get().second(), (long) loopY.get().second()),
                (long) loopZ.get().second());
    }

    @Override
    public void run() {
        this.pushSecondaryInput("numSteps", 1000);
        this.addPresetFromResource("Example 1", "/2019/day12e1.txt", 10);
        this.addPresetFromResource("Example 2", "/2019/day12e2.txt", 100);
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.positions.clear();
        this.velocities.clear();
        this.addTask("Restore input", this::processInput);
        this.addTask("Part two", this::partTwo);
    }
}
