package dev.nicotopia.aoc.algebra;

import java.util.Optional;
import java.util.stream.LongStream;

public record LongInterval(long beg, long end) implements Interval<LongInterval> {
    public static LongInterval EMPTY = new LongInterval(0, 0);

    public boolean contains(long v) {
        return this.beg <= v && v < this.end;
    }

    public boolean contains(LongInterval other) {
        return this.beg <= other.beg && other.end <= this.end;
    }

    public boolean isEmpty() {
        return this.end <= this.beg;
    }

    public long getSize() {
        return Math.max(0, this.end - this.beg);
    }

    public LongStream stream() {
        return LongStream.range(this.beg, this.end);
    }

    @Override
    public boolean isDisjoint(LongInterval other) {
        return other.isEmpty() || this.end <= other.beg || other.end <= this.beg;
    }

    @Override
    public Optional<LongInterval> tryMerge(LongInterval other) {
        if (this.isEmpty() || other.isEmpty()) {
            return Optional.of(new LongInterval(0, 0));
        } else if (Math.max(this.beg, other.beg) <= Math.min(this.end, other.end)) {
            return Optional.of(new LongInterval(Math.min(this.beg, other.beg), Math.max(this.end, other.end)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<LongInterval> tryRemove(LongInterval other) {
        if (this.isDisjoint(other)) {
            return Optional.of(this);
        } else if (other.contains(this)) {
            return Optional.of(LongInterval.EMPTY);
        } else if (this.beg < other.beg && other.end < this.end) {
            return Optional.empty();
        } else {
            return Optional.of(new LongInterval(Math.max(this.beg, other.beg), Math.min(this.end, other.end)));
        }
    }
}
