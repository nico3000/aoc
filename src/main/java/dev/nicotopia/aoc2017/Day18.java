package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class Day18 {
    private enum ArgType {
        REG, VAL,
    }

    private record Arg(ArgType type, long val) {
        public Arg(String src) {
            this(src.length() == 1 && 'a' <= src.charAt(0) && src.charAt(0) <= 'z' ? ArgType.REG : ArgType.VAL,
                    src.length() == 1 && 'a' <= src.charAt(0) && src.charAt(0) <= 'z' ? src.charAt(0)
                            : Long.valueOf(src));
        }

        public long get(Machine m) {
            return switch (type) {
                case VAL -> this.val;
                case REG -> m.registers.getOrDefault((char) this.val, 0L);
            };
        }
    }

    private record Op(String cmd, Arg arg0, Arg arg1) {
        public void execute(Machine m) {
            switch (this.cmd) {
                case "snd" -> m.snd((int) this.arg0.get(m));
                case "set" -> m.registers.put((char) this.arg0.val, this.arg1.get(m));
                case "add" -> m.registers.put((char) this.arg0.val, this.arg0.get(m) + this.arg1.get(m));
                case "mul" -> m.registers.put((char) this.arg0.val, this.arg0.get(m) * this.arg1.get(m));
                case "mod" -> m.registers.put((char) this.arg0.val, this.arg0.get(m) % this.arg1.get(m));
                case "rcv" -> {
                    if (this.arg0.get(m) != 0 && m.lastSndFrequency != null) {
                        System.out.println("Part one: " + m.lastSndFrequency);
                        m.lastSndFrequency = null;
                    }
                    m.registers.put((char) this.arg0.val, m.rcv());
                }
                case "jgz" -> {
                    if (0 < this.arg0.get(m)) {
                        m.jmp((int) this.arg1.get(m));
                    }
                }
                default -> throw new RuntimeException();
            }
        }
    }

    private static class Machine {
        private final Map<Character, Long> registers = new HashMap<>();
        private final List<Op> program = new LinkedList<>();
        private int ic;
        private final LongConsumer sndConsumer;
        private final LongSupplier rcvSupplier;
        private Long lastSndFrequency = 0L;

        public Machine(int id, List<String> assemblerLines, LongConsumer sndConsumer, LongSupplier rcvSupplier) {
            this.program.addAll(assemblerLines.stream().map(l -> l.split("\\s+"))
                    .map(s -> new Op(s[0], new Arg(s[1]), s.length == 2 ? null : new Arg(s[2]))).toList());
            this.sndConsumer = sndConsumer;
            this.rcvSupplier = rcvSupplier;
            this.registers.put('p', (long) id);
        }

        public void execute() {
            this.ic = 0;
            while (this.ic < this.program.size()) {
                this.program.get(this.ic++).execute(this);
            }
        }

        public void snd(long value) {
            if (this.lastSndFrequency != null) {
                this.lastSndFrequency = value;
            }
            this.sndConsumer.accept(value);
        }

        public long rcv() {
            return this.rcvSupplier.getAsLong();
        }

        public void jmp(int offset) {
            this.ic += offset - 1;
        }
    }

    public static class InterconQueue {
        private final String name;
        private final Queue<Long> queue = new LinkedList<>();
        private int throughput = 0;

        public InterconQueue(String name) {
            this.name = name;
        }

        public synchronized Long poll() {
            Long v;
            while ((v = this.queue.poll()) == null) {
                System.out.println(this.name + "\tNow waiting. In case this is the final deadlock, throughput = "
                        + this.throughput);
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return v;
        }

        public synchronized void offer(long v) {
            this.queue.offer(v);
            this.notify();
            ++this.throughput;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> assemblerLines;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day18.class.getResourceAsStream("/2017/day18.txt")))) {
            assemblerLines = br.lines().toList();
        }
        InterconQueue p0ToP1 = new InterconQueue("Queue p0 to p1");
        InterconQueue p1ToP0 = new InterconQueue("Queue p1 to p0");
        Machine p0 = new Machine(0, assemblerLines, p0ToP1::offer, p1ToP0::poll);
        Machine p1 = new Machine(1, assemblerLines, p1ToP0::offer, p0ToP1::poll);
        Thread p0t = new Thread(p0::execute);
        Thread p1t = new Thread(p1::execute);
        p0t.start();
        p1t.start();
        p0t.join();
        p1t.join();
    }
}
