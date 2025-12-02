package dev.nicotopia.aoc.algebra;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IntCombinationsIterator implements Iterator<int[]> {
    private final int begin;
    private final int end;
    private final int next[];
    private boolean hasNext;

    public IntCombinationsIterator(int size, int begin, int end) {
        this.begin = begin;
        this.end = end;
        this.hasNext = 0 < size && begin < end;
        this.next = this.hasNext ? new int[size] : null;
        if (this.hasNext) {
            Arrays.fill(this.next, this.begin);
        }
    }

    @Override
    public boolean hasNext() {
        return this.hasNext;
    }

    @Override
    public int[] next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        int ret[] = Arrays.copyOf(this.next, this.next.length);
        this.hasNext = false;
        for (int i = 0; i < this.next.length; ++i) {
            this.next[i] = this.begin + (this.next[i] - this.begin + 1) % (this.end - this.begin);
            if (this.next[i] != this.begin) {
                this.hasNext = true;
                break;
            }
        }
        return ret;
    }

    public Stream<int[]> stream(boolean parallel) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, 0), parallel);
    }
}
