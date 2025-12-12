package dev.nicotopia.aoc2025;

import java.util.stream.Stream;

import dev.nicotopia.Compass8;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day04 extends DayBase {
    private boolean isAccessible(CharMap2D map, Vec2i pos) {
        return Stream.of(Compass8.values()).filter(n -> map.is(pos.getNeighbour(n), '@')).count() < 4;
    }

    private long partOne(CharMap2D map) {
        return map.coordinates((p, c) -> c == '@').filter(p -> this.isAccessible(map, p)).count();
    }

    private int partTwo(CharMap2D map) {
        return map.update((p, c) -> c == '@' && this.isAccessible(map, p) ? '.' : null, true);
    }

    @Override
    public void run() {
        CharMap2D map = this.getPrimaryPuzzleInputAsCharMap2D();
        this.addTask("Part one", () -> this.partOne(map));
        this.addTask("Part two", () -> this.partTwo(map));
    }
}
