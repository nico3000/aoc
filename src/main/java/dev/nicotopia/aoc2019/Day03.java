package dev.nicotopia.aoc2019;

import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day03 extends DayBase {
    private Map<Vec2i, Integer> wireA;
    private Map<Vec2i, Integer> wireB;

    private Map<Vec2i, Integer> getWirePositions(String input) {
        Vec2i p = Vec2i.ORIGIN;
        Map<Vec2i, Integer> positions = new HashMap<>();
        int c = 0;
        for (String segment : input.split(",")) {
            Vec2i delta = switch (segment.charAt(0)) {
                case 'U' -> new Vec2i(0, -1);
                case 'R' -> new Vec2i(1, 0);
                case 'D' -> new Vec2i(0, 1);
                case 'L' -> new Vec2i(-1, 0);
                default -> throw new AocException("illegal direction");
            };
            int steps = Integer.valueOf(segment.substring(1));
            for (int i = 0; i < steps; ++i) {
                positions.put(p = p.add(delta), ++c);
            }
        }
        return positions;
    }

    private void processInput() {
        this.wireA = this.getWirePositions(this.getPrimaryPuzzleInput().get(0));
        this.wireB = this.getWirePositions(this.getPrimaryPuzzleInput().get(1));
    }

    private int partOne() {
        return this.wireA.keySet().stream().filter(this.wireB.keySet()::contains)
                .mapToInt(Vec2i.ORIGIN::manhattanDistanceTo).min().getAsInt();
    }

    private int partTwo() {
        return this.wireA.keySet().stream().filter(this.wireB.keySet()::contains)
                .mapToInt(p -> this.wireA.get(p) + this.wireB.get(p)).min().getAsInt();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}