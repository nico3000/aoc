package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day13 extends DayBase {
    private class Ground {
        private final List<char[]> rows;
        private final List<char[]> cols;

        public Ground(List<String> rows) {
            this.rows = rows.stream().map(String::toCharArray).toList();
            this.cols = IntStream.range(0, rows.getFirst().length()).mapToObj(c -> IntStream.range(0, rows.size())
                    .mapToObj(r -> rows.get(r).charAt(c)).reduce("", (a, b) -> a + b, String::concat).toCharArray())
                    .toList();
        }

        public int getMirrorPosValue(boolean fixSmudge) {
            OptionalInt originalRowPos = this.getMirrorPos(this.rows, OptionalInt.empty());
            OptionalInt originalColPos = this.getMirrorPos(this.cols, OptionalInt.empty());
            if (!fixSmudge) {
                return 100 * originalRowPos.orElse(0) + originalColPos.orElse(0);
            }
            OptionalInt result = this.iterateSmudgeFixed(() -> {
                OptionalInt rowPos = this.getMirrorPos(this.rows, originalRowPos);
                OptionalInt colPos = this.getMirrorPos(this.cols, originalColPos);
                if (rowPos.isPresent() || colPos.isPresent()) {
                    return OptionalInt.of(100 * rowPos.orElse(0) + colPos.orElse(0));
                } else {
                    return OptionalInt.empty();
                }
            });
            if (result.isEmpty()) {
                this.rows.forEach(r -> System.out.println(String.valueOf(r)));
                System.out.println(100 * originalRowPos.orElse(0) + originalColPos.orElse(0));
                throw new AocException("uh oh");
            }
            return result.getAsInt();
        }

        private OptionalInt iterateSmudgeFixed(Supplier<OptionalInt> accepted) {
            for (int i = 0; i < this.rows.size(); ++i) {
                for (int j = 0; j < this.rows.get(i).length; ++j) {
                    this.rows.get(i)[j] = this.rows.get(i)[j] == '#' ? '.' : '#';
                    this.cols.get(j)[i] = this.cols.get(j)[i] == '#' ? '.' : '#';
                    OptionalInt r = accepted.get();
                    this.cols.get(j)[i] = this.cols.get(j)[i] == '#' ? '.' : '#';
                    this.rows.get(i)[j] = this.rows.get(i)[j] == '#' ? '.' : '#';
                    if (r.isPresent()) {
                        return r;
                    }
                }
            }
            return OptionalInt.empty();
        }

        private OptionalInt getMirrorPos(List<char[]> lines, OptionalInt toIgnore) {
            int[] simplified = this.simplify(lines);
            for (int i = 0; i < simplified.length - 1; ++i) {
                int j = 0;
                while (i - j != -1 && i + j + 1 != simplified.length && simplified[i - j] == simplified[i + j + 1]) {
                    ++j;
                }
                if ((i - j == -1 || i + j + 1 == simplified.length)
                        && (toIgnore.isEmpty() || toIgnore.getAsInt() != i + 1)) {
                    return OptionalInt.of(i + 1);
                }
            }
            return OptionalInt.empty();
        }

        public int[] simplify(List<char[]> lines) {
            Map<String, Integer> known = new HashMap<>();
            return lines.stream().map(String::valueOf).mapToInt(line -> {
                Integer i = known.get(line);
                if (i == null) {
                    known.put(line, i = known.size());
                }
                return i;
            }).toArray();
        }
    }

    private List<Ground> processInput() {
        List<Ground> grounds = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < this.getPrimaryPuzzleInput().size(); ++i) {
            if (this.getPrimaryPuzzleInput().get(i).isBlank()) {
                grounds.add(new Ground(this.getPrimaryPuzzleInput().subList(start, i)));
                start = i + 1;
            }
            if (i == this.getPrimaryPuzzleInput().size() - 1) {
                grounds.add(new Ground(this.getPrimaryPuzzleInput().subList(start, i + 1)));
            }
        }
        return grounds;
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        List<Ground> grounds = this.addSilentTask("Process input", this::processInput);
        this.addTask("Part one", () -> grounds.stream().mapToInt(g -> g.getMirrorPosValue(false)).sum());
        this.addTask("Part one", () -> grounds.stream().mapToInt(g -> g.getMirrorPosValue(true)).sum());
    }
}