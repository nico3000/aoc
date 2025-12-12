package dev.nicotopia.aoc2025;

import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.Compass8;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day07 extends DayBase {
    private CharMap2D map;
    private final Map<Vec2i, Long> knownTimelineSplitCounts = new HashMap<>();
    private int uniqueSplitCount = 0;

    public long getTimelineSplitCount(Vec2i base) {
        if (!this.map.isInBounds(base)) {
            return 1;
        }
        Long count = this.knownTimelineSplitCounts.get(base);
        if (count != null) {
            return count;
        }
        Vec2i below = base.getNeighbour(Compass8.S);
        if (this.map.is(below, '^')) {
            ++this.uniqueSplitCount;
            count = this.getTimelineSplitCount(base.getNeighbour(Compass8.SW))
                    + this.getTimelineSplitCount(base.getNeighbour(Compass8.SE));
        } else {
            count = this.getTimelineSplitCount(below);
        }
        this.knownTimelineSplitCounts.put(base, count);
        return count;
    }

    @Override
    public void run() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        Vec2i start = this.map.findAnyPositionOf('S').get();
        long timelineSplitCount = this.addSilentTask("Algorithm", () -> this.getTimelineSplitCount(start));
        this.addTask("Part one", () -> this.uniqueSplitCount);
        this.addTask("Part two", () -> timelineSplitCount);
    }
}
