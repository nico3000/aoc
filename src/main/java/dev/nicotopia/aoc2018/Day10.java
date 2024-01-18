package dev.nicotopia.aoc2018;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day10 extends DayBase {
    private record Star(Vec2i pos, Vec2i velocity) {
    }

    private List<Star> stars;

    private void processInput() {
        Pattern p = Pattern.compile("position=<\\s*(-?\\d+),\\s*(-?\\d+)> velocity=<\\s*(-?\\d+),\\s*(-?\\d+)>");
        Function<Matcher, Star> toStar = m -> new Star(
                new Vec2i(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))),
                new Vec2i(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))));
        this.stars = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).map(toStar)
                .toList();
    }

    private Pair<Vec2i, Vec2i> getSkyExtents(int timePoint) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Star s : this.stars) {
            minX = Math.min(minX, s.pos.x() + timePoint * s.velocity.x());
            maxX = Math.max(maxX, s.pos.x() + timePoint * s.velocity.x());
            minY = Math.min(minY, s.pos.y() + timePoint * s.velocity.y());
            maxY = Math.max(maxY, s.pos.y() + timePoint * s.velocity.y());
        }
        return new Pair<Vec2i, Vec2i>(new Vec2i(minX, minY), new Vec2i(maxX, maxY));
    }

    private String renderSky(int timePoint) {
        var starPositions = this.stars.stream().map(s -> s.velocity.mad(timePoint, s.pos)).collect(Collectors.toSet());
        var extents = this.getSkyExtents(timePoint);
        StringBuilder builder = new StringBuilder();
        for (int y = extents.first().y(); y <= extents.second().y(); ++y) {
            for (int x = extents.first().x(); x <= extents.second().x(); ++x) {
                builder.append(starPositions.contains(new Vec2i(x, y)) ? '#' : ' ');
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    private int findCloseTimePoint() {
        final int maxWidth = 100; // heuristics
        final int maxHeight = 50;
        for (int i = 0;; ++i) {
            var extents = this.getSkyExtents(i);
            if (extents.second().x() - extents.first().x() < maxWidth
                    && extents.second().y() - extents.first().y() < maxHeight) {
                return i;
            }
        }
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        int timePoint = this.addTask("Both parts", this::findCloseTimePoint);
        boolean done = false;
        for (; !done; ++timePoint) {
            String text = String.format("After %d second(s):\n%s", timePoint, this.renderSky(timePoint));
            done = Dialog.showInfo("The beautiful sky <3", text, MONOSPACED_FONT, "Stop", "Next") != 1;
        }
    }
}