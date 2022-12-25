package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;

public class Day23 {
    private record Position(int x, int y) {
    }

    private interface Proposer {
        public Optional<Position> propose(Position currentPosition);
    }

    public static void main(String[] args) throws IOException {
        Set<Position> elves = new HashSet<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day23.class.getResourceAsStream("/2022/day23.txt")))) {
            int y = 0;
            for (String line : br.lines().toList()) {
                for (int x = 0; x < line.length(); ++x) {
                    if (line.charAt(x) == '#') {
                        elves.add(new Position(x, y));
                    }
                }
                ++y;
            }
        }
        Proposer p0 = p -> Arrays.asList(-1, 0, 1).stream().map(dx -> new Position(p.x + dx, p.y - 1))
                .noneMatch(elves::contains) ? Optional.of(new Position(p.x, p.y - 1)) : Optional.empty();
        Proposer p1 = p -> Arrays.asList(-1, 0, 1).stream().map(dx -> new Position(p.x + dx, p.y + 1))
                .noneMatch(elves::contains) ? Optional.of(new Position(p.x, p.y + 1)) : Optional.empty();
        Proposer p2 = p -> Arrays.asList(-1, 0, 1).stream().map(dy -> new Position(p.x - 1, p.y + dy))
                .noneMatch(elves::contains) ? Optional.of(new Position(p.x - 1, p.y)) : Optional.empty();
        Proposer p3 = p -> Arrays.asList(-1, 0, 1).stream().map(dy -> new Position(p.x + 1, p.y + dy))
                .noneMatch(elves::contains) ? Optional.of(new Position(p.x + 1, p.y)) : Optional.empty();
        Queue<Proposer> proposers = new LinkedList<>(Arrays.asList(p0, p1, p2, p3));
        int rounds = 0;
        for (;;) {
            Map<Position, List<Position>> proposedPositions = new HashMap<>();
            for (Position elf : elves) {
                if (1 < Arrays.asList(-1, 0, 1).stream()
                        .mapToInt(dx -> (int) Arrays.asList(-1, 0, 1).stream()
                                .map(dy -> new Position(elf.x + dx, elf.y + dy)).filter(elves::contains).count())
                        .sum()) {
                    Optional<Position> proposedPosition = proposers.stream().map(p -> p.propose(elf))
                            .filter(Optional::isPresent).map(Optional::get).findFirst();
                    if (proposedPosition.isPresent()) {
                        List<Position> proposingElves = proposedPositions.get(proposedPosition.get());
                        if (proposingElves == null) {
                            proposedPositions.put(proposedPosition.get(), proposingElves = new LinkedList<>());
                        }
                        proposingElves.add(elf);
                    }
                }
            }
            ++rounds;
            if (proposedPositions.values().stream().noneMatch(l -> l.size() == 1)) {
                break;
            }
            proposedPositions.values().stream().filter(l -> l.size() == 1).map(l -> l.get(0)).forEach(elves::remove);
            proposedPositions.keySet().stream().filter(p -> proposedPositions.get(p).size() == 1).forEach(elves::add);
            proposers.offer(proposers.poll());
            if (rounds == 10) {
                int minX = elves.stream().mapToInt(Position::x).min().getAsInt();
                int maxX = elves.stream().mapToInt(Position::x).max().getAsInt();
                int minY = elves.stream().mapToInt(Position::y).min().getAsInt();
                int maxY = elves.stream().mapToInt(Position::y).max().getAsInt();
                int sum = IntStream.rangeClosed(minX, maxX).map(x -> (int) IntStream.rangeClosed(minY, maxY)
                        .filter(y -> !elves.contains(new Position(x, y))).count()).sum();
                System.out.println("Part one: " + sum);
            }
        }
        System.out.println("Part two: " + rounds);
    }
}