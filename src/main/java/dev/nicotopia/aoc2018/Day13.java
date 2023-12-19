package dev.nicotopia.aoc2018;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dev.nicotopia.Vec2i;
import dev.nicotopia.aoc.DayBase;

public class Day13 extends DayBase {
    private enum Compass {
        N, E, S, W
    }

    private class Cart {
        private int crossing = 0;
        private int x;
        private int y;
        private Compass dir;

        public Cart(int x, int y, Compass dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        public void move() {
            this.dir = switch (this.dir) {
                case N ->
                    switch (Day13.this.track[--this.y][this.x]) {
                        case '+' -> switch (this.crossing++ % 3) {
                            case 0 -> Compass.W;
                            case 2 -> Compass.E;
                            default -> this.dir;
                        };
                        case '/' -> Compass.E;
                        case '\\' -> Compass.W;
                        default -> this.dir;
                    };
                case E ->
                    switch (Day13.this.track[this.y][++this.x]) {
                        case '+' -> switch (this.crossing++ % 3) {
                            case 0 -> Compass.N;
                            case 2 -> Compass.S;
                            default -> this.dir;
                        };
                        case '/' -> Compass.N;
                        case '\\' -> Compass.S;
                        default -> this.dir;
                    };
                case S ->
                    switch (Day13.this.track[++this.y][this.x]) {
                        case '+' -> switch (this.crossing++ % 3) {
                            case 0 -> Compass.E;
                            case 2 -> Compass.W;
                            default -> this.dir;
                        };
                        case '/' -> Compass.W;
                        case '\\' -> Compass.E;
                        default -> this.dir;
                    };
                case W ->
                    switch (Day13.this.track[this.y][--this.x]) {
                        case '+' -> switch (this.crossing++ % 3) {
                            case 0 -> Compass.S;
                            case 2 -> Compass.N;
                            default -> this.dir;
                        };
                        case '/' -> Compass.S;
                        case '\\' -> Compass.N;
                        default -> this.dir;
                    };
            };
        }
    }

    private final List<Cart> carts = new ArrayList<>();
    private char[][] track;

    private void processInput() {
        this.track = this.getPrimaryPuzzleInput().stream().map(l -> l.toCharArray()).toArray(char[][]::new);
        for (int y = 0; y < track.length; ++y) {
            for (int x = 0; x < track[y].length; ++x) {
                switch (track[y][x]) {
                    case '^':
                        this.track[y][x] = '|';
                        this.carts.add(new Cart(x, y, Compass.N));
                        break;
                    case '>':
                        this.track[y][x] = '-';
                        this.carts.add(new Cart(x, y, Compass.E));
                        break;
                    case 'v':
                        this.track[y][x] = '|';
                        this.carts.add(new Cart(x, y, Compass.S));
                        break;
                    case '<':
                        this.track[y][x] = '-';
                        this.carts.add(new Cart(x, y, Compass.W));
                        break;
                }
            }
        }
    }

    private void simulate(Predicate<Set<Cart>> onCrash) {
        while (1 < this.carts.size()) {
            Set<Cart> crashedCarts = new HashSet<>();
            for (Cart c : this.carts) {
                if (!crashedCarts.contains(c)) {
                    c.move();
                    Set<Cart> cartsOnSamePos = this.carts.stream()
                            .filter(d -> !crashedCarts.contains(d) && c.x == d.x && c.y == d.y)
                            .collect(Collectors.toSet());
                    if (1 < cartsOnSamePos.size()) {
                        crashedCarts.addAll(cartsOnSamePos);
                    }
                }
            }
            this.carts.removeAll(crashedCarts);
            if (!crashedCarts.isEmpty() && !onCrash.test(crashedCarts)) {
                return;
            }
        }
    }

    private Vec2i partOne() {
        List<Cart> crash = new ArrayList<>();
        this.simulate(cp -> {
            crash.addAll(cp);
            return false;
        });
        return new Vec2i(crash.getFirst().x, crash.getFirst().y);
    }

    private Vec2i partTwo() {
        this.simulate(cp -> 1 < this.carts.size());
        return this.carts.isEmpty() ? null : new Vec2i(this.carts.getFirst().x, this.carts.getFirst().y);
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example 1", "/2018/day13e1.txt");
        this.addPresetFromResource("Example 2", "/2018/day13e2.txt");
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}