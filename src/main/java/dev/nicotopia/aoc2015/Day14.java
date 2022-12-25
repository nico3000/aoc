package dev.nicotopia.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
    private record Reindeer(String name, int speed, int flyTime, int restTime) {
    }

    public static void main(String[] args) throws IOException {
        List<Reindeer> reindeers;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day14.class.getResourceAsStream("/2015/day14.txt")))) {
            Pattern p = Pattern
                    .compile("(\\w+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.");
            reindeers = br
                    .lines().map(p::matcher).filter(Matcher::matches).map(m -> new Reindeer(m.group(1),
                            Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4))))
                    .toList();
        }
        int time = 2503;
        int winnerDistance = reindeers.stream().mapToInt(r -> {
            int distance = time / (r.flyTime + r.restTime) * (r.flyTime * r.speed);
            int rem = time % (r.flyTime + r.restTime);
            return distance + Math.min(rem, r.flyTime) * r.speed;
        }).max().getAsInt();
        System.out.println("Part one: " + winnerDistance);
        Map<Reindeer, Integer> distances = new HashMap<>();
        Map<Reindeer, Integer> points = new HashMap<>();
        reindeers.forEach(r -> distances.put(r, 0));
        reindeers.forEach(r -> points.put(r, 0));
        for (int t = 0; t < time; ++t) {
            for (Reindeer r : reindeers) {
                if (t % (r.flyTime + r.restTime) < r.flyTime) {
                    distances.put(r, distances.get(r) + r.speed);
                }
            }
            Reindeer leader = reindeers.stream().reduce((a, b) -> distances.get(a) < distances.get(b) ? b : a).get();
            points.put(leader, points.get(leader) + 1);
        }
        System.out.println("Part two: " + points.values().stream().mapToInt(i -> i).max().getAsInt());
    }
}
