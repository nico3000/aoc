package dev.nicotopia.aoc2025;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;

public class Day10 extends DayBase {
    private class Machine {
        private final int indicatorLightsDiagram;
        private final List<Set<Integer>> buttons = new ArrayList<>();
        private final int joltageRequirements[];

        private Machine(String desc) {
            int indicatorLightsDiagram = 0;
            String split[] = desc.split("\s+");
            for (int i = 1; i < split[0].length() - 1; ++i) {
                if (split[0].charAt(i) == '#') {
                    indicatorLightsDiagram |= 1 << (i - 1);
                }
            }
            this.indicatorLightsDiagram = indicatorLightsDiagram;
            String splitJoltages[] = split[split.length - 1].substring(1, split[split.length - 1].length() - 1)
                    .split(",");
            this.joltageRequirements = Stream.of(splitJoltages).mapToInt(Integer::valueOf).toArray();
            for (int i = 1; i < split.length - 1; ++i) {
                this.buttons.add(Stream.of(split[i].substring(1, split[i].length() - 1).split(","))
                        .map(Integer::valueOf).collect(Collectors.toSet()));
            }
        }

        private int getButtonAsBits(int idx) {
            return this.buttons.get(idx).stream().mapToInt(i -> i).reduce(0, (a, b) -> a | (1 << b));
        }
    }

    private int partOne(List<Machine> machines) {
        int count = 0;
        for (Machine m : machines) {
            int minBitCount = Integer.MAX_VALUE;
            for (int i = 0; i < 1 << m.buttons.size(); ++i) {
                int test = 0;
                int bitCount = 0;
                for (int j = 0; j < m.buttons.size(); ++j) {
                    if ((i & (1 << j)) != 0) {
                        ++bitCount;
                        test ^= m.getButtonAsBits(j);
                    }
                }
                if (test == m.indicatorLightsDiagram && bitCount < minBitCount) {
                    minBitCount = bitCount;
                }
            }
            count += minBitCount;
        }
        return count;
    }

    private OptionalInt getMinButtonPressCount(List<Set<Integer>> buttons, int joltages[], OptionalInt currentMin) {
        int selectedIdxBtnCount = Integer.MAX_VALUE;
        int selectedIdx = -1;
        int maxJoltage = joltages[0];
        for (int joltageIdx = 0; joltageIdx < joltages.length; ++joltageIdx) {
            maxJoltage = Math.max(maxJoltage, joltages[joltageIdx]);
            if (joltages[joltageIdx] != 0) {
                int btnCount = 0;
                for (int btnIdx = 0; btnIdx < buttons.size(); ++btnIdx) {
                    if (buttons.get(btnIdx).contains(joltageIdx)) {
                        ++btnCount;
                    }
                }
                if (btnCount == 0) {
                    return OptionalInt.empty();
                } else if (btnCount < selectedIdxBtnCount) {
                    selectedIdx = joltageIdx;
                    selectedIdxBtnCount = btnCount;
                }
            }
        }

        if (selectedIdx == -1) {
            return OptionalInt.of(0);
        } else if (currentMin.isPresent() && currentMin.getAsInt() <= maxJoltage) {
            return OptionalInt.empty();
        }
        List<Set<Integer>> minButtons = new ArrayList<>();
        List<Set<Integer>> remainingButtons = new ArrayList<>();
        for (Set<Integer> btn : buttons) {
            if (btn.contains(selectedIdx)) {
                minButtons.add(btn);
            } else {
                remainingButtons.add(btn);
            }
        }
        if (minButtons.isEmpty()) {
            return OptionalInt.empty();
        }

        OptionalInt minPressCount = OptionalInt.empty();
        List<int[]> compositions = Util.getIntegerCompositions(joltages[selectedIdx], minButtons.size());
        for (int c[] : compositions) {
            int newJoltages[] = Arrays.copyOf(joltages, joltages.length);
            boolean works = true;
            for (int i = 0; i < c.length && works; ++i) {
                if (c[i] != 0) {
                    for (int joltageIdx : minButtons.get(i)) {
                        if ((newJoltages[joltageIdx] -= c[i]) < 0) {
                            works = false;
                            break;
                        }
                    }
                }
            }
            if (works) {
                OptionalInt pressCount = this.getMinButtonPressCount(remainingButtons, newJoltages, minPressCount);
                if (pressCount.isPresent()
                        && (minPressCount.isEmpty() || pressCount.getAsInt() < minPressCount.getAsInt())) {
                    minPressCount = pressCount;
                }
            }
        }
        return minPressCount.isPresent() ? OptionalInt.of(joltages[selectedIdx] + minPressCount.getAsInt())
                : OptionalInt.empty();
    }

    private int partTwo(List<Machine> machines) {
        Set<Integer> inProgress = new HashSet<>(IntStream.range(0, machines.size()).mapToObj(i -> i).toList());
        return IntStream.range(0, machines.size()).parallel().map(i -> {
            Machine m = machines.get(i);
            int v = this.getMinButtonPressCount(m.buttons, m.joltageRequirements, OptionalInt.empty()).getAsInt();
            synchronized (this) {
                inProgress.remove(i);
                if (!inProgress.isEmpty() && inProgress.size() < 10) {
                    System.out.println(
                            "Remaining: " + inProgress.stream().map(j -> "" + j).collect(Collectors.joining(", ")));
                }
            }
            return v;
        }).sum();
    }

    @Override
    public void run() {
        List<Machine> machines = this.getPrimaryPuzzleInput().stream().map(Machine::new).toList();
        Dialog.showInfo("Info",
                "Please be aware that finding a solution to part two might take a few minutes.\nThe progress will be printed to the standard output.",
                DEFAULT_FONT, "OK");
        this.addTask("Part one", () -> this.partOne(machines));
        this.addTask("Part two", () -> this.partTwo(machines));
    }
}
