package dev.nicotopia.aoc2023;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;

public class Day07 extends DayBase {
    private enum HandType {
        HIGH, ONE_PAIR, TWO_PAIRS, THREE, FULL_HOUSE, FOUR, FIVE
    }

    private enum RuleType {
        PART_ONE, PART_TWO
    }

    private record Hand(String str, String compStr, HandType handType, int bid)
            implements Comparable<Hand> {
        @Override
        public int compareTo(Hand o) {
            return this.handType != o.handType ? this.handType.compareTo(o.handType)
                    : this.compStr.compareTo(o.compStr);
        }
    }

    private static Hand createHand(RuleType ruleType, String hand, int bid) {
        String t = hand.chars().sorted().mapToObj(c -> String.valueOf((char) c)).reduce("", String::concat)
                .replaceAll("(.)\\1\\1\\1\\1", "{5}").replaceAll("(.)\\1\\1\\1", "{4}").replaceAll("(.)\\1\\1", "{3}")
                .replaceAll("(.)\\1", "{2}");
        int numJokers = switch (ruleType) {
            case PART_ONE -> 0;
            case PART_TWO -> (int) hand.chars().filter(c -> c == 'J').count();
        };
        HandType handType;
        if (t.contains("{5}")) {
            handType = HandType.FIVE;
        } else if (t.contains("{4}")) {
            handType = numJokers == 0 ? HandType.FOUR : HandType.FIVE;
        } else if (t.contains("{3}")) {
            if (t.contains("{2}")) {
                handType = numJokers == 0 ? HandType.FULL_HOUSE : HandType.FIVE;
            } else {
                handType = numJokers == 0 ? HandType.THREE : HandType.FOUR;
            }
        } else if (t.contains("{2}")) {
            if (t.indexOf("{2}") != t.lastIndexOf("{2}")) {
                handType = numJokers == 2 ? HandType.FOUR : numJokers == 1 ? HandType.FULL_HOUSE : HandType.TWO_PAIRS;
            } else {
                handType = numJokers == 0 ? HandType.ONE_PAIR : HandType.THREE;
            }
        } else {
            handType = numJokers == 1 ? HandType.ONE_PAIR : HandType.HIGH;
        }
        String compStr = hand.replace('A', 'E').replace('K', 'D').replace('Q', 'C').replace('T', 'A').replace('J',
                switch (ruleType) {
                    case PART_ONE -> 'B';
                    case PART_TWO -> '1';
                });
        return new Hand(hand, compStr, handType, bid);
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day07e.txt");
        Function<RuleType, List<Hand>> toHands = rt -> this.getPrimaryPuzzleInput().stream()
                .map(l -> createHand(rt, l.substring(0, 5), Integer.valueOf(l.substring(6)))).sorted().toList();
        Function<List<Hand>, Integer> sumUp = l -> IntStream.range(0, l.size()).map(i -> (i + 1) * l.get(i).bid).sum();
        List<Hand> partOneHands = this.addSilentTask("Process input, task one", () -> toHands.apply(RuleType.PART_ONE));
        this.addTask("Part one", () -> sumUp.apply(partOneHands));
        List<Hand> partTwoHands = this.addSilentTask("Process input, task one", () -> toHands.apply(RuleType.PART_TWO));
        this.addTask("Part two", () -> sumUp.apply(partTwoHands));
    }
}