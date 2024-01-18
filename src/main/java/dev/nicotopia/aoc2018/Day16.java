package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc2018.Machine.OpCode;
import dev.nicotopia.aoc2018.Machine.Operation;

public class Day16 extends DayBase {
    private final List<HashSet<OpCode>> possibilities = IntStream.range(0, OpCode.values().length)
            .mapToObj(i -> new HashSet<>(Arrays.asList(OpCode.values()))).toList();
    private int idx;
    private final Pattern fourIntsPattern = Pattern.compile("(\\d+),? (\\d+),? (\\d+),? (\\d+)");

    private int[] extractFourInts(String str) {
        return this.fourIntsPattern.matcher(str).results().findFirst()
                .map(m -> IntStream.range(0, 4).map(i -> Integer.valueOf(m.group(1 + i))).toArray()).get();
    }

    private int partOne() {
        Machine m = new Machine(4);
        int count = 0;
        for (this.idx = 0; !this.getPrimaryPuzzleInput().get(this.idx).isEmpty(); this.idx += 4) {
            int[] before = this.extractFourInts(this.getPrimaryPuzzleInput().get(idx));
            int[] operation = this.extractFourInts(this.getPrimaryPuzzleInput().get(idx + 1));
            int[] after = this.extractFourInts(this.getPrimaryPuzzleInput().get(idx + 2));
            Set<OpCode> possibleOpCodes = Arrays.stream(OpCode.values()).map(code -> {
                IntStream.range(0, 4).forEach(i -> m.register(i, before[i]));
                m.execute(new Operation(code, operation[1], operation[2], operation[3]));
                return IntStream.range(0, 4).allMatch(i -> m.register(i) == after[i]) ? code : null;
            }).filter(c -> c != null).collect(Collectors.toSet());
            if (3 <= possibleOpCodes.size()) {
                ++count;
            }
            this.possibilities.get(operation[0]).removeIf(c -> !possibleOpCodes.contains(c));
        }
        return count;
    }

    public int partTwo() {
        OpCode[] codes = new OpCode[this.possibilities.size()];
        for (;;) {
            OptionalInt oneLeftIdx = IntStream.range(0, this.possibilities.size())
                    .filter(i -> this.possibilities.get(i).size() == 1).findAny();
            if (oneLeftIdx.isEmpty()) {
                break;
            }
            OpCode found = this.possibilities.get(oneLeftIdx.getAsInt()).stream().findAny().get();
            codes[oneLeftIdx.getAsInt()] = found;
            this.possibilities.forEach(p -> p.remove(found));
        }
        if (Arrays.stream(codes).anyMatch(c -> c == null)) {
            throw new AocException(
                    "Unidentified op codes left. Implementation fix: Instead of searching for single element sets, search for op codes that appear only in a single set.");
        }
        Machine m = new Machine(4);
        m.execute(this.getPrimaryPuzzleInput().subList(this.idx + 2, this.getPrimaryPuzzleInput().size()).stream()
                .map(this::extractFourInts).map(r -> new Operation(codes[r[0]], r[1], r[2], r[3])).toList());
        return m.register(0);
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}