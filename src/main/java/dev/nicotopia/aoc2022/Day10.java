package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

public class Day10 {
    public static class Crt {
        private final int width;
        private int ticks;
        private int reg;
        private final Consumer<Crt> tickCallback;

        public Crt(int width, Consumer<Crt> tickCallback) {
            this.width = width;
            this.tickCallback = tickCallback;
        }

        public void execute(List<Addx> ops) {
            this.ticks = 0;
            this.reg = 1;
            for (Addx addx : ops) {
                do {
                    System.out.print(Math.abs(this.ticks % this.width - this.reg) <= 1 ? '#' : '.');
                    ++this.ticks;
                    this.tickCallback.accept(this);
                    if (this.ticks % this.width == 0) {
                        System.out.println();
                    }
                } while (addx != null && !addx.tick(this));
            }
        }
    }

    public static class Addx {
        private final int value;
        private int ticks = 0;

        public Addx(int value) {
            this.value = value;
        }

        public boolean tick(Crt crt) {
            if (++this.ticks == 2) {
                crt.reg += this.value;
                this.ticks = 0;
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Addx> ops;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day10.class.getResourceAsStream("/2022/day10.txt")))) {
            ops = br.lines().map(l -> l.split("\\s+"))
                    .map(s -> s[0].equals("addx") ? new Addx(Integer.valueOf(s[1])) : null).toList();
        }
        LongAdder adder = new LongAdder();
        Crt crt = new Crt(40, _crt -> {
            if ((_crt.ticks - 20) % 40 == 0) {
                adder.add(_crt.reg * _crt.ticks);
            }
        });
        crt.execute(ops);
        System.out.println("Part one: " + adder.sum());
    }
}