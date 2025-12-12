package dev.nicotopia.aoc2019;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.ImageComponent;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day11 extends DayBase {
    private final Set<Vec2i> whitePanels = new HashSet<>();
    private final Set<Vec2i> paintedPanels = new HashSet<>();
    private Vec2i robotPos;
    private Compass4 robotDir;
    private int numProcessedValues;

    private void process(long v) {
        if (this.numProcessedValues++ % 2 == 0) {
            switch ((int) v) {
                case 0 -> this.whitePanels.remove(this.robotPos);
                case 1 -> this.whitePanels.add(this.robotPos);
                default -> throw new AocException("Unexpected value: " + v);
            }
            this.paintedPanels.add(this.robotPos);
        } else {
            this.robotDir = switch ((int) v) {
                case 0 -> switch (this.robotDir) {
                    case N -> Compass4.W;
                    case E -> Compass4.N;
                    case S -> Compass4.E;
                    case W -> Compass4.S;
                };
                case 1 -> switch (this.robotDir) {
                    case N -> Compass4.E;
                    case E -> Compass4.S;
                    case S -> Compass4.W;
                    case W -> Compass4.N;
                };
                default -> throw new AocException("Unexpected value: " + v);
            };
            this.robotPos = this.robotPos.add(switch (this.robotDir) {
                case N -> new Vec2i(0, -1);
                case E -> new Vec2i(1, 0);
                case S -> new Vec2i(0, 1);
                case W -> new Vec2i(-1, 0);
            });
        }
    }

    private int runRobot(boolean startOnWhitePanel) {
        this.whitePanels.clear();
        this.paintedPanels.clear();
        this.robotPos = Vec2i.ORIGIN;
        this.robotDir = Compass4.N;
        this.numProcessedValues = 0;
        if (startOnWhitePanel) {
            this.whitePanels.add(this.robotPos);
        }
        IntcodeMachine robot = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
        robot.execute(() -> this.whitePanels.contains(this.robotPos) ? 1 : 0, this::process);
        return this.paintedPanels.size();
    }

    private void partTwo() {
        this.runRobot(true);
        this.pushPostResultsOption("Show painted hull", () -> Dialog.showImage("The gorgeous hull <3",
                new ImageComponent(ImageComponent.imageFrom(this.whitePanels, Color.WHITE, Color.BLACK))));
    }

    @Override
    public void run() {
        this.addTask("Part one", () -> this.runRobot(false));
        this.addTask("Part two", this::partTwo);
    }
}