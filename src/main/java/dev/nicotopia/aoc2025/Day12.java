package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.aoc.DayBase;

public class Day12 extends DayBase {
    private record Region(int width, int height, List<Integer> presentCounts) {
    }

    private final List<char[][]> presents = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();

    private int getPresentArea(int idx) {
        int area = 0;
        for (int i = 0; i < this.presents.get(idx).length; ++i) {
            for (int j = 0; j < this.presents.get(idx)[i].length; ++j) {
                if (this.presents.get(idx)[i][j] == '#') {
                    ++area;
                }
            }
        }
        return area;
    }

    private boolean checkRegion(int idx) {
        Region r = this.regions.get(idx);
        if (r.presentCounts.stream().mapToInt(i -> i).sum() <= (r.width / 3) * (r.height / 3)) {
            return true;
        }
        int areaSum = 0;
        for (int presentIdx = 0; presentIdx < r.presentCounts.size(); ++presentIdx) {
            areaSum += r.presentCounts.get(presentIdx) * this.getPresentArea(presentIdx);
        }
        if (r.width * r.height < areaSum) {
            return false;
        }
        throw new UnsupportedOperationException(
                "This algorithm works only for trivial solutions. And my puzzle input was in fact trivial.");
    }

    private long partOne() {
        return IntStream.range(0, this.regions.size()).filter(this::checkRegion).count();
    }

    @Override
    public void run() {
        Pattern p = Pattern.compile("(\\d+)x(\\d+): ((\\d+ )*\\d+)");
        List<char[]> present = null;
        for (String line : this.getPrimaryPuzzleInput()) {
            if (line.endsWith(":")) {
                present = new ArrayList<>();
            } else if (line.isEmpty()) {
                presents.add(present.toArray(char[][]::new));
            } else {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    regions.add(new Region(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                            Stream.of(m.group(3).split(" ")).map(Integer::valueOf).toList()));
                } else {
                    present.add(line.toCharArray());
                }
            }
        }
        this.addTask("Part one", this::partOne);
    }
}
