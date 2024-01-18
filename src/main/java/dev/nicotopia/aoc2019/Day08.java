package dev.nicotopia.aoc2019;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;

public class Day08 extends DayBase {
    private final int WIDTH = 25;
    private final int HEIGHT = 6;

    private class Layer {
        private final int layerIdx;

        public Layer(int layerIdx) {
            this.layerIdx = layerIdx;
        }

        public Color get(int x, int y) {
            return switch (Day08.this.data[this.layerIdx * WIDTH * HEIGHT + y * WIDTH + x]) {
                case 0 -> Color.BLACK;
                case 1 -> Color.WHITE;
                default -> null;
            };
        }

        public long getPixelCount(int value) {
            return Arrays.stream(Day08.this.data, this.layerIdx * WIDTH * HEIGHT, (this.layerIdx + 1) * WIDTH * HEIGHT)
                    .filter(p -> p == value).count();
        }
    }

    private int[] data;
    private List<Layer> layers;

    private void processInput() {
        this.data = this.getPrimaryPuzzleInput().getFirst().chars().map(c -> c - '0').toArray();
        this.layers = IntStream.range(0, data.length / (WIDTH * HEIGHT)).mapToObj(Layer::new).toList();
    }

    private long partOne() {
        Layer layer = this.layers.stream().min((a, b) -> Long.compare(a.getPixelCount(0), b.getPixelCount(0))).get();
        return layer.getPixelCount(1) * layer.getPixelCount(2);
    }

    private BufferedImage partTwo() {
        return ImageComponent.imageFrom(WIDTH, HEIGHT,
                (x, y) -> this.layers.stream().map(l -> l.get(x, y)).filter(c -> c != null).findFirst().get());
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        BufferedImage img = this.addSilentTask("Part two", this::partTwo);
        this.pushPostResultsOption("Show part two image...",
                () -> Dialog.showImage("The unbelievable message <3", new ImageComponent(img)));
    }
}