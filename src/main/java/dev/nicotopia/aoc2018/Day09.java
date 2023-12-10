package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.GiantList;
import dev.nicotopia.aoc.DayBase;

public class Day09 extends DayBase {
    private long play(int numPlayers, int numMarbles) {
        long scores[] = new long[numPlayers];
        GiantList<Integer> game = new GiantList<>();
        game.add(0);
        int current = 0;
        for (int i = 1; i < numMarbles; ++i) {
            if (i % 23 == 0) {
                current = (current + game.size() - 7) % game.size();
                scores[(i - 1) % numPlayers] += i + game.remove(current);
            } else {
                current = (current + 2) % game.size();
                game.add(current, i);
            }
        }
        return Arrays.stream(scores).max().getAsLong();
    }

    @Override
    public void run() {
        this.addPreset("Example A", "10 players; last marble is worth 1618 points");
        this.addPreset("Example B", "13 players; last marble is worth 7999 points");
        this.addPreset("Example C", "17 players; last marble is worth 1104 points");
        this.addPreset("Example D", "21 players; last marble is worth 6111 points");
        this.addPreset("Example E", "30 players; last marble is worth 5807 points");
        Matcher m = Pattern.compile("(\\d+) .* (\\d+) .*").matcher(this.getPrimaryPuzzleInput().getFirst());
        if (m.matches()) {
            int lastMarbleValue = Integer.valueOf(m.group(2));
            this.addTask("Part one", () -> this.play(Integer.valueOf(m.group(1)), lastMarbleValue + 1));
            this.addTask("Part two", () -> this.play(Integer.valueOf(m.group(1)), 100 * lastMarbleValue + 1));
        }
    }
}