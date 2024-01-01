package dev.nicotopia.aoc.algebra;

public record Vec2d(double x, double y) {
    public Vec2d add(Vec2d other) {
        return new Vec2d(this.x + other.x, this.y + other.y);
    }

    public Vec2d mul(double v) {
        return new Vec2d(this.x * v, this.y * v);
    }

    @Override
    public String toString() {
        return String.format("%.3f, %.3f", this.x, this.y);
    }
}
