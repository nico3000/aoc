package dev.nicotopia.aoc2024;

import java.util.List;
import java.util.OptionalLong;

import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day18 extends DayBase {
    private CharMap2D map;
    private List<Vec2i> blocks;

    private OptionalLong getShortestPathLength(int numBlocks) {
        this.map.fill('.');
        this.blocks.subList(0, numBlocks).forEach(b -> this.map.set(b, '#'));
        return this.map.getShortestDistance(Vec2i.ORIGIN, new Vec2i(this.map.getWidth() - 1, this.map.getHeight() - 1),
                '.');
    }

    private long partOne() {
        return this.getShortestPathLength(this.getIntInput("Part one num blocks")).getAsLong();
    }

    private Vec2i partTwo() {
        int numBlocks = this.getIntInput("Part one num blocks") + 1;
        for (; numBlocks <= this.blocks.size(); ++numBlocks) {
            if (this.getShortestPathLength(numBlocks).isEmpty()) {
                return this.blocks.get(numBlocks - 1);
            }
        }
        return null;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("Width", 71);
        this.pushSecondaryInput("Height", 71);
        this.pushSecondaryInput("Part one num blocks", 1024);
        this.addPresetFromResource("Example", "/2024/day18e.txt", 7, 7, 12);
        this.map = new CharMap2D(this.getIntInput("Width"), this.getIntInput("Height"));
        this.blocks = this.getPrimaryPuzzleInput().stream().map(l -> l.split(","))
                .map(s -> new Vec2i(Integer.valueOf(s[0]), Integer.valueOf(s[1]))).toList();
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }

}
