package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day02 extends DayBase {
    private record Draw(int r, int g, int b) {
    }

    static final Pattern drawStringPattern = Pattern.compile("(\\d+) (red|green|blue)");

    private Draw createDrawFromString(String rawDraw) {
        var draw = drawStringPattern.matcher(rawDraw).results()
                .collect(Collectors.toMap(m -> m.group(2), m -> Integer.valueOf(m.group(1))));
        return new Draw(draw.getOrDefault("red", 0), draw.getOrDefault("green", 0),
                draw.getOrDefault("blue", 0));
    }

    private List<List<Draw>> processInput() {
        return this.getPrimaryPuzzleInput().stream().map(line -> Arrays
                .stream(line.substring(line.indexOf(':') + 1).split(";")).map(this::createDrawFromString).toList())
                .toList();
    }

    private int partOne(List<List<Draw>> games) {
        return IntStream.range(0, games.size())
                .map(i -> games.get(i).stream().allMatch(d -> d.r <= 12 && d.g <= 13 && d.b <= 14) ? i + 1 : 0).sum();
    }

    private int partTwo(List<List<Draw>> games) {
        return games.stream().mapToInt(game -> game.stream().mapToInt(Draw::r).max().orElse(0) *
                game.stream().mapToInt(Draw::g).max().orElse(0) *
                game.stream().mapToInt(Draw::b).max().orElse(0)).sum();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        List<List<Draw>> games = this.addSilentTask("Input processing", this::processInput);
        this.addTask("Part one", () -> this.partOne(games));
        this.addTask("Part two", () -> this.partTwo(games));
    }
}