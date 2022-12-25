package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day19 {
    private record Blueprint(int id, int oreRobotOreCost, int clayRobotOreCost, int obsidianRobotOreCost,
            int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost) {
    }

    public static void main(String[] args) throws IOException {
        List<Blueprint> blueprints;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day19.class.getResourceAsStream("/2022/day19.txt")))) {
            Pattern p = Pattern.compile(
                    "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.");
            blueprints = br.lines().map(p::matcher).filter(Matcher::matches)
                    .map(m -> new Blueprint(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)),
                            Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5)),
                            Integer.valueOf(m.group(6)), Integer.valueOf(m.group(7))))
                    .toList();
        }
        int partOneSum = blueprints.stream().parallel().mapToInt(b -> {
            currentMax.set(0);
            int maxGeodes = findMaxGeodes(b, 24);
            System.out.printf("Blueprint %d done: %d (%d x %d)\n", b.id, b.id * maxGeodes, b.id, maxGeodes);
            return maxGeodes * b.id;
        }).sum();
        System.out.println("Part one: " + partOneSum);
        int partTwoProduct = IntStream.range(0, 3).parallel().map(i -> {
            currentMax.set(0);
            int maxGeodes = findMaxGeodes(blueprints.get(i), 32);
            System.out.printf("Blueprint %d done: %d\n", blueprints.get(i).id, maxGeodes);
            return maxGeodes;
        }).reduce(1, (a, b) -> a * b);
        System.out.println("Part two: " + partTwoProduct);
    }

    private static final int ORE_IDX = 0;
    private static final int CLAY_IDX = 1;
    private static final int OBSIDIAN_IDX = 2;
    private static final int GEODE_IDX = 3;
    private static final int ORE_ROBOT_IDX = 4;
    private static final int CLAY_ROBOT_IDX = 5;
    private static final int OBSIDIAN_ROBOT_IDX = 6;
    private static final int GEODE_ROBOT_IDX = 7;

    private static ThreadLocal<Integer> currentMax = new ThreadLocal<>();

    public static int findMaxGeodes(Blueprint b, int maxMinutes) {
        int status[] = IntStream.range(0, 8).map(i -> i == ORE_ROBOT_IDX ? 1 : 0).toArray();
        currentMax.set(0);
        return findMaxGeodes(b, status, maxMinutes);
    }

    public static int findMaxGeodes(Blueprint blueprint, int status[], int remainingMinutes) {
        if (remainingMinutes == 0) {
            currentMax.set(Math.max(currentMax.get(), status[GEODE_IDX]));
            return status[GEODE_IDX];
        }
        int projectedMaxGeodeProduction = status[GEODE_IDX] + remainingMinutes * status[GEODE_ROBOT_IDX]
                + remainingMinutes * (remainingMinutes - 1) / 2;
        if (projectedMaxGeodeProduction <= currentMax.get()) {
            return 0;
        }
        int max = 0;
        if (blueprint.geodeRobotOreCost <= status[ORE_IDX]
                && blueprint.geodeRobotObsidianCost <= status[OBSIDIAN_IDX]) {
            status[ORE_IDX] -= blueprint.geodeRobotOreCost;
            status[OBSIDIAN_IDX] -= blueprint.geodeRobotObsidianCost;
            incStatus(status);
            ++status[GEODE_ROBOT_IDX];
            max = Math.max(max, findMaxGeodes(blueprint, status, remainingMinutes - 1));
            --status[GEODE_ROBOT_IDX];
            decStatus(status);
            status[OBSIDIAN_IDX] += blueprint.geodeRobotObsidianCost;
            status[ORE_IDX] += blueprint.geodeRobotOreCost;
        }
        if (blueprint.obsidianRobotOreCost <= status[ORE_IDX] && blueprint.obsidianRobotClayCost <= status[CLAY_IDX]) {
            status[ORE_IDX] -= blueprint.obsidianRobotOreCost;
            status[CLAY_IDX] -= blueprint.obsidianRobotClayCost;
            incStatus(status);
            ++status[OBSIDIAN_ROBOT_IDX];
            max = Math.max(max, findMaxGeodes(blueprint, status, remainingMinutes - 1));
            --status[OBSIDIAN_ROBOT_IDX];
            decStatus(status);
            status[CLAY_IDX] += blueprint.obsidianRobotClayCost;
            status[ORE_IDX] += blueprint.obsidianRobotOreCost;
        }
        incStatus(status);
        max = Math.max(max, findMaxGeodes(blueprint, status, remainingMinutes - 1));
        decStatus(status);
        if (blueprint.clayRobotOreCost <= status[ORE_IDX]) {
            status[ORE_IDX] -= blueprint.clayRobotOreCost;
            incStatus(status);
            ++status[CLAY_ROBOT_IDX];
            max = Math.max(max, findMaxGeodes(blueprint, status, remainingMinutes - 1));
            --status[CLAY_ROBOT_IDX];
            decStatus(status);
            status[ORE_IDX] += blueprint.clayRobotOreCost;
        }
        if (blueprint.oreRobotOreCost <= status[ORE_IDX]) {
            status[ORE_IDX] -= blueprint.oreRobotOreCost;
            incStatus(status);
            ++status[ORE_ROBOT_IDX];
            max = Math.max(max, findMaxGeodes(blueprint, status, remainingMinutes - 1));
            --status[ORE_ROBOT_IDX];
            decStatus(status);
            status[ORE_IDX] += blueprint.oreRobotOreCost;
        }
        return max;
    }

    public static void incStatus(int status[]) {
        for (int i = 0; i < 4; ++i) {
            status[i] += status[i + 4];
        }
    }

    public static void decStatus(int status[]) {
        for (int i = 0; i < 4; ++i) {
            status[i] -= status[i + 4];
        }
    }
}