package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev.nicotopia.GraphUtil;
import dev.nicotopia.GraphUtil.HashedDijkstraInterface;
import dev.nicotopia.GraphUtil.NodeDistancePair;

public class Day12 {
    public record Position(int x, int y) {
    }

    public static void main(String[] args) throws IOException {
        int terrain[][];
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day12.class.getResourceAsStream("/2022/day12.txt")))) {
            terrain = br.lines().map(l -> l.chars().map(c -> c == 'S' || c == 'E' ? c : c - 'a').toArray())
                    .toArray(int[][]::new);
        }
        Position start = null;
        Position end = null;
        for (int y = 0; y < terrain.length; ++y) {
            for (int x = 0; x < terrain[y].length; ++x) {
                if (terrain[y][x] == 'S') {
                    terrain[y][x] = 0;
                    start = new Position(x, y);
                } else if (terrain[y][x] == 'E') {
                    terrain[y][x] = 'z' - 'a';
                    end = new Position(x, y);
                }
            }
        }
        HashedDijkstraInterface<Position> disjkstraInterface = new HashedDijkstraInterface<Day12.Position>((p, i) -> {
            int c = 0;
            if (p.x != 0 && terrain[p.y][p.x - 1] - 1 <= terrain[p.y][p.x] && c++ == i) {
                return new NodeDistancePair<>(new Position(p.x - 1, p.y), 1);
            } else if (p.x != terrain[p.y].length - 1 && terrain[p.y][p.x + 1] - 1 <= terrain[p.y][p.x] && c++ == i) {
                return new NodeDistancePair<>(new Position(p.x + 1, p.y), 1);
            } else if (p.y != 0 && terrain[p.y - 1][p.x] - 1 <= terrain[p.y][p.x] && c++ == i) {
                return new NodeDistancePair<>(new Position(p.x, p.y - 1), 1);
            } else if (p.y != terrain.length - 1 && terrain[p.y + 1][p.x] - 1 <= terrain[p.y][p.x] && c++ == i) {
                return new NodeDistancePair<>(new Position(p.x, p.y + 1), 1);
            }
            return null;
        });
        long min = Long.MAX_VALUE;
        for (int y = 0; y < terrain.length; ++y) {
            for (int x = 0; x < terrain[y].length; ++x) {
                if (terrain[y][x] == 0) {
                    GraphUtil.dijkstra(disjkstraInterface, new Position(x, y));
                    min = Math.min(min, disjkstraInterface.getDistanceMap().getOrDefault(end, Integer.MAX_VALUE));
                    if (x == start.x && y == start.y) {
                        System.out.println("Part one: " + disjkstraInterface.getDistance(end));
                    }
                }
            }
        }
        System.out.println("Part two: " + min);
    }
}