package dev.nicotopia.aoc.algebra;

public record Vec3i(int x, int y, int z) {

    public static final Vec3i ORIGIN = new Vec3i(0, 0, 0);

    public int manhattanDistance(Vec3i to) {
        return Math.abs(to.x - this.x) + Math.abs(to.y - this.y) + Math.abs(to.z - this.z);
    }

    public Vec3i add(Vec3i v) {
        return new Vec3i(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    @Override
    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }
}
