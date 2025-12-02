package dev.nicotopia.aoc2018;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.DayBase;

public class Day06 extends DayBase {
    private record Coordinate(int x, int y) {
        public int distanceTo(Coordinate other) {
            return Math.abs(other.x - this.x) + Math.abs(other.y - this.y);
        }

        public boolean isOnBorder(Coordinate min, Coordinate max) {
            return min.x == this.x || max.x == this.x || min.y == this.y || max.y == this.y;
        }
    }

    private Optional<Coordinate> findNearest(List<Coordinate> coords, Coordinate c) {
        var sorted = coords.stream().sorted((a, b) -> Integer.compare(a.distanceTo(c), b.distanceTo(c))).toList();
        return sorted.get(0).distanceTo(c) == sorted.get(1).distanceTo(c) ? Optional.empty()
                : Optional.of(sorted.get(0));
    }

    private int getArea(List<Coordinate> coords, Coordinate min, Coordinate max, Coordinate start) {
        Set<Coordinate> visited = new HashSet<>();
        Stack<Coordinate> stack = new Stack<>();
        stack.push(start);
        int area = 0;
        while (!stack.isEmpty()) {
            Coordinate c = stack.pop();
            if (!visited.contains(c)) {
                var nearest = findNearest(coords, c);
                if (nearest.isPresent() && nearest.get() == start) {
                    if (c.isOnBorder(min, max)) {
                        return 0;
                    }
                    ++area;
                    stack.add(new Coordinate(c.x, c.y - 1));
                    stack.add(new Coordinate(c.x, c.y + 1));
                    stack.add(new Coordinate(c.x - 1, c.y));
                    stack.add(new Coordinate(c.x + 1, c.y));
                }
            }
            visited.add(c);
        }
        return area;
    }

    private int partTwo(List<Coordinate> coords, int distance) {
        int area = 0;
        for (int y = -distance; y < distance; ++y) {
            for (int x = -distance; x < distance; ++x) {
                Coordinate c = new Coordinate(x, y);
                if (coords.stream().mapToInt(a -> a.distanceTo(c)).sum() < distance) {
                    ++area;
                }
            }
        }
        return area;
    }

    @Override
    public void run() {
        Pattern p = Pattern.compile("(\\d+), (\\d+)");
        var coords = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> new Coordinate(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)))).toList();
        var min = new Coordinate(coords.stream().mapToInt(Coordinate::x).min().getAsInt(),
                coords.stream().mapToInt(Coordinate::y).min().getAsInt());
        var max = new Coordinate(coords.stream().mapToInt(Coordinate::x).max().getAsInt(),
                coords.stream().mapToInt(Coordinate::y).max().getAsInt());
        this.addTask("Part one", () -> {
            return coords.stream().mapToInt(c -> this.getArea(coords, min, max, c)).max().getAsInt();
        });
        this.addTask("Part two", () -> partTwo(coords, 10000));
    }
}
