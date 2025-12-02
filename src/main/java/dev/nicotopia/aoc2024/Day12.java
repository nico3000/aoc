package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Interval;
import dev.nicotopia.aoc.algebra.IntervalSet;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day12 extends DayBase {
    private class Region {
        private final char label;
        private final Set<Vec2i> tiles = new HashSet<>();

        public Region(char label, Vec2i start) {
            this.label = label;
            Stack<Vec2i> stack = new Stack<>();
            stack.push(start);
            while (!stack.isEmpty()) {
                Vec2i p = stack.pop();
                if (this.tiles.add(p)) {
                    p.neighbours4().filter(n -> Day12.this.map.is(n, this.label)).forEach(stack::push);
                }
            }
        }

        public int getArea() {
            return this.tiles.size();
        }

        public int getPerimeterLength() {
            return (int) this.tiles.stream()
                    .mapToLong(tile -> tile.neighbours4().filter(n -> !Day12.this.map.is(n, this.label)).count()).sum();
        }

        public int getNumSides() {
            int numSides = 0;
            Pair<Vec2i, Vec2i> extents = Vec2i.getExtents(this.tiles);
            Vec2i min = extents.first();
            Vec2i max = extents.second();
            for (int x = min.x(); x <= max.x() + 1; ++x) {
                IntervalSet scanlineA = new IntervalSet(min.y(), max.y() + 1);
                IntervalSet scanlineB = new IntervalSet(min.y(), max.y() + 1);
                for (int y = min.y(); y <= max.y(); ++y) {
                    Vec2i left = new Vec2i(x - 1, y);
                    Vec2i right = new Vec2i(x, y);
                    if (!this.tiles.contains(left) || this.tiles.contains(right)) {
                        scanlineA.remove(new Interval(y, y + 1));
                    }
                    if (!this.tiles.contains(right) || this.tiles.contains(left)) {
                        scanlineB.remove(new Interval(y, y + 1));
                    }
                }
                numSides += scanlineA.getNumIntervals() + scanlineB.getNumIntervals();
            }
            for (int y = min.y(); y <= max.y() + 1; ++y) {
                IntervalSet scanlineA = new IntervalSet(min.x(), max.x() + 1);
                IntervalSet scanlineB = new IntervalSet(min.x(), max.x() + 1);
                for (int x = min.x(); x <= max.x(); ++x) {
                    Vec2i top = new Vec2i(x, y - 1);
                    Vec2i bottom = new Vec2i(x, y);
                    if (!this.tiles.contains(top) || this.tiles.contains(bottom)) {
                        scanlineA.remove(new Interval(x, x + 1));
                    }
                    if (!this.tiles.contains(bottom) || this.tiles.contains(top)) {
                        scanlineB.remove(new Interval(x, x + 1));
                    }
                }
                numSides += scanlineA.getNumIntervals() + scanlineB.getNumIntervals();
            }
            return numSides;
        }
    }

    private CharMap2D map;
    private final List<Region> regions = new ArrayList<>();

    private void buildRegions() {
        for (Vec2i p : this.map.coordinates().toList()) {
            boolean known = this.regions.stream().anyMatch(r -> r.label == this.map.get(p) && r.tiles.contains(p));
            if (!known) {
                this.regions.add(new Region(this.map.get(p), p));
            }
        }
    }

    private int partOne() {
        return regions.stream().mapToInt(r -> r.getArea() * r.getPerimeterLength()).sum();
    }

    private int partTwo() {
        return this.regions.stream().mapToInt(r -> r.getArea() * r.getNumSides()).sum();
    }

    @Override
    public void run() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        this.addTask("Process input", this::buildRegions);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
