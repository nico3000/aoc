package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.List;

import dev.nicotopia.aoc.DayBase;

public class Day04 extends DayBase {
    private record Card(List<Integer> winningNumbers, List<Integer> ownNumbers) {
        public int getNumOwnWinningCards() {
            return (int) this.ownNumbers.stream().filter(this.winningNumbers::contains).count();
        }
    }

    private List<Card> processInput() {
        return this.getPrimaryPuzzleInput().stream().map(line -> {
            String split[] = line.substring(line.indexOf(':') + 1).split("\\|");
            return new Card(Arrays.stream(split[0].trim().split("\\s+")).map(n -> Integer.valueOf(n.trim())).toList(),
                    Arrays.stream(split[1].trim().split("\\s+")).map(n -> Integer.valueOf(n.trim())).toList());
        }).toList();
    }

    private int partTwo(List<Card> cards) {
        int extraCounts[] = new int[cards.size()];
        for (int i = 0; i < cards.size(); ++i) {
            int numWinningCards = cards.get(i).getNumOwnWinningCards();
            for (int j = 0; j < numWinningCards && i + j + 1 < cards.size(); ++j) {
                extraCounts[i + j + 1] += 1 + extraCounts[i];
            }
        }
        return Arrays.stream(extraCounts).sum() + cards.size();
    }

    @Override
    public void run() {
        List<Card> cards = this.addSilentTask("Process input", this::processInput);
        this.addTask("Part one", () -> cards.stream().mapToInt(c -> (1 << c.getNumOwnWinningCards()) / 2).sum());
        this.addTask("Part two", () -> this.partTwo(cards));
    }
}