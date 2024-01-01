package dev.nicotopia.aoc.algebra;

public record Vec3i64(long x, long y, long z) {
    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }
}
