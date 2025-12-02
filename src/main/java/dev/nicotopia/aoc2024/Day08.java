package dev.nicotopia.aoc2024;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day08 extends DayBase {
    private CharMap2D map;
    private final Map<Character, List<Vec2i>> antennas = new HashMap<>();

    private void processInput() {
        this.map = this.getPrimaryPuzzleInputAsCharMap2D();
        Consumer<Vec2i> addAntenna = p -> {
            List<Vec2i> positions = this.antennas.get(this.map.get(p));
            if (positions == null) {
                this.antennas.put(this.map.get(p), positions = new ArrayList<>());
            }
            positions.add(p);
        };
        this.map.coordinates().filter(p -> !this.map.is(p, '.')).forEach(addAntenna);
    }

    private void forEachAntennaPair(List<Vec2i> antennas, BiConsumer<Vec2i, Vec2i> action) {
        for (int i = 0; i < antennas.size() - 1; ++i) {
            for (int j = i + 1; j < antennas.size(); ++j) {
                action.accept(antennas.get(i), antennas.get(j));
            }
        }
    }

    private Set<Vec2i> getSimpleAntinodes(List<Vec2i> antennas) {
        Set<Vec2i> antinodes = new HashSet<>();
        this.forEachAntennaPair(antennas, (a, b) -> {
            Vec2i d = b.sub(a);
            Set<Vec2i> t = new HashSet<>(Arrays.asList(a.add(d), a.sub(d), b.add(d), b.sub(d)));
            t.removeAll(Arrays.asList(a, b));
            antinodes.addAll(t);
        });
        return antinodes;
    }

    private Set<Vec2i> getAllAntinodes(List<Vec2i> antennas) {
        Set<Vec2i> antinodes = new HashSet<>();
        this.forEachAntennaPair(antennas, (a, b) -> {
            Vec2i d = b.sub(a);
            Vec2i p = a;
            while (this.map.isInBounds(p)) {
                antinodes.add(p);
                p = p.add(d);
            }
            p = a;
            while (this.map.isInBounds(p)) {
                antinodes.add(p);
                p = p.sub(d);
            }
        });
        return antinodes;
    }

    private int partOne() {
        Set<Vec2i> antinodeLocations = new HashSet<>();
        antennas.values().stream().map(this::getSimpleAntinodes).forEach(antinodeLocations::addAll);
        antinodeLocations.removeIf(p -> !this.map.isInBounds(p));
        return antinodeLocations.size();
    }

    private int partTwo() {
        Set<Vec2i> antinodeLocations = new HashSet<>();
        antennas.values().stream().map(this::getAllAntinodes).forEach(antinodeLocations::addAll);
        return antinodeLocations.size();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
