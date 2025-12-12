package dev.nicotopia.aoc.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntervalSet {
    private final List<IntInterval> intervals = new ArrayList<>();

    public IntervalSet() {
    }

    public IntervalSet(int initialBeg, int initialEnd) {
        this.intervals.add(new IntInterval(initialBeg, initialEnd));
    }

    public void remove(IntInterval toRemove) {
        if (!toRemove.isEmpty()) {
            ListIterator<IntInterval> iter = intervals.listIterator();
            while (iter.hasNext()) {
                IntInterval interval = iter.next();
                if (!interval.isDisjoint(toRemove)) {
                    iter.remove();
                    if (!toRemove.contains(interval.beg())) {
                        iter.add(new IntInterval(interval.beg(), toRemove.beg()));
                    }
                    if (!toRemove.contains(interval.end() - 1)) {
                        iter.add(new IntInterval(toRemove.end(), interval.end()));
                    }
                }
            }
        }
    }

    public IntStream streamValues() {
        return this.intervals.stream().flatMapToInt(IntInterval::stream);
    }

    public Stream<IntInterval> streamIntervals() {
        return this.intervals.stream();
    }

    public int getNumIntervals() {
        return this.intervals.size();
    }
}
