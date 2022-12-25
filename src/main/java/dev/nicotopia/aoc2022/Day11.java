package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Day11 {
    private enum OpType {
        MUL, ADD, SQU,
    }

    private record Operation(OpType type, int val) {
        public void execute(Item item) {
            switch (this.type) {
                case MUL -> item.multiply(this.val);
                case ADD -> item.add(this.val);
                case SQU -> item.square();
            }
        }
    }

    private interface Item {
        public void multiply(int value);

        public void add(int value);

        public void square();

        public void divide(int val);

        public int mod(int prime);
    }

    public static class SimpleItem implements Item {
        private int val;

        public SimpleItem(int val) {
            this.val = val;
        }

        @Override
        public void multiply(int value) {
            this.val *= value;
        }

        @Override
        public void add(int value) {
            this.val += value;
        }

        @Override
        public void square() {
            this.val *= val;
        }

        @Override
        public void divide(int val) {
            this.val /= val;
        }

        @Override
        public int mod(int prime) {
            return this.val % prime;
        }
    }

    public static class ModItem implements Item {
        private final int originalValue;
        private final Map<Integer, Integer> remainders = new HashMap<>();

        public ModItem(int originalValue) {
            this.originalValue = originalValue;
        }

        public void addPrime(int prime) {
            this.remainders.put(prime, this.originalValue % prime);
        }

        @Override
        public void multiply(int value) {
            for (int prime : this.remainders.keySet()) {
                this.remainders.put(prime, (this.remainders.get(prime) * value) % prime);
            }
        }

        @Override
        public void add(int value) {
            for (int prime : this.remainders.keySet()) {
                this.remainders.put(prime, (this.remainders.get(prime) + value) % prime);
            }
        }

        @Override
        public void square() {
            for (int prime : this.remainders.keySet()) {
                this.remainders.put(prime, (this.remainders.get(prime) * this.remainders.get(prime)) % prime);
            }
        }

        @Override
        public void divide(int val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int mod(int prime) {
            return this.remainders.get(prime);
        }
    }

    private static class Monkey<E extends Item> {
        private final List<Item> items = new LinkedList<>();
        private final Operation operation;
        private final int divisor;
        private final int trueTarget;
        private final int falseTarget;
        private int count = 0;

        public Monkey(List<E> startingItems, Operation operation, int divisor, int trueTarget,
                int falseTarget) {
            this.items.addAll(startingItems);
            this.operation = operation;
            this.divisor = divisor;
            this.trueTarget = trueTarget;
            this.falseTarget = falseTarget;
        }

        public void turn(List<Monkey<E>> monkeys, boolean divide) {
            for (Item item : this.items) {
                this.operation.execute(item);
                if (divide) {
                    item.divide(3);
                }
                monkeys.get(item.mod(this.divisor) == 0 ? this.trueTarget : this.falseTarget).items.add(item);
            }
            this.count += this.items.size();
            this.items.clear();
        }
    }

    public static void main(String[] args) throws IOException {
        List<Monkey<SimpleItem>> monkeysPartOne = new ArrayList<>();
        List<Monkey<ModItem>> monkeysPartTwo = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day11.class.getResourceAsStream("/2022/day11.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Monkey")) {
                    List<Integer> startingItems = Arrays.stream(br.readLine().substring(18).split(", "))
                            .map(Integer::valueOf).toList();
                    String opLn = br.readLine();
                    String opVal = opLn.substring(25);
                    Operation op = opVal.equals("old") ? new Operation(OpType.SQU, 0)
                            : new Operation(opLn.charAt(23) == '+' ? OpType.ADD : OpType.MUL, Integer.valueOf(opVal));
                    int divisor = Integer.valueOf(br.readLine().substring(21));
                    int trueTarget = Integer.valueOf(br.readLine().substring(29));
                    int falseTarget = Integer.valueOf(br.readLine().substring(30));
                    monkeysPartOne.add(new Monkey<>(startingItems.stream().map(SimpleItem::new).toList(), op, divisor,
                            trueTarget, falseTarget));
                    monkeysPartTwo.add(new Monkey<>(startingItems.stream().map(ModItem::new).toList(), op, divisor,
                            trueTarget, falseTarget));
                }
            }
        }
        monkeysPartTwo.stream().mapMulti((m, c) -> m.items.forEach(c)).map(ModItem.class::cast)
                .forEach(item -> monkeysPartTwo.stream().mapToInt(m -> m.divisor).forEach(item::addPrime));
        for (int i = 0; i < 20; ++i) {
            monkeysPartOne.forEach(m -> m.turn(monkeysPartOne, true));
        }
        long counts[] = monkeysPartOne.stream().mapToLong(m -> m.count).sorted().toArray();
        System.out.println("Part one: " + counts[counts.length - 1] * counts[counts.length - 2]);
        for (int i = 0; i < 10000; ++i) {
            monkeysPartTwo.forEach(m -> m.turn(monkeysPartTwo, false));
        }
        counts = monkeysPartTwo.stream().mapToLong(m -> m.count).sorted().toArray();
        System.out.println("Part two: " + counts[counts.length - 1] * counts[counts.length - 2]);
    }
}