package dev.nicotopia.aoc2025;

import java.util.List;

import dev.nicotopia.aoc.DayBase;

public class Day03 extends DayBase {
    private int leftMostMaxIdx(char data[], int beg, int end) {
        int maxIdx = beg;
        for (int i = beg; i < end; ++i) {
            if (data[maxIdx] < data[i]) {
                maxIdx = i;
            }
        }
        return maxIdx;
    }

    private long getMaxBankJoltage(char batteries[], int digitCount) {
        StringBuffer result = new StringBuffer();
        int minIdx = 0;
        for (int i = 0; i < digitCount; ++i) {
            int idx = this.leftMostMaxIdx(batteries, minIdx, batteries.length - (digitCount - 1 - i));
            result.append(batteries[idx]);
            minIdx = idx + 1;
        }
        return Long.parseLong(result.toString());
    }

    private long getMaxOutputJoltage(List<char[]> banks, int digitCount) {
        return banks.stream().mapToLong(bank -> this.getMaxBankJoltage(bank, digitCount)).sum();
    }

    @Override
    public void run() {
        List<char[]> banks = this.getPrimaryPuzzleInput().stream().map(String::toCharArray).toList();
        this.addTask("Part one", () -> this.getMaxOutputJoltage(banks, 2));
        this.addTask("Part two", () -> this.getMaxOutputJoltage(banks, 12));
    }
}
