package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;

public class Day12 extends DayBase {
    private record Line(String state, int stateBegin, int stateEnd, int[] groups, int groupsBegin, int groupsEnd) {
        public Line(String state, int[] groups) {
            this(state, 0, state.length(), groups, 0, groups.length);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Line l && l.stateEnd - l.stateBegin == this.stateEnd - this.stateBegin
                    && l.groupsEnd - l.groupsBegin == this.groupsEnd - this.groupsBegin) {
                return Arrays.equals(this.groups, this.groupsBegin, this.groupsEnd, l.groups, l.groupsBegin,
                        l.groupsEnd)
                        && l.state.substring(l.stateBegin, l.stateEnd)
                                .equals(this.state.substring(this.stateBegin, this.stateEnd));
            }
            return false;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 0;
            result = prime * result + this.state.substring(this.stateBegin, this.stateEnd).hashCode();
            for (int i = this.stateBegin; i < this.stateEnd; ++i) {
                result = prime * result + this.state.charAt(i);
            }
            for (int i = this.groupsBegin; i < this.groupsEnd; ++i) {
                result = prime * result + this.groups[i];
            }
            return result;
        }

        @Override
        public String toString() {
            return this.stateBegin == this.stateEnd && this.groupsBegin == this.groupsEnd ? "<empty>"
                    : this.state.substring(this.stateBegin, stateEnd) + " "
                            + Arrays.stream(this.groups, this.groupsBegin, this.groupsEnd).mapToObj(String::valueOf)
                                    .collect(Collectors.joining(","));
        }
    }

    private final Map<Line, Long> knownLines = new HashMap<>();
    private final Map<Integer, Pattern> knownPatterns = new HashMap<>();

    private int trimBegin(String state, int stateBegin, int stateEnd) {
        while (stateBegin < stateEnd && state.charAt(stateBegin) == '.') {
            ++stateBegin;
        }
        return stateBegin;
    }

    private int trimEnd(String state, int stateBegin, int stateEnd) {
        while (stateBegin < stateEnd && state.charAt(stateEnd - 1) == '.') {
            --stateEnd;
        }
        return stateEnd;
    }

    private int getGroupsSum(int[] groups, int groupsBegin, int groupsEnd) {
        int sum = 0;
        for (int i = groupsBegin; i < groupsEnd; ++i) {
            sum += groups[i];
        }
        return sum;
    }

    private int getMinGroupsLength(int[] groups, int groupsBegin, int groupsEnd) {
        return this.getGroupsSum(groups, groupsBegin, groupsEnd) + (groupsEnd - groupsBegin) - 1;
    }

    private long getNumPossibleArrangements(Line line) {
        long l = this.getNumPossibleArrangements(line.state, 0, line.state.length(), line.groups, 0,
                line.groups.length);
        return l;
    }

    private long getNumPossibleArrangements(String state, int stateBegin, int stateEnd, int groups[], int groupsBegin,
            int groupsEnd) {
        stateBegin = this.trimBegin(state, stateBegin, stateEnd);
        stateEnd = this.trimEnd(state, stateBegin, stateEnd);
        if (stateBegin == stateEnd) {
            return groupsBegin == groupsEnd ? 1 : 0;
        } else if (groupsBegin == groupsEnd) {
            return state.indexOf('#', stateBegin, stateEnd) == -1 ? 1 : 0;
        } else if (stateEnd - stateBegin < this.getMinGroupsLength(groups, groupsBegin, groupsEnd)) {
            return 0;
        } else if (IntStream.range(stateBegin, stateEnd).map(state::charAt).allMatch(c -> c == '?')) {
            return Util.binomial(stateEnd - stateBegin - this.getGroupsSum(groups, groupsBegin, groupsEnd) + 1,
                    groupsEnd - groupsBegin);
        } else if (this.getGroupsSum(groups, groupsBegin, groupsEnd) < IntStream.range(stateBegin, stateEnd)
                .map(state::charAt).filter(c -> c == '#').count()) {
            return 0;
        } else if (this.getGroupsSum(groups, groupsBegin, groupsEnd) == 1) {
            return IntStream.range(stateBegin, stateEnd).map(state::charAt).anyMatch(c -> c == '#') ? 1
                    : IntStream.range(stateBegin, stateEnd).map(state::charAt).filter(c -> c == '?').count();
        }

        Line l = new Line(state, stateBegin, stateEnd, groups, groupsBegin, groupsEnd);
        if (knownLines.containsKey(l)) {
            return knownLines.get(l);
        }

        long result = 0;
        int periodPos = state.indexOf('.', stateBegin, stateEnd);
        if (periodPos != -1) {
            int leftStateBegin = stateBegin;
            int leftStateEnd = periodPos;
            int rightStateBegin = periodPos + 1;
            int rightStateEnd = stateEnd;
            int leftMinGroupsLength = 0;
            int rightMinGroupsLength = this.getMinGroupsLength(groups, groupsBegin, groupsEnd);
            for (int i = 0; i <= groupsEnd - groupsBegin && leftMinGroupsLength <= leftStateEnd - leftStateBegin; ++i) {
                if (rightMinGroupsLength <= rightStateEnd - rightStateBegin) {
                    int leftGroupsBegin = groupsBegin;
                    int leftGroupsEnd = groupsBegin + i;
                    int rightGroupsBegin = groupsBegin + i;
                    int rightGroupsEnd = groupsEnd;
                    result += this.getNumPossibleArrangements(state, leftStateBegin, leftStateEnd, groups,
                            leftGroupsBegin, leftGroupsEnd)
                            * this.getNumPossibleArrangements(state, rightStateBegin, rightStateEnd, groups,
                                    rightGroupsBegin, rightGroupsEnd);
                }
                if (i == 0) {
                    leftMinGroupsLength += groups[groupsBegin + i];
                    rightMinGroupsLength -= groups[groupsBegin + i] + 1;
                } else if (i == groupsEnd - groupsBegin - 1) {
                    leftMinGroupsLength += groups[groupsBegin + i] + 1;
                    rightMinGroupsLength -= groups[groupsBegin + i];
                } else if (i != groupsEnd - groupsBegin) {
                    leftMinGroupsLength += groups[groupsBegin + i] + 1;
                    rightMinGroupsLength -= groups[groupsBegin + i] + 1;
                }
            }
        } else {
            Pattern p = knownPatterns.get(groups[groupsBegin]);
            if (p == null) {
                String regex = String.format("[^#][^\\.]{%d}(?:\\.|\\?|$)", groups[groupsBegin]);
                knownPatterns.put(groups[groupsBegin], p = Pattern.compile(regex));
            }
            int remGroupsBegin = groupsBegin + 1;
            int remGroupsEnd = groupsEnd;
            int remMinGroupsLength = this.getMinGroupsLength(groups, remGroupsBegin, remGroupsEnd);
            int limit = state.indexOf('#', stateBegin, stateEnd);
            if (limit != -1) {
                limit -= stateBegin;
            } else {
                limit = stateEnd - stateBegin;
            }

            Matcher m = p.matcher("." + state.substring(stateBegin, stateEnd));
            int pos = 0;
            while (pos <= limit && m.find(pos) && m.start() - 1 < limit
                    && remMinGroupsLength <= stateEnd - (stateBegin + (m.end() - 1))) {
                int remStateBegin = stateBegin + (m.end() - 1);
                int remStateEnd = stateEnd;
                result += this.getNumPossibleArrangements(state, remStateBegin, remStateEnd, groups, remGroupsBegin,
                        remGroupsEnd);
                pos = m.start() + 1;
            }
        }
        knownLines.put(l, result);
        return result;
    }

    private Line combine(Line left, Line right) {
        int mergedGroups[] = new int[left.groups.length + right.groups.length];
        System.arraycopy(left.groups, 0, mergedGroups, 0, left.groups.length);
        System.arraycopy(right.groups, 0, mergedGroups, left.groups.length, right.groups.length);
        return new Line(left.state + '?' + right.state, mergedGroups);
    }

    @Override
    public void run() {
        List<Line> lines = this.addSilentTask("Process input",
                () -> this.getPrimaryPuzzleInput().stream().map(l -> l.split("\\s+"))
                        .map(s -> new Line(s[0], Arrays.stream(s[1].split(",")).mapToInt(Integer::valueOf).toArray()))
                        .toList());
        this.addTask("Part one", () -> lines.stream().mapToLong(this::getNumPossibleArrangements).sum());
        this.addTask("Part two",
                () -> lines.stream().mapToLong(l -> this
                        .getNumPossibleArrangements(IntStream.range(1, 5).mapToObj(j -> l).reduce(l, this::combine)))
                        .sum());
    }
}