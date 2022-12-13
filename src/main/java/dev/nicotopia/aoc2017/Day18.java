package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Day18 {
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
