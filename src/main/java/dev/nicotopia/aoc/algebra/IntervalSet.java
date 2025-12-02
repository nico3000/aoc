package dev.nicotopia.aoc.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntervalSet {
    private final List<Interval> intervals = new ArrayList<>();

    public IntervalSet() {
    }

    public IntervalSet(int initialBeg, int initialEnd) {
        this.intervals.add(new Interval(initialBeg, initialEnd));
    }

    public void remove(Interval toRemove) {
        if (!toRemove.isEmpty()) {
            ListIterator<Interval> iter = intervals.listIterator();
            while (iter.hasNext()) {
                Interval interval = iter.next();
                if (!interval.isDisjunctTo(toRemove)) {
                    iter.remove();
                    if (!toRemove.contains(interval.beg())) {
                        Optional.of(new Interval(interval.beg(), toRemove.beg())).ifPresent(iter::add);
                    }
                    if (!toRemove.contains(interval.end() - 1)) {
                        Optional.of(new Interval(toRemove.end(), interval.end())).ifPresent(iter::add);
                    }
                }
            }
        }
    }

    public IntStream streamValues() {
        return this.intervals.stream().flatMapToInt(Interval::stream);
    }

    public Stream<Interval> streamIntervals() {
        return this.intervals.stream();
    }

    public int getNumIntervals() {
        return this.intervals.size();
    }
}
