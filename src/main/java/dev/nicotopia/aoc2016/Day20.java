package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Day20 {
    private record Range(long beg, long end) implements Comparable<Range> {
        public Range(long beg, long end) {
            this.beg = beg;
            this.end = end;
            if (this.end <= this.beg) {
                throw new IllegalArgumentException("Empty or negative interval");
            }
        }

        public Range(String desc) {
            this(desc.split("-"));
        }

        private Range(String splitDesc[]) {
            this(Long.valueOf(splitDesc[0]), Long.valueOf(splitDesc[1]) + 1);
        }

        public boolean contains(long value) {
            return this.beg <= value && value < this.end;
        }

        public boolean contains(Range other) {
            return this.contains(other.beg) && this.contains(other.end - 1);
        }

        public boolean overlaps(Range other) {
            return other.contains(this) || this.contains(other.beg) || this.contains(other.end - 1);
        }

        @Override
        public int compareTo(Range o) {
            return o == null ? 1 : Long.compare(this.beg, o.beg);
        }
    }

    private static class RangeSet {
        private final List<Range> ranges = new LinkedList<>();

        public void add(RangeSet other) {
            other.ranges.forEach(this::add);
        }

        public void add(Range range) {
            int index = Collections.binarySearch(this.ranges, range);
            if (0 <= index) {
                if (this.ranges.get(index).contains(range)) {
                    return;
                }
                this.ranges.set(index, range);
            } else {
                index = -index - 1;
                if (index != 0) {
                    Range leftRange = this.ranges.get(index - 1);
                    if (leftRange.contains(range)) {
                        return;
                    } else if (leftRange.overlaps(range) || leftRange.end == range.beg) {
                        this.ranges.set(index - 1, range = new Range(leftRange.beg, range.end));
                        --index;
                    } else {
                        this.ranges.add(index, range);
                    }
                } else {
                    this.ranges.add(index, range);
                }
            }
            while (index + 1 < this.ranges.size()) {
                Range nextRange = this.ranges.get(index + 1);
                if (range.contains(nextRange)) {
                    this.ranges.remove(index + 1);
                } else {
                    if (range.overlaps(nextRange) || range.end == nextRange.beg) {
                        this.ranges.set(index, new Range(range.beg, nextRange.end));
                        this.ranges.remove(index + 1);
                    }
                    break;
                }
            }
        }

        public long getFirstOutside(long beg) {
            int index = Collections.binarySearch(this.ranges, new Range(beg, beg + 1));
            index = index < 0 ? -index - 1 : index;
            return index == this.ranges.size() ? beg : this.ranges.get(index).end;
        }

        public long getOutSideCount(long end) {
            long count = 0;
            long lastEnd = 0;
            for (Range range : this.ranges) {
                count += range.beg - lastEnd;
                lastEnd = range.end;
            }
            return count + end - lastEnd;
        }
    }

    public static void main(String[] args) throws IOException {
        RangeSet rangeSet;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day20.class.getResourceAsStream("/2016/day20.txt")))) {
            rangeSet = br.lines().map(Range::new).collect(RangeSet::new, RangeSet::add, RangeSet::add);
        }
        System.out.printf("First allowed: %d, allowed count: %d\n", rangeSet.getFirstOutside(0),
                rangeSet.getOutSideCount(1l << 32));
    }
}