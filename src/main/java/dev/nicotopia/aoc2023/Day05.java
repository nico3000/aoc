package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import dev.nicotopia.Range;
import dev.nicotopia.RangeSet;
import dev.nicotopia.aoc.DayBase;

public class Day05 extends DayBase {
    private class GardeningMap {
        private final String fromType;
        private final String toType;
        private final Map<Range, Long> rangeStartMappings = new HashMap<>();

        public GardeningMap(String fromType, String toType) {
            this.fromType = fromType;
            this.toType = toType;
        }

        public long getFinalMapping(long value) {
            var range = this.rangeStartMappings.keySet().stream().filter(r -> r.contains(value)).findFirst();
            long mappedValue = range.map(r -> value + this.rangeStartMappings.get(r) - r.start()).orElse(value);
            var toMap = Day05.this.maps.get(this.toType);
            return toMap == null ? mappedValue : toMap.getFinalMapping(mappedValue);
        }

        public RangeSet getFinalMappings(Range range) {
            return this.getFinalMappings(new RangeSet(range));
        }

        private RangeSet getFinalMappings(RangeSet ranges) {
            var mappedRanges = new RangeSet(ranges.stream().map(this::getMappings).flatMap(RangeSet::stream));
            var toMap = Day05.this.maps.get(this.toType);
            return toMap == null ? mappedRanges : toMap.getFinalMappings(mappedRanges);
        }

        private RangeSet getMappings(Range range) {
            RangeSet in = new RangeSet(range);
            RangeSet out = new RangeSet();
            for (Range toCutOut : this.rangeStartMappings.keySet()) {
                RangeSet cutOut = in.cutOut(toCutOut);
                cutOut.offset(this.rangeStartMappings.get(toCutOut) - toCutOut.start());
                out.add(cutOut);
            }
            out.add(in);
            return out;
        }
    }

    private List<Long> seeds;
    private final Map<String, GardeningMap> maps = new HashMap<>();

    private void processInput() {
        Iterator<String> lines = this.getPrimaryPuzzleInput().iterator();
        this.seeds = Arrays.stream(lines.next().split(": ")[1].split("\\s+")).map(Long::valueOf).toList();
        lines.next();
        while (lines.hasNext()) {
            String fromTo[] = lines.next().split("\\s+")[0].split("-to-");
            GardeningMap map = new GardeningMap(fromTo[0], fromTo[1]);
            Consumer<long[]> addRange = v -> map.rangeStartMappings.put(new Range(v[1], v[2]), v[0]);
            String line;
            while (lines.hasNext() && !(line = lines.next()).isEmpty()) {
                addRange.accept(Arrays.stream(line.split("\\s+")).mapToLong(Long::valueOf).toArray());
            }
            this.maps.put(map.fromType, map);
        }
    }

    private long partOne() {
        return this.seeds.stream().mapToLong(this.maps.get("seed")::getFinalMapping).min().getAsLong();
    }

    private long partTwo() {
        var seeds = IntStream.range(0, this.seeds.size() / 2)
                .mapToObj(i -> new Range(this.seeds.get(2 * i), this.seeds.get(2 * i + 1)));
        return seeds.mapToLong(s -> this.maps.get("seed").getFinalMappings(s).getFirst().start()).min().getAsLong();
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
