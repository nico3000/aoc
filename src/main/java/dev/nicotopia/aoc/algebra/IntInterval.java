package dev.nicotopia.aoc.algebra;

import java.util.Optional;
import java.util.stream.IntStream;

public record IntInterval(int beg, int end) implements Interval<IntInterval> {
    public static final IntInterval EMPTY = new IntInterval(0, 0);

    public boolean contains(int i) {
        return this.beg <= i && i < this.end;
    }

    public boolean contains(IntInterval other) {
        return this.beg <= other.beg && other.end <= this.end;
    }

    public boolean isEmpty() {
        return this.end <= this.beg;
    }

    public int getSize() {
        return Math.max(0, this.end - this.beg);
    }

    public IntStream stream() {
        return IntStream.range(this.beg, this.end);
    }

    @Override
    public boolean isDisjoint(IntInterval other) {
        return other.isEmpty() || this.end <= other.beg || other.end <= this.beg;
    }

    @Override
    public Optional<IntInterval> tryMerge(IntInterval other) {
        if (this.isEmpty() || other.isEmpty()) {
            return Optional.of(IntInterval.EMPTY);
        } else if (Math.max(this.beg, other.beg) <= Math.min(this.end, other.end)) {
            return Optional.of(new IntInterval(Math.min(this.beg, other.beg), Math.max(this.end, other.end)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<IntInterval> tryRemove(IntInterval other) {
        if (this.isDisjoint(other)) {
            return Optional.of(this);
        } else if (other.contains(this)) {
            return Optional.of(IntInterval.EMPTY);
        } else if (this.beg < other.beg && other.end < this.end) {
            return Optional.empty();
        } else {
            return Optional.of(new IntInterval(Math.max(this.beg, other.beg), Math.min(this.end, other.end)));
        }
    }
}
