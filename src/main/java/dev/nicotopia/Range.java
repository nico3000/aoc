package dev.nicotopia;

import java.util.Optional;

public record Range(long start, long size) {
    public static final Range EMPTY = new Range(0, 0);

    public long end() {
        return this.start + this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean contains(long v) {
        return this.start <= v && v < this.end();
    }

    public boolean intersects(Range other) {
        return other.start < this.end() && this.start < other.end();
    }

    public Optional<Range> getIntersection(Range other) {
        long begin = Math.max(this.start, other.start);
        long end = Math.min(this.end(), other.end());
        return begin < end ? Optional.of(new Range(begin, end - begin)) : Optional.empty();
    }

    public int compareByStart(Range o) {
        return o == null ? -1 : Long.compare(this.start, o.start);
    }

    public boolean containsCompletely(Range other) {
        return this.start <= other.start && other.end() <= this.end();
    }

    public boolean areDisjoint(Range other) {
        return other.end() <= this.start || this.end() <= other.start;
    }

    @Override
    public String toString() {
        return this.size == 0 ? "empty" : String.format("[%d,%d]", this.start, this.start + this.size - 1);
    }
}
