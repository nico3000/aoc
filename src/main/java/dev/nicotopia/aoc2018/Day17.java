package dev.nicotopia.aoc2018;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day17 extends DayBase {
    private enum VeinDir {
        SOUTH, EAST,
    }

    private record Vein(int x, int y, VeinDir dir, int length) {
        public int maxX() {
            return this.dir == VeinDir.EAST ? this.x + this.length - 1 : this.x;
        }

        public int maxY() {
            return this.dir == VeinDir.SOUTH ? this.y + this.length - 1 : this.y;
        }
    }

    private enum Material {
        SAND, CLAY, MOVING_WATER, STANDING_WATER
    }

    private List<Vein> veins;
    private int minX;
    private Material[][] map;

    private void set(int x, int y, Material type) {
        this.map[y][x - this.minX] = type;
    }

    private Material get(int x, int y) {
        return this.map[y][x - this.minX];
    }

    private void processInput() {
        Pattern p = Pattern.compile("([xy])=(-?\\d+), ([x,y])=(-?\\d+)\\.\\.(-?\\d+)");
        this.veins = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).map(m -> {
            VeinDir dir = m.group(1).equals("x") ? VeinDir.SOUTH : VeinDir.EAST;
            int x = Integer.valueOf(m.group(dir == VeinDir.EAST ? 4 : 2));
            int y = Integer.valueOf(m.group(dir == VeinDir.EAST ? 2 : 4));
            int length = Integer.valueOf(m.group(5)) - Integer.valueOf(m.group(4)) + 1;
            return new Vein(x, y, dir, length);
        }).toList();
        this.minX = this.veins.stream().mapToInt(Vein::x).min().getAsInt() - 1;
        int maxX = this.veins.stream().mapToInt(Vein::maxX).max().getAsInt();
        int maxY = this.veins.stream().mapToInt(Vein::maxY).max().getAsInt();
        this.map = new Material[maxY + 1][maxX - minX + 2];
        Arrays.stream(this.map).forEach(r -> Arrays.fill(r, Material.SAND));
        for (Vein v : this.veins) {
            for (int i = 0; i < v.length; ++i) {
                this.set(v.dir == VeinDir.EAST ? v.x + i : v.x, v.dir == VeinDir.SOUTH ? v.y + i : v.y, Material.CLAY);
            }
        }
    }

    private void fill(int x, int y, Queue<Vec2i> queue) {
        if (this.get(x, y) != Material.SAND) {
            return;
        }
        while (y < this.map.length && this.get(x, y) == Material.SAND) {
            this.set(x, y++, Material.MOVING_WATER);
        }
        if (y == this.map.length || this.get(x, y) == Material.MOVING_WATER) {
            return;
        }
        while (--y != -1) {
            boolean boundedLeft = true;
            int l = x - 1;
            while (this.get(l, y) != Material.CLAY) {
                if (this.get(l, y + 1) == Material.SAND) {
                    boundedLeft = false;
                    this.set(l, y, Material.MOVING_WATER);
                    queue.offer(new Vec2i(l, y + 1));
                    break;
                }
                --l;
            }
            boolean boundedRight = true;
            int r = x + 1;
            while (this.get(r, y) != Material.CLAY) {
                if (this.get(r, y + 1) == Material.SAND) {
                    boundedRight = false;
                    this.set(r, y, Material.MOVING_WATER);
                    queue.offer(new Vec2i(r, y + 1));
                    break;
                }
                ++r;
            }
            for (int i = l + 1; i < r; ++i) {
                this.set(i, y, boundedLeft && boundedRight ? Material.STANDING_WATER : Material.MOVING_WATER);
            }
            if (!boundedLeft || !boundedRight) {
                return;
            }
        }
    }

    private int partOne() {
        Queue<Vec2i> queue = new LinkedList<>();
        queue.offer(new Vec2i(500, 0));
        while (!queue.isEmpty()) {
            Vec2i p = queue.poll();
            this.fill(p.x(), p.y(), queue);
        }
        int minY = this.veins.stream().mapToInt(Vein::y).min().getAsInt();
        return (int) Arrays.stream(this.map).map(Arrays::stream).flatMap(s -> s)
                .filter(m -> m == Material.MOVING_WATER || m == Material.STANDING_WATER).count() - minY;
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", Arrays.stream(this.map).map(Arrays::stream).flatMap(s -> s)
                .filter(m -> m == Material.STANDING_WATER)::count);
        this.pushPostResultsOption("Show image...", () -> Dialog.showImage("The beautiful image <3",
                new ImageComponent(ImageComponent.imageFrom(this.map))));
    }
}