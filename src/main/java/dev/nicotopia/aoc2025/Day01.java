package dev.nicotopia.aoc2025;

import dev.nicotopia.aoc.DayBase;

public class Day01 extends DayBase {
  private int partOne() {
    int result = 0;
    int pos = 50;
    for (String line : this.getPrimaryPuzzleInput()) {
      int amount = Integer.parseInt(line.substring(1)) * (line.startsWith("L") ? -1 : 1);
      if ((pos += amount) % 100 == 0) {
        ++result;
      }
    }
    return result;
  }

  private int partTwo() {
    int result = 0;
    int pos = 50;
    for (String line : this.getPrimaryPuzzleInput()) {
      int amount = Integer.parseInt(line.substring(1)) * (line.startsWith("L") ? -1 : 1);
      result += Math.abs(amount / 100);
      boolean wasZero = pos == 0;
      pos += amount % 100;
      if (100 <= pos || pos <= 0) {
        if (!wasZero) {
          ++result;
        }
        pos = (pos + 100) % 100;
      }
    }
    return result;
  }

  @Override
  public void run() {
    this.addTask("Part one", this::partOne);
    this.addTask("Part two", this::partTwo);
  }
}
