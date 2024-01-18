package dev.nicotopia.aoc2019;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day13 extends DayBase {
    private enum Tile {
        EMPTY, WALL, BLOCK, PADDLE, BALL
    }

    private int numProcessedValues = 0;
    private int x;
    private int y;
    private Vec2i offset;
    private Tile[][] screen;
    private int score;
    private final ImageComponent imageComponent = new ImageComponent(null);
    private int sleepMillis = 0;

    private void processValuePartA(Map<Vec2i, Tile> tileMap, long v) {
        switch (this.numProcessedValues++ % 3) {
            case 0 -> this.x = (int) v;
            case 1 -> this.y = (int) v;
            case 2 -> tileMap.put(new Vec2i(this.x, this.y), Tile.values()[(int) v]);
        }
    }

    private void processValuePartB(long v) {
        switch (this.numProcessedValues++ % 3) {
            case 0 -> this.x = (int) v;
            case 1 -> this.y = (int) v;
            case 2 -> {
                if (this.x == -1 && this.y == 0) {
                    this.score = (int) v;
                } else {
                    this.screen[this.y - this.offset.y()][this.x - this.offset.x()] = Tile.values()[(int) v];
                }
            }
        }
    }

    private int partOne() {
        Map<Vec2i, Tile> tileMap = new HashMap<>();
        new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst()).execute(() -> 0,
                v -> this.processValuePartA(tileMap, v));
        var extents = Vec2i.getExtents(tileMap.keySet());
        this.offset = extents.first();
        this.screen = new Tile[extents.second().y() - this.offset.y() + 1][extents.second().x() - this.offset.x() + 1];
        tileMap.entrySet().forEach(
                e -> this.screen[e.getKey().y() - this.offset.y()][e.getKey().x() - this.offset.x()] = e.getValue());
        BufferedImage img = ImageComponent.imageFrom(this.screen);
        this.pushPostResultsOption("Show part one screen",
                () -> Dialog.showImage("The wonderful screen <3", new ImageComponent(img)));
        return (int) tileMap.values().stream().filter(t -> t == Tile.BLOCK).count();
    }

    private int partTwo(boolean watch) {
        this.imageComponent.setImage(ImageComponent.imageFrom(this.screen), true);
        if (watch) {
            this.sleepMillis = 50;
            new Thread(() -> {
                new Dialog("Part two", this.imageComponent, null, "Close").show();
                this.sleepMillis = 0;
            }).start();
        }
        this.numProcessedValues = 0;
        IntcodeMachine arcade = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
        arcade.set(0, 2);
        arcade.execute(this::requestInput, this::processValuePartB);
        return this.score;
    }

    private long requestInput() {
        this.imageComponent.setImage(ImageComponent.imageFrom(this.screen), false);
        if (this.sleepMillis != 0) {
            try {
                Thread.sleep(this.sleepMillis);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        Vec2i ball = Vec2i.streamFromRectangle(0, 0, this.screen[0].length, this.screen.length)
                .filter(p -> this.screen[p.y()][p.x()] == Tile.BALL).findAny().get();
        Vec2i paddle = Vec2i.streamFromRectangle(0, 0, this.screen[0].length, this.screen.length)
                .filter(p -> this.screen[p.y()][p.x()] == Tile.PADDLE).findAny().get();
        return ball.x() < paddle.x() ? -1 : ball.x() == paddle.x() ? 0 : 1;
    }

    @Override
    public void run() {
        this.addTask("Part one", this::partOne);
        boolean watch = Dialog.showYesNoQuestion("Watch?", "Do you want to watch the computer playing? :D");
        this.addTask("Part two", () -> this.partTwo(watch));
    }
}
