package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.graphlib.KargerStein;

public class Day25 extends DayBase {
    private List<Pair<String, String>> edges = new ArrayList<>();

    private void processInput() {
        this.getPrimaryPuzzleInput().stream().map(l -> l.split(": "))
                .forEach(s -> Arrays.stream(s[1].split("\\s+")).forEach(n -> this.edges.add(new Pair<>(s[0], n))));
    }

    private int partOne() {
        return Stream.iterate(0, i -> i + 1).parallel().map(i -> new KargerStein<>(this.edges).run())
                .filter(r -> r.isPresent() && r.get().minCut() == 3).findAny()
                .map(r -> r.get().setA().size() * r.get().setB().size()).get();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
    }
}