package dev.nicotopia.aoc2023;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day14 extends DayBase {
    private class Dish {
        private final int numRows = Day14.this.getPrimaryPuzzleInput().size();
        private final int numCols = Day14.this.getPrimaryPuzzleInput().getFirst().length();
        private final BitSet obstacles = new BitSet();
        private final BitSet stones = new BitSet();

        public Dish() {
            Vec2i.streamFromRectangle(0, 0, this.numCols, this.numRows).forEach(rc -> {
                switch (Day14.this.getPrimaryPuzzleInput().get(rc.x()).charAt(rc.y())) {
                    case 'O':
                        this.setStone(rc);
                        break;
                    case '#':
                        this.setObstacle(rc);
                        break;
                }
            });
        }

        private void setObstacle(Vec2i rowAndCol) {
            this.obstacles.set(rowAndCol.y() * this.numRows + rowAndCol.x(), true);
        }

        public boolean isObstacle(Vec2i rowAndCol) {
            return this.obstacles.get(rowAndCol.y() * this.numRows + rowAndCol.x());
        }

        public void setStone(Vec2i rowAndCol) {
            this.stones.set(rowAndCol.y() * this.numRows + rowAndCol.x(), true);
        }

        public boolean isStone(Vec2i rowAndCol) {
            return this.stones.get(rowAndCol.y() * this.numRows + rowAndCol.x());
        }

        public void clear(Vec2i rowAndCol) {
            this.stones.set(rowAndCol.y() * this.numRows + rowAndCol.x(), false);
        }

        public long getTotalLoadOnNorthSupportBeams() {
            return IntStream.range(0, this.numRows).mapToLong(r -> (this.numRows - r)
                    * Vec2i.streamFromRectangle(r, 0, r + 1, this.numCols).filter(this::isStone).count()).sum();
        }

        public void tiltLine(int lineLength, Function<Integer, Vec2i> lineIdxToRowAndCol) {
            int start = 0;
            int count = 0;
            for (int i = 0; i <= lineLength; ++i) {
                Vec2i rc = lineIdxToRowAndCol.apply(i);
                if (i == lineLength || this.isObstacle(rc)) {
                    for (int j = start; j < start + count; ++j) {
                        this.setStone(lineIdxToRowAndCol.apply(j));
                    }
                    start = i + 1;
                    count = 0;
                } else if (this.isStone(rc)) {
                    if (start == i) {
                        ++start;
                    } else {
                        ++count;
                        this.clear(rc);
                    }
                }
            }
        }

        public void tiltWest() {
            IntStream.range(0, this.numRows).forEach(r -> this.tiltLine(this.numCols, idx -> new Vec2i(r, idx)));
        }

        public void tiltEast() {
            IntStream.range(0, this.numRows)
                    .forEach(r -> this.tiltLine(this.numCols, idx -> new Vec2i(r, this.numCols - 1 - idx)));
        }

        public void tiltNorth() {
            IntStream.range(0, this.numCols).forEach(c -> this.tiltLine(this.numRows, idx -> new Vec2i(idx, c)));
        }

        public void tiltSouth() {
            IntStream.range(0, this.numCols)
                    .forEach(c -> this.tiltLine(this.numRows, idx -> new Vec2i(this.numRows - 1 - idx, c)));
        }

        public void cycle() {
            this.tiltNorth();
            this.tiltWest();
            this.tiltSouth();
            this.tiltEast();
        }

        @Override
        public String toString() {
            return IntStream.range(0, this.numRows)
                    .mapToObj(r -> IntStream.range(0, this.numCols).mapToObj(
                            c -> this.isStone(new Vec2i(r, c)) ? "O" : this.isObstacle(new Vec2i(r, c)) ? "#" : ".")
                            .collect(Collectors.joining()))
                    .collect(Collectors.joining("\n"));
        }
    }

    public long partTwo() {
        Map<BitSet, Long> knownStates = new HashMap<>();
        Dish d = new Dish();
        long i = 0;
        while (!knownStates.containsKey(d.stones)) {
            knownStates.put((BitSet) d.stones.clone(), i);
            d.cycle();
            ++i;
        }
        long loopStart = knownStates.get(d.stones);
        long loopLength = i - loopStart;
        long left = (1000000000L - loopStart) % loopLength;
        for (int j = 0; j < left; ++j) {
            d.cycle();
        }
        return d.getTotalLoadOnNorthSupportBeams();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Part one", () -> {
            Dish dish = new Dish();
            dish.tiltNorth();
            return dish.getTotalLoadOnNorthSupportBeams();
        });
        this.addTask("Part two", this::partTwo);
    }
}