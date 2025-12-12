package dev.nicotopia.aoc.algebra;

import java.util.Optional;

public interface Interval<E extends Interval<E>> {
    public boolean isEmpty();

    public boolean contains(E other);

    public boolean isDisjoint(E other);

    public Optional<E> tryMerge(E other);

    public Optional<E> tryRemove(E other);
}
