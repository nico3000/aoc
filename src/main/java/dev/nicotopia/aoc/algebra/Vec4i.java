package dev.nicotopia.aoc.algebra;

public record Vec4i(int x, int y, int z, int w) {
    public int manhattanDistance(Vec4i to) {
        return Math.abs(to.x - this.x) + Math.abs(to.y - this.y) + Math.abs(to.z - this.z) + Math.abs(to.w - this.w);
    }
}
