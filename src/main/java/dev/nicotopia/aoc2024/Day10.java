package dev.nicotopia.aoc2024;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import dev.nicotopia.Compass;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day10 extends DayBase {
    private record HikingTrailTop(Vec2i p, int numTrails) {
    }

    private CharMap2D map;

    private List<HikingTrailTop> getReachableTops(Vec2i start) {
        Stack<Vec2i> stack = new Stack<>();
        stack.push(start);
        Map<Vec2i, Integer> tops = new HashMap<>();
        while (!stack.isEmpty()) {
            Vec2i currentPos = stack.pop();
            char currentValue = this.map.get(currentPos);
            if (currentValue == '9') {
                tops.put(currentPos, tops.getOrDefault(currentPos, 0) + 1);
            } else {
                Arrays.stream(Compass.values()).map(currentPos::getNeighbour)
                        .filter(n -> this.map.applies(n, v -> currentValue + 1 == v)).forEach(stack::push);
            }
        }
        return tops.entrySet().stream().map(e -> new HikingTrailTop(e.getKey(), e.getValue())).toList();
    }

    private List<HikingTrailTop> getFlatHikingTrailTops() {
        return this.map.coordinates((p, c) -> c == '0').map(this::getReachableTops).flatMap(List::stream).toList();
    }

    private int partTwo(List<HikingTrailTop> flatHikingTrailTops) {
        return flatHikingTrailTops.stream().mapToInt(HikingTrailTop::numTrails).sum();
    }

    @Override
    public void run() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        List<HikingTrailTop> flatHikingTrailTops = this.addSilentTask("Preprocess", this::getFlatHikingTrailTops);
        this.addTask("Part one", flatHikingTrailTops::size);
        this.addTask("Part two", () -> this.partTwo(flatHikingTrailTops));
    }
}
