package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i64;

public class Day11 extends DayBase {
    private List<Vec2i64> galaxies;

    private void processInput() {
        this.galaxies = IntStream.range(0, this.getPrimaryPuzzleInput().size())
                .mapToObj(y -> IntStream.range(0, this.getPrimaryPuzzleInput().get(y).length())
                        .filter(x -> this.getPrimaryPuzzleInput().get(y).charAt(x) == '#')
                        .mapToObj(x -> new Vec2i64(x, y)).toList())
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    private List<Vec2i64> expand(long factor) {
        List<Vec2i64> expanded = this.galaxies;
        Pair<Vec2i64, Vec2i64> extents = Vec2i64.getExtents(expanded);
        long growthX = 0;
        for (long x = extents.first().x(); x <= extents.second().x() + growthX; ++x) {
            final long fx = x;
            if (expanded.stream().noneMatch(g -> g.x() == fx)) {
                expanded = expanded.stream().map(g -> g.x() < fx ? g : new Vec2i64(g.x() + factor - 1, g.y())).toList();
                growthX += factor - 1;
                x += factor - 1;
            }
        }
        long growthY = 0;
        for (long y = extents.first().y(); y <= extents.second().y() + growthY; ++y) {
            final long fy = y;
            if (expanded.stream().noneMatch(g -> g.y() == fy)) {
                expanded = expanded.stream().map(g -> g.y() < fy ? g : new Vec2i64(g.x(), g.y() + factor - 1)).toList();
                growthY += factor - 1;
                y += factor - 1;
            }
        }
        return expanded;
    }

    private long sumOfShortestDistances(long expansionFactor) {
        List<Vec2i64> expanded = this.expand(expansionFactor);
        long sum = 0;
        for (int i = 0; i < expanded.size(); ++i) {
            Vec2i64 from = expanded.get(i);
            sum += expanded.subList(i + 1, expanded.size()).stream().mapToLong(from::manhattanDistanceTo).sum();
        }
        return sum;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("expansionFactor", 1000000);
        this.addPresetFromResource("Example", "/2023/day11e.txt", 10);
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", () -> this.sumOfShortestDistances(2));
        this.addTask("Part two", () -> this.sumOfShortestDistances(this.getIntInput("expansionFactor")));
        return;
    }
}