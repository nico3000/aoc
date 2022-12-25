package dev.nicotopia;

import java.util.Arrays;
import java.util.stream.IntStream;

public class RationalPolynomial {
    private final Rational[] coefficients;

    public RationalPolynomial(Rational... coefficients) {
        if (coefficients.length == 0) {
            this.coefficients = new Rational[] { Rational.ZERO };
        } else {
            int len = coefficients.length;
            while (len != 1 && coefficients[len - 1].isZero()) {
                --len;
            }
            this.coefficients = Arrays.copyOf(coefficients, len);
        }
    }

    public int getDegree() {
        return this.coefficients.length - 1;
    }

    public Rational getCoefficient(int i) {
        return i < this.coefficients.length ? this.coefficients[i] : Rational.ZERO;
    }

    public RationalPolynomial add(RationalPolynomial right) {
        return new RationalPolynomial(IntStream.rangeClosed(0, Math.max(this.getDegree(), right.getDegree()))
                .mapToObj(i -> this.getCoefficient(i).add(right.getCoefficient(i))).toArray(Rational[]::new));
    }

    public RationalPolynomial sub(RationalPolynomial right) {
        return new RationalPolynomial(IntStream.rangeClosed(0, Math.max(this.getDegree(), right.getDegree()))
                .mapToObj(i -> this.getCoefficient(i).sub(right.getCoefficient(i))).toArray(Rational[]::new));
    }

    public RationalPolynomial mul(RationalPolynomial right) {
        Rational c[] = IntStream.rangeClosed(0, this.getDegree() + right.getDegree()).mapToObj(i -> Rational.ZERO)
                .toArray(Rational[]::new);
        for (int i = 0; i <= this.getDegree(); ++i) {
            for (int j = 0; j <= right.getDegree(); ++j) {
                c[i + j] = c[i + j].add(this.getCoefficient(i).mul(right.getCoefficient(j)));
            }
        }
        return new RationalPolynomial(c);
    }

    public RationalPolynomial div(RationalPolynomial right) {
        if (right.getDegree() != 0) {
            throw new ArithmeticException("Divisor must have degree zero");
        }
        return this.mul(new RationalPolynomial(right.getCoefficient(0).invert()));
    }

    public Rational getEqualsZeroResult() {
        if (this.getDegree() == 1) {
            return this.getCoefficient(0).negate().div(this.getCoefficient(1));
        }
        throw new UnsupportedOperationException("Polynom must have degree 1");
    }

    public Rational eval(Rational x) {
        Rational sum = this.getCoefficient(0);
        Rational xn = Rational.ONE;
        for (int i = 1; i <= this.getDegree(); ++i) {
            xn = xn.mul(x);
            sum = sum.add(this.getCoefficient(i).mul(xn));
        }
        return sum;
    }

    public String toString(String variable) {
        return String.join(" + ",
                IntStream.rangeClosed(0, this.getDegree()).map(i -> this.getDegree() - i)
                        .filter(i -> !this.getCoefficient(i).isZero())
                        .mapToObj(i -> this.getCoefficient(i) + (0 < i ? variable + (1 < i ? ("^" + i) : "") : ""))
                        .toList());
    }

    @Override
    public String toString() {
        return this.toString("x");
    }
}