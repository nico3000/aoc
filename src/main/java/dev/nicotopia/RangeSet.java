package dev.nicotopia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class RangeSet {
    private final List<Range> ranges = new ArrayList<>();

    public RangeSet(Collection<Range> ranges) {
        this.ranges.addAll(ranges);
        Collections.sort(this.ranges, Range::compareByStart);
        for (int i = 0; i < this.ranges.size() - 1; ++i) {
            if (this.ranges.get(i + 1).start() < this.ranges.get(i).end()) {
                throw new RuntimeException("Tried to create RangeSet from overlapping ranges.");
            }
        }
    }

    public RangeSet(Range... ranges) {
        this(Arrays.asList(ranges));
    }

    public RangeSet(Stream<Range> ranges) {
        this(ranges.toList());
    }

    public void add(Range range) {
        int idx = Collections.binarySearch(this.ranges, range, Range::compareByStart);
        if (0 <= idx) {
            throw new RuntimeException("Tried to add range twice.");
        }
        idx = -idx - 1;
        Range prev = 0 < idx ? this.ranges.get(idx - 1) : null;
        Range next = idx < this.ranges.size() ? this.ranges.get(idx) : null;
        if ((next != null && next.start() < range.end()) || (prev != null && range.start() < prev.end())) {
            throw new RuntimeException("Tried to add overlapping range.");
        }
        boolean combinableWithPrev = prev != null && prev.end() == range.start();
        boolean combinableWithNext = next != null && range.end() == next.start();
        if (combinableWithPrev && combinableWithNext) {
            Range combined = new Range(prev.start(), next.end() - prev.start());
            this.ranges.remove(idx);
            this.ranges.set(idx - 1, combined);
        } else if (combinableWithNext) {
            this.ranges.set(idx, new Range(range.start(), range.size() + next.size()));
        } else if (combinableWithPrev) {
            this.ranges.set(idx - 1, new Range(prev.start(), prev.size() + range.size()));
        } else {
            this.ranges.add(idx, range);
        }
    }

    public void add(RangeSet rangeSet) {
        rangeSet.ranges.forEach(this::add);
    }

    public void offset(long delta) {
        List<Range> newRanges = this.ranges.stream().map(r -> new Range(r.start() + delta, r.size())).toList();
        this.ranges.clear();
        this.ranges.addAll(newRanges);
    }

    public Stream<Range> stream() {
        return this.ranges.stream();
    }

    public Range getFirst() {
        return this.ranges.getFirst();
    }

    public RangeSet cutOut(Range toCutOut) {
        RangeSet cutOut = new RangeSet();
        List<Range> newRanges = new ArrayList<>();
        for (Range range : this.ranges) {
            if (toCutOut.areDisjoint(range)) {
                newRanges.add(range);
            } else if (toCutOut.containsCompletely(range)) {
                cutOut.add(range);
            } else if (range.containsCompletely(toCutOut)) {
                cutOut.add(toCutOut);
                newRanges.add(new Range(range.start(), toCutOut.start() - range.start()));
                newRanges.add(new Range(toCutOut.end(), range.end() - toCutOut.end()));
            } else {
                cutOut.add(toCutOut.getIntersection(range).get());
                if (toCutOut.start() < range.start()) {
                    newRanges.add(new Range(toCutOut.end(), range.end() - toCutOut.end()));
                } else {
                    newRanges.add(new Range(range.start(), toCutOut.start() - range.start()));
                }
            }
        }
        this.ranges.clear();
        this.ranges.addAll(newRanges.stream().filter(r -> r.size() != 0).toList());
        return cutOut;
    }

    public boolean isEmpty() {
        return this.ranges.isEmpty() || this.ranges.stream().allMatch(r -> r.size() == 0);
    }

    public long count() {
        return this.ranges.stream().mapToLong(Range::size).sum();
    }

    @Override
    public RangeSet clone() {
        return new RangeSet(this.ranges);
    }
}
