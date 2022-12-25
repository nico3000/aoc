package dev.nicotopia;

public record Rational(long num, long den) {

    public static final Rational ZERO = new Rational(0);
    public static final Rational ONE = new Rational(1);

    public Rational(long num, long den) {
        if (den == 0) {
            throw new ArithmeticException("divide by zero");
        }
        long gcd = Util.gcd(Math.abs(num), Math.abs(den));
        this.num = Math.abs(num) / gcd * (num < 0 ^ den < 0 ? -1 : 1);
        this.den = Math.abs(den) / gcd;
    }

    public Rational(long number) {
        this(number, 1);
    }

    public boolean isZero() {
        return this.num == 0;
    }

    public boolean isNegative() {
        return this.num < 0;
    }

    public Rational negate() {
        return new Rational(-this.num, this.den);
    }

    public Rational invert() {
        return new Rational(this.den, this.num);
    }

    public Rational add(Rational right) {
        return new Rational(this.num * right.den + this.den * right.num, this.den * right.den);
    }

    public Rational sub(Rational right) {
        return new Rational(this.num * right.den - this.den * right.num, this.den * right.den);
    }

    public Rational mul(Rational right) {
        return new Rational(this.num * right.num, this.den * right.den);
    }

    public Rational div(Rational right) {
        return new Rational(this.num * right.den, this.den * right.num);
    }

    @Override
    public String toString() {
        return "" + this.num + (this.den == 1 ? "" : "/" + this.den);
    }
}
