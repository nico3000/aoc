package dev.nicotopia.aoc2024;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day20 extends DayBase {
    private CharMap2D map;
    private Vec2i start;
    private Vec2i end;
    private final Map<Pair<Vec2i, Vec2i>, OptionalInt> distances = new HashMap<>();

    private void preprocess() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        this.start = this.map.findAnyPositionOf('S').get();
        this.end = this.map.findAnyPositionOf('E').get();
        this.map.set(this.start, '.');
        this.map.set(this.end, '.');
        OptionalInt d[][] = this.map.getShortestDistances(this.start, '.');
        this.map.coordinates((p, c) -> c == '.').forEach(p -> {
            this.distances.put(new Pair<>(p, this.end), this.map.getShortestDistance(p, this.end, '.'));
            this.distances.put(new Pair<>(this.start, p), d[p.y()][p.x()]);
        });
    }

    private long getNumEffectiveCheats(int cheatDuration, int minSaving) {
        int baseDistance = this.distances.get(new Pair<>(this.start, this.end)).getAsInt();
        return this.map.coordinates((p, c) -> c == '.').mapToLong(p -> {
            long count = 0;
            OptionalInt toHere = this.distances.get(new Pair<>(this.start, p));
            if (toHere.isPresent()) {
                count += p.manhattanSphere(cheatDuration).filter(cheatEnd -> this.map.is(cheatEnd, '.'))
                        .filter(cheatEnd -> {
                            OptionalInt fromCheatEnd = this.distances.get(new Pair<>(cheatEnd, this.end));
                            if (fromCheatEnd.isPresent()) {
                                int cheatDistance = p.manhattanDistanceTo(cheatEnd);
                                int totalDistance = toHere.getAsInt() + cheatDistance + fromCheatEnd.getAsInt();
                                if (minSaving <= baseDistance - totalDistance) {
                                    return true;
                                }
                            }
                            return false;
                        }).count();
            }
            return count;
        }).sum();
    }

    @Override
    public void run() {
        this.pushSecondaryInput("Min saving", 100);
        this.addTask("Preprocess", this::preprocess);
        this.addTask("Part one", () -> this.getNumEffectiveCheats(2, this.getIntInput("Min saving")));
        this.addTask("Part two", () -> this.getNumEffectiveCheats(20, this.getIntInput("Min saving")));
    }
}