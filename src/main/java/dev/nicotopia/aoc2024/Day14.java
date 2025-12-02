package dev.nicotopia.aoc2024;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day14 extends DayBase {
    private record Robot(Vec2i p, Vec2i v) {
        public Vec2i simulate(int numSteps, int numTilesX, int numTilesY) {
            int x = ((p.x() + numSteps * this.v.x()) % numTilesX + numTilesX) % numTilesX;
            int y = ((p.y() + numSteps * this.v.y()) % numTilesY + numTilesY) % numTilesY;
            return new Vec2i(x, y);
        }
    }

    private List<Robot> robots;

    private long partOne() {
        int numTilesX = this.getIntInput("Num tiles X");
        int numTilesY = this.getIntInput("Num tiles Y");
        Map<Vec2i, Long> map = this.robots.stream().map(r -> r.simulate(100, numTilesX, numTilesY))
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
        long tl = map.entrySet().stream().filter(e -> e.getKey().x() < numTilesX / 2 && e.getKey().y() < numTilesY / 2)
                .mapToLong(e -> e.getValue()).sum();
        long tr = map.entrySet().stream().filter(e -> numTilesX / 2 < e.getKey().x() && e.getKey().y() < numTilesY / 2)
                .mapToLong(e -> e.getValue()).sum();
        long bl = map.entrySet().stream().filter(e -> e.getKey().x() < numTilesX / 2 && numTilesY / 2 < e.getKey().y())
                .mapToLong(e -> e.getValue()).sum();
        long br = map.entrySet().stream().filter(e -> numTilesX / 2 < e.getKey().x() && numTilesY / 2 < e.getKey().y())
                .mapToLong(e -> e.getValue()).sum();
        return tl * tr * bl * br;
    }

    private void apply(Set<Vec2i> positions, BufferedImage image) {
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                image.setRGB(x, y, positions.contains(new Vec2i(x, y)) ? Color.GREEN.getRGB() : Color.BLACK.getRGB());
            }
        }
    }

    private int nextCandidate(BufferedImage image, int fromNumSteps) {
        for (int numSteps = fromNumSteps;; ++numSteps) {
            int fNumSteps = numSteps;
            Set<Vec2i> positions = this.robots.stream()
                    .map(r -> r.simulate(fNumSteps, image.getWidth(), image.getHeight())).distinct()
                    .collect(Collectors.toSet());
            for (int y = 0; y < image.getHeight(); ++y) {
                int numConsecutiveRobots = 0;
                for (int x = 0; x < image.getWidth(); ++x) {
                    boolean isRobot = positions.contains(new Vec2i(x, y));
                    numConsecutiveRobots = isRobot ? numConsecutiveRobots + 1 : 0;
                    if (10 < numConsecutiveRobots) {
                        this.apply(positions, image);
                        return numSteps;
                    }
                }
            }

        }
    }

    private int partTwo() {
        int numTilesX = this.getIntInput("Num tiles X");
        int numTilesY = this.getIntInput("Num tiles Y");
        BufferedImage image = new BufferedImage(numTilesX, numTilesY, BufferedImage.TYPE_3BYTE_BGR);
        int numSteps = -1;
        do {
            numSteps = this.nextCandidate(image, numSteps + 1);
        } while (!Dialog.showYesNoWithImage("Part two", "Is there a christmas tree?", new ImageComponent(image)));
        return numSteps;
    }

    @Override
    public void run() {
        this.pushSecondaryInput("Num tiles X", 101);
        this.pushSecondaryInput("Num tiles Y", 103);
        this.addPresetFromResource("Example", "/2024/day14e.txt", 11, 7);
        Pattern p = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
        this.robots = this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches)
                .map(m -> new Robot(new Vec2i(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))),
                        new Vec2i(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)))))
                .toList();
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
