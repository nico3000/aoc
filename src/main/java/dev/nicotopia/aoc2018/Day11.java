package dev.nicotopia.aoc2018;

import dev.nicotopia.Vec2i;
import dev.nicotopia.aoc.DayBase;

public class Day11 extends DayBase {
    private static final int GRID_SIZE = 300;

    private record Window(Vec2i base, int size, int totalPower) {
    }

    private final int grid[][] = new int[GRID_SIZE][GRID_SIZE];
    private final int buffer[][] = new int[GRID_SIZE][GRID_SIZE];
    private int bufferWindowWidth = 0;
    private Window maxTotalPowerWindow = null;

    private void fillGrid() {
        int gridSerialNumber = Integer.valueOf(this.getPrimaryPuzzleInput().getFirst());
        for (int y = 0; y < GRID_SIZE; ++y) {
            for (int x = 0; x < GRID_SIZE; ++x) {
                int rackId = x + 11;
                this.grid[y][x] = (((rackId * (y + 1) + gridSerialNumber) * rackId) / 100) % 10 - 5;
            }
        }
    }

    private void fillNextBuffer() {
        for (int y = 0; y < GRID_SIZE - this.bufferWindowWidth; ++y) {
            for (int x = 0; x < GRID_SIZE - this.bufferWindowWidth; ++x) {
                for (int i = 0; i < this.bufferWindowWidth; ++i) {
                    this.buffer[y][x] += this.grid[y + this.bufferWindowWidth][x + i];
                    this.buffer[y][x] += this.grid[y + i][x + this.bufferWindowWidth];
                }
                this.buffer[y][x] += this.grid[y + this.bufferWindowWidth][x + this.bufferWindowWidth];
                if (this.maxTotalPowerWindow == null || this.maxTotalPowerWindow.totalPower < this.buffer[y][x]) {
                    this.maxTotalPowerWindow = new Window(new Vec2i(x, y), this.bufferWindowWidth + 1,
                            this.buffer[y][x]);
                }
            }
        }
        ++this.bufferWindowWidth;
    }

    private String partOne() {
        this.fillNextBuffer();
        this.fillNextBuffer();
        Window temp = this.maxTotalPowerWindow;
        this.maxTotalPowerWindow = null;
        this.fillNextBuffer();
        String result = String.format("%d,%d", this.maxTotalPowerWindow.base.x() + 1,
                this.maxTotalPowerWindow.base.y() + 1);
        if (this.maxTotalPowerWindow.totalPower < temp.totalPower) {
            this.maxTotalPowerWindow = temp;
        }
        return result;
    }

    private String partTwo() {
        while (this.bufferWindowWidth <= GRID_SIZE) {
            this.fillNextBuffer();
        }
        return String.format("%d,%d,%d", this.maxTotalPowerWindow.base.x() + 1, this.maxTotalPowerWindow.base.y() + 1,
                this.maxTotalPowerWindow.size);
    }

    @Override
    public void run() {
        this.addPreset("Example 1", 18);
        this.addPreset("Example 2", 42);
        this.addTask("Fill grid", this::fillGrid);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}