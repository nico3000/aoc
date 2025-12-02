package dev.nicotopia.aoc2019;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day06 extends DayBase {
    private Map<String, String> orbits;

    private void processInput() {
        this.orbits = this.getPrimaryPuzzleInput().stream().map(l -> l.split("\\)"))
                .collect(Collectors.toMap(s -> s[1], s -> s[0]));
    }

    private int getNumOrbits(String s) {
        return Optional.ofNullable(this.orbits.get(s)).map(v -> 1 + this.getNumOrbits(v)).orElse(0);
    }

    private int partOne() {
        return this.orbits.keySet().stream().mapToInt(this::getNumOrbits).sum();
    }

    private int partTwoAlt() {
        List<String> ancestors = new LinkedList<>();
        String body = this.orbits.get("YOU");
        while (body != null) {
            ancestors.add(body);
            body = this.orbits.get(body);
        }
        body = this.orbits.get("SAN");
        int idx = 0;
        while (body != null) {
            int ancIdx = ancestors.indexOf(body);
            if (ancIdx != -1) {
                return ancIdx + idx;
            }
            body = this.orbits.get(body);
            ++idx;
        }
        throw new AocException("Part two needs SAN and YOU to be connected indirectly.");
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwoAlt);
    }
}