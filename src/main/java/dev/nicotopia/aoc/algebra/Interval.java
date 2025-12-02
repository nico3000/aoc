package dev.nicotopia.aoc.algebra;

import java.util.stream.IntStream;

public record Interval(int beg, int end) {
    public boolean contains(int i) {
        return this.beg <= i && i < this.end;
    }

    public boolean isDisjunctTo(Interval other) {
        return this.end <= other.beg || other.end <= this.beg;
    }

    public boolean isEmpty() {
        return this.end <= this.beg;
    }

    public IntStream stream() {
        return IntStream.range(this.beg, this.end);
    }
}
