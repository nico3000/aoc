package dev.nicotopia.aoc2018;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec4i;

public class Day25 extends DayBase {
    private List<Vec4i> stars;

    private void processInput() {
        this.stars = this.getPrimaryPuzzleInput().stream().map(l -> l.split(","))
                .map(s -> new Vec4i(Integer.valueOf(s[0].trim()), Integer.valueOf(s[1].trim()),
                        Integer.valueOf(s[2].trim()), Integer.valueOf(s[3].trim())))
                .toList();
    }

    private int partOne() {
        List<Set<Vec4i>> constellations = new LinkedList<>();
        for (Vec4i star : this.stars) {
            var l = constellations.stream().filter(c -> c.stream().anyMatch(s -> s.manhattanDistance(star) <= 3))
                    .toList();
            if (l.isEmpty()) {
                constellations.add(new HashSet<>());
                constellations.getLast().add(star);
            } else {
                l.getFirst().add(star);
                for (int i = 1; i < l.size(); ++i) {
                    l.getFirst().addAll(l.get(i));
                    constellations.remove(l.get(i));
                }
            }
        }
        return constellations.size();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
    }
}
