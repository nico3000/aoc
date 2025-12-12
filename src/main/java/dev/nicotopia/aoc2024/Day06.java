package dev.nicotopia.aoc2024;

import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day06 extends DayBase {
    private record DirectedPosition(Vec2i pos, Compass4 dir) {
    }

    private char map[][];

    private OptionalInt simulate(Vec2i additionalObstacle) {
        Vec2i pos = Vec2i.streamCoordinatesFor(this.map).filter(p -> this.map[p.y()][p.x()] == '^').findAny().get();
        Compass4 dir = Compass4.N;
        Set<Vec2i> uniquePositions = new HashSet<>();
        uniquePositions.add(pos);
        Set<DirectedPosition> uniqueDirectedPositions = new HashSet<>();
        uniqueDirectedPositions.add(new DirectedPosition(pos, dir));
        for (;;) {
            Vec2i next = pos.getNeighbour(dir);
            if (next.y() < 0 || this.map.length <= next.y() || next.x() < 0 || this.map[next.y()].length <= next.x()) {
                return OptionalInt.of(uniquePositions.size());
            } else if (this.map[next.y()][next.x()] == '#' || next.equals(additionalObstacle)) {
                dir = switch (dir) {
                    case N -> Compass4.E;
                    case E -> Compass4.S;
                    case S -> Compass4.W;
                    case W -> Compass4.N;
                };
            } else {
                pos = next;
                uniquePositions.add(pos);
                if (!uniqueDirectedPositions.add(new DirectedPosition(pos, dir))) {
                    return OptionalInt.empty();
                }
            }
        }
    }

    private int partOne() {
        return this.simulate(null).getAsInt();
    }

    private long partTwo() {
        return Vec2i.streamCoordinatesFor(this.map).parallel()
                .filter(p -> this.map[p.y()][p.x()] == '.' && this.simulate(p).isEmpty()).count();
    }

    @Override
    public void run() {
        this.map = this.getPrimaryPuzzleInputAs2DCharArray();
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
