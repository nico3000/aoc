package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.GraphUtil;
import dev.nicotopia.GraphUtil.HashedDijkstraInterface;
import dev.nicotopia.GraphUtil.NodeDistancePair;

public class Day16 {
    public static class Valve {
        private final String name;
        private final List<Valve> connectedValves = new ArrayList<>();
        private final List<Integer> connectedValvesDistances = new ArrayList<>();
        private final int flowRate;
        private boolean open = false;

        public Valve(String name, int flowRate) {
            this.name = name;
            this.flowRate = flowRate;
        }

        public void addConnectedValve(Valve valve, int distance) {
            this.connectedValves.add(valve);
            this.connectedValvesDistances.add(distance);
        }

        public void removeConnectedValveGently(Valve valve) {
            int idx = this.connectedValves.indexOf(valve);
            if (idx != -1) {
                this.connectedValves.remove(idx);
                int distance = this.connectedValvesDistances.remove(idx);
                for (int i = 0; i < valve.connectedValves.size(); ++i) {
                    Valve v = valve.connectedValves.get(i);
                    if (v != this) {
                        this.addConnectedValve(v, distance + valve.connectedValvesDistances.get(i));
                    }
                }
            }
        }

        @Override
        public String toString() {
            return this.name + " " + this.flowRate;
        }
    }

    public static void main(String[] args) throws IOException {
        Map<Valve, String[]> valveConnections = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day16.class.getResourceAsStream("/2022/day16.txt")))) {
            Pattern p = Pattern
                    .compile("Valve ([A-Z]+) has flow rate=([0-9]+); tunnels? leads? to valves? (([A-Z]+, )*[A-Z]+)");
            br.lines().map(p::matcher).filter(Matcher::matches).forEach(m -> valveConnections
                    .put(new Valve(m.group(1), Integer.valueOf(m.group(2))), m.group(3).split(", ")));
        }
        valveConnections.forEach((v, c) -> Arrays.stream(c)
                .map(vn -> valveConnections.keySet().stream().filter(v1 -> vn.equals(v1.name)).findAny().get())
                .forEach(v1 -> v.addConnectedValve(v1, 1)));
        List<Valve> zeroFlowRateValves = valveConnections.keySet().stream()
                .filter(v -> v.flowRate == 0 && !v.name.equals("AA")).toList();
        zeroFlowRateValves.forEach(v -> valveConnections.keySet().forEach(v2 -> v2.removeConnectedValveGently(v)));
        valveConnections.keySet().forEach(v -> zeroFlowRateValves.forEach(v::removeConnectedValveGently));
        List<Valve> valves = new LinkedList<>(
                valveConnections.keySet().stream().filter(v -> !zeroFlowRateValves.contains(v)).toList());
        HashedDijkstraInterface<Valve> di = new HashedDijkstraInterface<>((v, i) -> i < v.connectedValves.size()
                ? new NodeDistancePair<>(v.connectedValves.get(i), v.connectedValvesDistances.get(i))
                : null);
        Map<Valve, Map<Valve, Integer>> shortestPaths = new HashMap<>();
        for (Valve v : valves) {
            GraphUtil.dijkstra(di, v);
            shortestPaths.put(v, new HashMap<>(di.getDistanceMap()));
        }
        Valve start = valveConnections.keySet().stream().filter(v -> v.name.equals("AA")).findAny().get();
        long now = System.currentTimeMillis();
        int max = findMaxReleasableSteam(shortestPaths, start, 30);
        System.out.printf("Part one: %d, time: %.2fs\n", max, 1e-3f * (float) (System.currentTimeMillis() - now));
        now = System.currentTimeMillis();
        players[0] = new Player(start, 26);
        players[1] = new Player(start, 26);
        max = findMaxReleasableSteamWithTwo(shortestPaths);
        System.out.printf("Part two: %d, time: %.2fs\n", max, 1e-3f * (float) (System.currentTimeMillis() - now));
    }

    private static int findMaxReleasableSteam(Map<Valve, Map<Valve, Integer>> shortestPaths, Valve currentValve,
            int remainingTime) {
        if (remainingTime <= 0) {
            return 0;
        } else {
            LongAccumulator accu = new LongAccumulator((a, b) -> Math.max(a, b), 0);
            currentValve.open = true;
            shortestPaths.getOrDefault(currentValve, Collections.emptyMap()).forEach((v, d) -> {
                if (!v.open) {
                    accu.accumulate(findMaxReleasableSteam(shortestPaths, v,
                            remainingTime - d - (currentValve.flowRate == 0 ? 0 : 1)));
                }
            });
            currentValve.open = false;
            return currentValve.flowRate * (remainingTime - 1) + accu.intValue();
        }
    }

    public static class Player {
        private Valve dest;
        private int eta;

        public Player(Valve dest, int eta) {
            this.dest = dest;
            this.eta = eta;
        }
    }

    private static final Player players[] = new Player[2];

    private static int findMaxReleasableSteamWithTwo(Map<Valve, Map<Valve, Integer>> shortestPaths) {
        int idx = players[0].eta < players[1].eta ? 1 : 0;
        Player player = players[idx];
        if (player.eta <= 0) {
            return 0;
        }
        Player other = players[1 - idx];
        Valve current = player.dest;
        int remainingTime = player.eta;
        current.open = true;
        LongAccumulator accu = new LongAccumulator((a, b) -> Math.max(a, b), 0);
        shortestPaths.getOrDefault(current, Collections.emptyMap()).forEach((v, d) -> {
            if (!v.open && v != other.dest) {
                player.eta = remainingTime - d - (current.flowRate == 0 ? 0 : 1);
                if (1 < player.eta) {
                    player.dest = v;
                    accu.accumulate(findMaxReleasableSteamWithTwo(shortestPaths));
                }
            }
        });
        if (accu.intValue() == 0) {
            player.dest = null;
            player.eta = Integer.MIN_VALUE;
            accu.accumulate(findMaxReleasableSteamWithTwo(shortestPaths));
        }
        current.open = false;
        player.eta = remainingTime;
        player.dest = current;
        return current.flowRate * (remainingTime - 1) + accu.intValue();
    }
}