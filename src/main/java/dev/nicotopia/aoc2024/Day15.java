package dev.nicotopia.aoc2024;

import java.util.List;
import java.util.stream.Collectors;

import dev.nicotopia.Compass;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.CharMap2D;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day15 extends DayBase {
    private CharMap2D map;
    private String commands;

    private void processInput(boolean resize) {
        List<String> input = this.getPrimaryPuzzleInput();
        int splitIndex = input.indexOf("");
        List<String> mapInput = input.subList(0, splitIndex);
        if (resize) {
            mapInput = mapInput.stream().map(l -> {
                char resized[] = new char[2 * l.length()];
                for (int i = 0; i < l.length(); ++i) {
                    switch (l.charAt(i)) {
                        case 'O':
                            resized[2 * i] = '[';
                            resized[2 * i + 1] = ']';
                            break;
                        case '@':
                            resized[2 * i] = '@';
                            resized[2 * i + 1] = '.';
                            break;
                        default:
                            resized[2 * i] = l.charAt(i);
                            resized[2 * i + 1] = l.charAt(i);
                            break;
                    }
                }
                return String.valueOf(resized);
            }).toList();
        }
        this.map = new CharMap2D(mapInput.stream().map(String::toCharArray).toArray(char[][]::new));
        this.commands = input.subList(splitIndex + 1, input.size()).stream().collect(Collectors.joining());
    }

    private boolean isMoveable(Vec2i from, Compass dir) {
        if (this.map.is(from, '.')) {
            return true;
        }
        Vec2i to = from.getNeighbour(dir);
        if (this.map.is(from, 'O') && this.isMoveable(to, dir)) {
            return true;
        } else if (this.map.is(from, '[')) {
            if (dir == Compass.E || dir == Compass.W) {
                return this.isMoveable(to, dir);
            } else {
                return this.isMoveable(to, dir) && this.isMoveable(to.getNeighbour(Compass.E), dir);
            }
        } else if (this.map.is(from, ']')) {
            if (dir == Compass.E || dir == Compass.W) {
                return this.isMoveable(to, dir);
            } else {
                return this.isMoveable(to, dir) && this.isMoveable(to.getNeighbour(Compass.W), dir);
            }
        }
        return false;
    }

    private void move(Vec2i from, Compass dir) {
        if (!this.map.is(from, '.')) {
            Vec2i to = from.getNeighbour(dir);
            this.move(to, dir);
            this.map.set(to, this.map.get(from));
            this.map.set(from, '.');
            if (dir == Compass.N || dir == Compass.S) {
                if (this.map.is(to, '[')) {
                    this.move(from.getNeighbour(Compass.E), dir);
                } else if (this.map.is(to, ']')) {
                    this.move(from.getNeighbour(Compass.W), dir);
                }
            }
        }
    }

    private int execute() {
        Vec2i pos = this.map.findAnyPositionOf('@').get();
        this.map.set(pos, '.');
        for (char c : this.commands.toCharArray()) {
            Compass dir = switch (c) {
                case '^' -> Compass.N;
                case '>' -> Compass.E;
                case 'v' -> Compass.S;
                case '<' -> Compass.W;
                default -> throw new AocException("malformed input");
            };
            Vec2i next = pos.getNeighbour(dir);
            if (this.isMoveable(next, dir)) {
                this.move(next, dir);
                pos = next;
            }
        }
        return this.map.coordinates((p, c) -> c == 'O' || c == '[').mapToInt(p -> 100 * p.y() + p.x()).sum();
    }

    @Override
    public void run() {
        this.addTask("Process input for part one", () -> this.processInput(false));
        this.addTask("Part one", this::execute);
        this.addTask("Process input for part two", () -> this.processInput(true));
        this.addTask("Part two", this::execute);
    }
}
