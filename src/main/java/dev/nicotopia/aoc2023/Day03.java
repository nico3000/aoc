package dev.nicotopia.aoc2023;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import dev.nicotopia.Vec2i;
import dev.nicotopia.aoc.DayBase;

public class Day03 extends DayBase {
    private record PartNumber(int value, List<Vec2i> adjacentSymbolCoordinates) {
    }

    private List<String> lines;
    private List<PartNumber> partNumbers;

    private boolean isSymbol(int x, int y) {
        if (0 <= y && y < this.lines.size() && 0 <= x && x < this.lines.get(y).length()) {
            char c = this.lines.get(y).charAt(x);
            return !Character.isDigit(c) && c != '.';
        }
        return false;
    }

    private void processInput() {
        Pattern p = Pattern.compile("\\d+");
        this.partNumbers = IntStream.range(0, this.lines.size())
                .mapToObj(y -> p.matcher(this.lines.get(y)).results().map(r -> {
                    List<Vec2i> adjacentSymbolCoordinates = new LinkedList<>();
                    BiConsumer<Integer, Integer> addIfSymbol = (tx, ty) -> {
                        if (this.isSymbol(tx, ty)) {
                            adjacentSymbolCoordinates.add(new Vec2i(tx, ty));
                        }
                    };
                    addIfSymbol.accept(r.start() - 1, y);
                    addIfSymbol.accept(r.end(), y);
                    for (int x = r.start() - 1; x < r.end() + 1; ++x) {
                        addIfSymbol.accept(x, y - 1);
                        addIfSymbol.accept(x, y + 1);
                    }
                    return new PartNumber(Integer.valueOf(r.group()), adjacentSymbolCoordinates);
                })).flatMap(s -> s).toList();
    }

    private int partOne() {
        return this.partNumbers.stream().mapToInt(pn -> pn.adjacentSymbolCoordinates.isEmpty() ? 0 : pn.value).sum();
    }

    private int partTwo() {
        return Vec2i.streamFromRectangle(0, 0, this.lines.getFirst().length(), this.lines.size()).mapToInt(c -> {
            var pns = this.partNumbers.stream().filter(pn -> pn.adjacentSymbolCoordinates.contains(c))
                    .toList();
            return pns.size() == 2 ? pns.get(0).value * pns.get(1).value : 0;
        }).sum();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day03e.txt");
        this.lines = this.getPrimaryPuzzleInput();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}