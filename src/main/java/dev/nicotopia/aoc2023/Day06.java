package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import dev.nicotopia.aoc.DayBase;

public class Day06 extends DayBase {
    private record Game(long time, long distance) {
        public long getNumWinningOutcomes() {
            return LongStream.rangeClosed(0, this.time).map(this::getTravelDistance).filter(d -> this.distance < d)
                    .count();
        }

        public long getTravelDistance(long pressingTime) {
            return Math.max(0, (this.time - pressingTime) * pressingTime);
        }
    }

    private List<Game> games;

    private void processInput() {
        Function<String, List<Integer>> toIntList = s -> Arrays
                .stream(s.replaceAll("\\s+", " ").split(": ")[1].split(" ")).map(Integer::valueOf).toList();
        List<Integer> times = toIntList.apply(this.getPrimaryPuzzleInput().getFirst());
        List<Integer> distances = toIntList.apply(this.getPrimaryPuzzleInput().getLast());
        this.games = IntStream.range(0, times.size()).mapToObj(i -> new Game(times.get(i), distances.get(i))).toList();
    }

    private long partTwo() {
        String time = this.games.stream().map(g -> String.valueOf(g.time())).collect(Collectors.joining());
        String distance = this.games.stream().map(g -> String.valueOf(g.distance())).collect(Collectors.joining());
        return new Game(Long.valueOf(time), Long.valueOf(distance)).getNumWinningOutcomes();
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day06e.txt");
        this.addTask("Process input", this::processInput);
        this.addTask("Part one",
                () -> this.games.stream().mapToLong(Game::getNumWinningOutcomes).reduce(1, (a, b) -> a * b));
        this.addTask("Part two", this::partTwo);
    }
}