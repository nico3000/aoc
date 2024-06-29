package dev.nicotopia.aoc2019;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.Compass;
import dev.nicotopia.Pair;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day15 extends DayBase {
    private enum Type {
        WALL, EMPTY, OXYGEN_SYSTEM, START
    }

    private IntcodeMachine machine;
    private final Map<Vec2i, Pair<Type, IntcodeMachine>> map = new HashMap<>();
    private Vec2i oxygenPos;

    private NodeDistancePair<Vec2i> getNeighbour(Vec2i pos, int i) {
        if (4 <= i) {
            return null;
        } else if (this.isEmpty(pos, Compass.N) && i-- == 0) {
            return new NodeDistancePair<>(new Vec2i(pos.x(), pos.y() - 1), 1);
        } else if (this.isEmpty(pos, Compass.E) && i-- == 0) {
            return new NodeDistancePair<>(new Vec2i(pos.x() + 1, pos.y()), 1);
        } else if (this.isEmpty(pos, Compass.S) && i-- == 0) {
            return new NodeDistancePair<>(new Vec2i(pos.x(), pos.y() + 1), 1);
        } else if (this.isEmpty(pos, Compass.W) && i-- == 0) {
            return new NodeDistancePair<>(new Vec2i(pos.x() - 1, pos.y()), 1);
        } else {
            return null;
        }
    }

    private void processInput() {
        this.machine = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
    }

    private boolean isEmpty(Vec2i base, Compass dir) {
        var p = this.map.get(base);
        Vec2i dstPos = switch (dir) {
            case N -> new Vec2i(base.x(), base.y() - 1);
            case E -> new Vec2i(base.x() + 1, base.y());
            case S -> new Vec2i(base.x(), base.y() + 1);
            case W -> new Vec2i(base.x() - 1, base.y());
        };
        var dst = this.map.get(dstPos);
        if (dst == null) {
            IntcodeMachine c = p.second().clone();
            int result = c.execute(switch (dir) {
                case N -> 1;
                case S -> 2;
                case W -> 3;
                case E -> 4;
            }).getFirst().intValue();
            this.map.put(dstPos, dst = new Pair<>(Type.values()[result], c));
        }
        return dst.first() != Type.WALL;
    }

    private int partOne() {
        this.map.put(Vec2i.ORIGIN, new Pair<>(Type.START, this.machine.clone()));
        HashedAStarDataStructure<Vec2i> asds = new HashedAStarDataStructure<>(p -> 0, pos -> false);
        AStar.run(this::getNeighbour, Vec2i.ORIGIN, asds);
        this.oxygenPos = this.map.entrySet().stream().filter(e -> e.getValue().first() == Type.OXYGEN_SYSTEM).findAny()
                .get().getKey();
        return asds.getFScore(oxygenPos);
    }

    private int partTwo() {
        HashedAStarDataStructure<Vec2i> asds = new HashedAStarDataStructure<>(p -> 0, pos -> false);
        AStar.run(this::getNeighbour, this.oxygenPos, asds);
        return this.map.keySet().stream().mapToInt(p -> asds.getFScore(p)).max().getAsInt();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
        this.pushPostResultsOption("Show map...",
                () -> Dialog.showImage("The confusing labyrinth <3", new ImageComponent(
                        ImageComponent.imageFrom(this.map, p -> p == null ? Color.BLACK : switch (p.first()) {
                            case START -> Color.RED;
                            case EMPTY -> Color.LIGHT_GRAY;
                            case WALL -> Color.BLACK;
                            case OXYGEN_SYSTEM -> Color.BLUE;
                        }))));
    }
}