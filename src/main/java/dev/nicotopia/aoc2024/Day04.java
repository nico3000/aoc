package dev.nicotopia.aoc2024;

import java.util.stream.IntStream;

import dev.nicotopia.Compass8;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day04 extends DayBase {
    private boolean findXmas(CharMap2D map, Vec2i s, Vec2i d) {
        return IntStream.range(0, 4).allMatch(n -> map.is(d.mad(n, s), "XMAS".charAt(n)));
    }

    private long countXmas(CharMap2D map, Vec2i start) {
        return Vec2i.ORIGIN.neighbours8().filter(d -> this.findXmas(map, start, d)).count();
    }

    private long partOne(CharMap2D map) {
        return map.coordinates().mapToLong(p -> this.countXmas(map, p)).sum();
    }

    private boolean isXmas(CharMap2D map, Vec2i center) {
        boolean isMasA = map.is(center.getNeighbour(Compass8.NW), 'M') && map.is(center.getNeighbour(Compass8.SE), 'S');
        boolean isSamA = map.is(center.getNeighbour(Compass8.NW), 'S') && map.is(center.getNeighbour(Compass8.SE), 'M');
        boolean isMasB = map.is(center.getNeighbour(Compass8.NE), 'M') && map.is(center.getNeighbour(Compass8.SW), 'S');
        boolean isSamB = map.is(center.getNeighbour(Compass8.NE), 'S') && map.is(center.getNeighbour(Compass8.SW), 'M');
        return map.get(center) == 'A' && (isMasA || isSamA) && (isMasB || isSamB);
    }

    private long partTwo(CharMap2D map) {
        return map.coordinates().filter(p -> this.isXmas(map, p)).count();
    }

    @Override
    public void run() {
        CharMap2D map = this.getPrimaryPuzzleInputAsCharMap2D();
        this.addTask("Part one", () -> this.partOne(map));
        this.addTask("Part two", () -> this.partTwo(map));
    }
}
