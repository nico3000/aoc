package dev.nicotopia.aoc.algebra;

public record Vec3i(int x, int y, int z) {
    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }
}
