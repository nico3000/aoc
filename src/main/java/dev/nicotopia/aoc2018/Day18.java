package dev.nicotopia.aoc2018;

import java.awt.Color;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.Pair;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day18 extends DayBase {
    private enum Acre {
        OPEN, TREES, LUMBERYARD
    }

    private int width;
    private int height;
    private BitSet map = new BitSet();

    private Acre get(int x, int y) {
        int idx = 2 * (y * this.width + x);
        return this.map.get(idx) ? Acre.LUMBERYARD : this.map.get(idx + 1) ? Acre.TREES : Acre.OPEN;
    }

    private void set(Vec2i p, Acre acre) {
        int idx = 2 * (p.y() * this.width + p.x());
        this.map.set(idx, acre == Acre.LUMBERYARD);
        this.map.set(idx + 1, acre == Acre.TREES);
    }

    private int countAdj(Vec2i p, Acre filter) {
        int count = 0;
        for (int dy = -1; dy <= 1; ++dy) {
            int y = p.y() + dy;
            for (int dx = -1; dx <= 1; ++dx) {
                int x = p.x() + dx;
                if ((dx != 0 || dy != 0) && 0 <= y && y < this.height && 0 <= x && x < this.width
                        && this.get(x, y) == filter) {
                    ++count;
                }
            }
        }
        return count;
    }

    private int count(Acre filter) {
        return (int) Vec2i.streamFromRectangle(0, 0, this.width, this.height)
                .filter(p -> this.get(p.x(), p.y()) == filter).count();
    }

    private void loadMap() {
        this.width = this.getPrimaryPuzzleInput().getFirst().length();
        this.height = this.getPrimaryPuzzleInput().size();
        Vec2i.streamFromRectangle(0, 0, this.width, this.height)
                .forEach(p -> this.set(p, switch (this.getPrimaryPuzzleInput().get(p.y()).charAt(p.x())) {
                    case '.' -> Acre.OPEN;
                    case '|' -> Acre.TREES;
                    case '#' -> Acre.LUMBERYARD;
                    default -> throw new AocException("Illegal char");
                }));
    }

    private int run(int minutes) {
        this.loadMap();
        Map<BitSet, Integer> known = new HashMap<>();
        for (int i = 0; i < minutes; ++i) {
            if (known.containsKey(this.map)) {
                int loopStart = known.get(this.map);
                int loopEnd = i;
                int loopLength = loopEnd - loopStart;
                int finalIdx = loopStart + (minutes - loopStart) % loopLength;
                this.map = known.entrySet().stream().filter(e -> e.getValue() == finalIdx).findAny().get().getKey();
                break;
            }
            known.put((BitSet) this.map.clone(), i);
            Vec2i.streamFromRectangle(0, 0, this.width, this.height)
                    .map(p -> new Pair<>(p, switch (this.get(p.x(), p.y())) {
                        case OPEN -> 3 <= this.countAdj(p, Acre.TREES) ? Acre.TREES : Acre.OPEN;
                        case TREES -> 3 <= this.countAdj(p, Acre.LUMBERYARD) ? Acre.LUMBERYARD : Acre.TREES;
                        case LUMBERYARD ->
                            1 <= this.countAdj(p, Acre.LUMBERYARD) && 1 <= this.countAdj(p, Acre.TREES)
                                    ? Acre.LUMBERYARD
                                    : Acre.OPEN;
                    })).toList().forEach(p -> this.set(p.first(), p.second()));
        }
        return this.count(Acre.TREES) * this.count(Acre.LUMBERYARD);
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.run(10));
        this.addTask("Part two", () -> this.run(1000000000));
        this.pushPostResultsOption("Show map", () -> {
            Dialog.showImage("Fancy map <3", new ImageComponent(
                    ImageComponent.imageFrom(this.width, this.height, (x, y) -> switch (this.get(x, y)) {
                        case OPEN -> new Color(0x26d07c);
                        case TREES -> new Color(0x006e33);
                        case LUMBERYARD -> new Color(0x664228);
                    })));
        });
    }
}