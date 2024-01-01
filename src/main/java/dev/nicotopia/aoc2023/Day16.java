package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import dev.nicotopia.Compass;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day16 extends DayBase {
    private record LaserTile(int x, int y, Compass d) {
    }

    private LaserTile move(int posX, int posY, Compass dir) {
        return switch (dir) {
            case N -> new LaserTile(posX, posY - 1, dir);
            case E -> new LaserTile(posX + 1, posY, dir);
            case S -> new LaserTile(posX, posY + 1, dir);
            case W -> new LaserTile(posX - 1, posY, dir);
        };
    }

    private LaserTile next(LaserTile current, List<String> contraption, Stack<LaserTile> stack) {
        switch (contraption.get(current.y).charAt(current.x)) {
            case '/':
                return this.move(current.x, current.y, switch (current.d) {
                    case N -> Compass.E;
                    case E -> Compass.N;
                    case S -> Compass.W;
                    case W -> Compass.S;
                });
            case '\\':
                return this.move(current.x, current.y, switch (current.d) {
                    case N -> Compass.W;
                    case E -> Compass.S;
                    case S -> Compass.E;
                    case W -> Compass.N;
                });
            case '-':
                if (current.d == Compass.N || current.d == Compass.S) {
                    stack.push(new LaserTile(current.x - 1, current.y, Compass.W));
                    return new LaserTile(current.x + 1, current.y, Compass.E);
                }
                break;
            case '|':
                if (current.d == Compass.E || current.d == Compass.W) {
                    stack.push(new LaserTile(current.x, current.y - 1, Compass.N));
                    return new LaserTile(current.x, current.y + 1, Compass.S);
                }
                break;
        }
        return this.move(current.x, current.y, current.d);
    }

    private int getNumEnergized(List<String> contraption, LaserTile start) {
        Set<LaserTile> visited = new HashSet<>();
        Stack<LaserTile> stack = new Stack<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            LaserTile current = stack.pop();
            while (0 <= current.x && current.x < contraption.getFirst().length() && 0 <= current.y
                    && current.y < contraption.size() && visited.add(current)) {
                current = this.next(current, contraption, stack);
            }
        }
        return (int) visited.stream().map(t -> new Vec2i(t.x, t.y)).distinct().count();
    }

    private int partTwo(List<String> contraption) {
        List<LaserTile> startingStates = new ArrayList<>();
        for (Compass d : Compass.values()) {
            for (int x = 0; x < contraption.getFirst().length(); ++x) {
                startingStates.add(new LaserTile(x, 0, d));
                startingStates.add(new LaserTile(x, contraption.size() - 1, d));
            }
            for (int y = 0; y < contraption.size(); ++y) {
                startingStates.add(new LaserTile(0, y, d));
                startingStates.add(new LaserTile(contraption.getFirst().length() - 1, y, d));
            }
        }
        return startingStates.stream().parallel().mapToInt(s -> this.getNumEnergized(contraption, s)).max().orElse(0);
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day16e.txt");
        List<String> contraption = this.getPrimaryPuzzleInput();
        this.addTask("Part one", () -> this.getNumEnergized(contraption, new LaserTile(0, 0, Compass.E)));
        this.addTask("Part two", () -> this.partTwo(contraption));
    }
}