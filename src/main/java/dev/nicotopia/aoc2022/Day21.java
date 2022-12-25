package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import dev.nicotopia.Rational;
import dev.nicotopia.RationalPolynomial;

public class Day21 {
    private interface Operation {
        RationalPolynomial eval(RationalPolynomial a, RationalPolynomial b);
    }

    public static class Monkey {
        private String symbol;
        private Rational number;
        private Monkey a;
        private Operation operation;
        private Monkey b;

        private void set(String symbol, Rational number, Monkey a, Operation operation, Monkey b) {
            this.symbol = symbol;
            this.number = number;
            this.a = a;
            this.operation = operation;
            this.b = b;
        }

        public void setOperation(Monkey a, Operation operation, Monkey b) {
            this.set(null, null, a, operation, b);
        }

        public void setSymbol(String symbol) {
            this.set(symbol, null, null, null, null);
        }

        public void setNumber(Rational number) {
            this.set(null, number, null, null, null);
        }

        public RationalPolynomial evaluate(String variable) {
            if (this.symbol != null) {
                if (!this.symbol.equals(variable)) {
                    throw new RuntimeException("Evaluation only works with a single variable " + variable);
                }
                return new RationalPolynomial(Rational.ZERO, Rational.ONE);
            } else if (this.number != null) {
                return new RationalPolynomial(this.number);
            }
            RationalPolynomial polyA = this.a.evaluate(variable);
            RationalPolynomial polyB = this.b.evaluate(variable);
            return this.operation.eval(polyA, polyB);
        }
    }

    public static void main(String[] args) throws IOException {
        Map<String, Monkey> monkeys = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day21.class.getResourceAsStream("/2022/day21.txt")))) {

            Map<String, String> ops = new HashMap<>();
            br.lines().map(l -> l.split(": ")).forEach(s -> {
                monkeys.put(s[0], new Monkey());
                ops.put(s[0], s[1]);
            });
            monkeys.forEach((name, monkey) -> {
                String split[] = ops.get(name).split(" ");
                if (split.length == 1) {
                    monkey.setNumber(new Rational(Integer.valueOf(split[0])));
                } else {
                    monkey.setOperation(monkeys.get(split[0]), switch (split[1]) {
                        case "+" -> RationalPolynomial::add;
                        case "-" -> RationalPolynomial::sub;
                        case "*" -> RationalPolynomial::mul;
                        case "/" -> RationalPolynomial::div;
                        default -> throw new RuntimeException();
                    }, monkeys.get(split[2]));
                }
            });
        }
        Monkey root = monkeys.get("root");
        Monkey humn = monkeys.get("humn");
        System.out.println("Part one: " + root.evaluate(null).eval(null));
        humn.setSymbol("x");
        root.setOperation(root.a, (a, b) -> {
            System.out.println("Part two: " + a.sub(b).getEqualsZeroResult());
            return null;
        }, root.b);
        root.evaluate("x");
    }
}