package dev.nicotopia.aoc2019;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.nicotopia.Pair;
import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day10 extends DayBase {
    private Set<Vec2i> asteroids;
    private Vec2i station;
    private Map<Vec2i, List<Vec2i>> others;

    private void processInput() {
        this.asteroids = Vec2i
                .streamFromRectangle(0, 0, this.getPrimaryPuzzleInput().getFirst().length(),
                        this.getPrimaryPuzzleInput().size())
                .filter(p -> this.getPrimaryPuzzleInput().get(p.y()).charAt(p.x()) == '#').collect(Collectors.toSet());
    }

    private Map<Vec2i, List<Vec2i>> getOthers(Vec2i station) {
        Map<Vec2i, List<Vec2i>> others = new HashMap<>();
        this.asteroids.stream().filter(p -> p != station).forEach(p -> {
            Vec2i dir = p.sub(station);
            dir = dir.divide(Util.gcd(dir.x(), dir.y()));
            List<Vec2i> dst = others.get(dir);
            if (dst == null) {
                others.put(dir, dst = new LinkedList<>());
            }
            dst.add(p);
        });
        return others;
    }

    private int partOne() {
        var result = this.asteroids.stream().map(station -> new Pair<>(station, this.getOthers(station)))
                .max((a, b) -> Integer.compare(a.second().size(), b.second().size())).get();
        this.station = result.first();
        this.others = result.second();
        return this.others.size();
    }

    private double angle(Vec2i dir) {
        double len = Math.sqrt((double) (dir.x() * dir.x()) + (double) (dir.y() * dir.y()));
        double angle = Math.atan2((double) dir.y() / len, (double) dir.x() / len);
        return angle < -0.5 * Math.PI ? 2.0 * Math.PI + angle : angle;
    }

    private int partTwo() {
        int i = 0;
        this.others.values().forEach(l -> l.sort(
                (a, b) -> Integer.compare(a.manhattanDistanceTo(this.station), b.manhattanDistanceTo(this.station))));
        while (!this.others.isEmpty()) {
            List<Vec2i> sorted = this.others.keySet().stream()
                    .sorted((a, b) -> Double.compare(this.angle(a), this.angle(b))).toList();
            for (Vec2i dir : sorted) {
                List<Vec2i> inDir = this.others.get(dir);
                Vec2i removed = inDir.removeFirst();
                if (++i == 200) {
                    return 100 * removed.x() + removed.y();
                }
                if (inDir.isEmpty()) {
                    this.others.remove(dir);
                }
            }
        }
        return 0;
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
