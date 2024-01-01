package dev.nicotopia.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dev.nicotopia.Pair;
import dev.nicotopia.Util;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day20 extends DayBase {
    private enum PulseType {
        LOW, HIGH,
    }

    private abstract class Module {
        private final List<Module> dst = new ArrayList<>();

        protected void pushDestinationModule(Module d) {
            this.dst.add(d);
            if (d instanceof Conjunction c) {
                c.registerInput(this);
            }
        }

        protected void send(PulseType pulseType) {
            switch (pulseType) {
                case LOW -> Day20.this.numLowPulses += this.dst.size();
                case HIGH -> Day20.this.numHighPulses += this.dst.size();
            }
            this.dst.forEach(d -> Day20.this.pulseQueue.offer(new Pulse(this, pulseType, d)));
        }

        public abstract void receive(Module src, PulseType pulseType);

        public abstract void reset();

        public abstract int appendState(BitSet state, int offset);
    }

    private class Sink extends Module {

        @Override
        public void receive(Module src, PulseType pulseType) {
        }

        @Override
        public void reset() {
        }

        @Override
        public int appendState(BitSet state, int offset) {
            return offset;
        }
    }

    private class FlipFlop extends Module {
        private boolean state = false;

        @Override
        public void receive(Module src, PulseType pulseType) {
            if (pulseType == PulseType.LOW) {
                this.state = !this.state;
                this.send(this.state ? PulseType.HIGH : PulseType.LOW);
            }
        }

        @Override
        public void reset() {
            this.state = false;
        }

        @Override
        public int appendState(BitSet state, int offset) {
            state.set(offset, this.state);
            return offset + 1;
        }
    }

    private class Conjunction extends Module {
        private final Map<Module, PulseType> lastInputs = new HashMap<>();
        private BiConsumer<Module, PulseType> onChange = null;

        public void registerInput(Module module) {
            this.lastInputs.put(module, PulseType.LOW);
        }

        @Override
        public void receive(Module src, PulseType pulseType) {
            if (this.onChange != null && (pulseType != this.lastInputs.get(src))) {
                this.onChange.accept(src, pulseType);
            }
            this.lastInputs.put(src, pulseType);
            this.send(this.lastInputs.values().stream().allMatch(p -> p == PulseType.HIGH) ? PulseType.LOW
                    : PulseType.HIGH);
        }

        @Override
        public void reset() {
            this.lastInputs.keySet().forEach(k -> this.lastInputs.put(k, PulseType.LOW));
        }

        @Override
        public int appendState(BitSet state, int offset) {
            for (PulseType type : this.lastInputs.values()) {
                state.set(offset++, type == PulseType.HIGH);
            }
            return offset;
        }
    }

    private class Broadcast extends Module {
        @Override
        public void receive(Module src, PulseType pulseType) {
            this.send(pulseType);
        }

        @Override
        public void reset() {
        }

        @Override
        public int appendState(BitSet state, int offset) {
            return offset;
        }
    }

    private class Cluster {
        private final Module finalModule;
        private final List<Module> modules = new ArrayList<>();

        public Cluster(Module start, Module end) {
            Stack<Module> stack = new Stack<>();
            stack.push(start);
            Module temp = null;
            while (!stack.isEmpty()) {
                Module m = stack.pop();
                if (!this.modules.contains(m)) {
                    this.modules.add(m);
                    for (Module dst : m.dst) {
                        if (dst == end) {
                            temp = m;
                        } else {
                            stack.push(dst);
                        }
                    }
                }
            }
            this.finalModule = temp;
        }

        public BitSet queryState() {
            BitSet state = new BitSet();
            int offset = 0;
            for (Module m : this.modules) {
                offset = m.appendState(state, offset);
            }
            return state;
        }
    }

    private record Pulse(Module src, PulseType type, Module dst) {
        public void execute() {
            this.dst.receive(this.src, this.type);
        }
    }

    private Map<String, Module> modules = new HashMap<>();
    private Module broadcaster;
    private Queue<Pulse> pulseQueue = new LinkedList<>();
    private long numButtonPresses = 0;
    private int numLowPulses = 0;
    private int numHighPulses = 0;

    private void reset() {
        this.modules.values().forEach(Module::reset);
        this.numButtonPresses = 0;
    }

    private void build() {
        Map<String, Pair<Module, List<String>>> tempModules = new HashMap<>();
        Pattern p = Pattern.compile("^([^ ]([^ ]+)) -> (([a-z]+, )*[a-z]+)$");
        for (Matcher m : this.getPrimaryPuzzleInput().stream().map(p::matcher).filter(Matcher::matches).toList()) {
            Module module = m.group(1).equals("broadcaster") ? new Broadcast() : switch (m.group(1).charAt(0)) {
                case '%' -> new FlipFlop();
                case '&' -> new Conjunction();
                default -> throw new AocException("Illegal module: %s", m.group());
            };
            tempModules.put(m.group(module instanceof Broadcast ? 1 : 2),
                    new Pair<>(module, Arrays.asList(m.group(3).split(", "))));
        }
        this.modules.put("rx", new Sink());
        this.modules.putAll(
                tempModules.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().first())));
        tempModules.values().forEach(
                src -> src.second().stream().map(this.modules::get).forEach(src.first()::pushDestinationModule));
        this.broadcaster = tempModules.get("broadcaster").first();
    }

    private void pressButton() {
        ++this.numLowPulses;
        this.pulseQueue.offer(new Pulse(null, PulseType.LOW, this.broadcaster));
        while (!this.pulseQueue.isEmpty()) {
            this.pulseQueue.poll().execute();
        }
        ++this.numButtonPresses;
    }

    private long partOne() {
        for (int i = 0; i < 1000; ++i) {
            this.pressButton();
        }
        return (long) this.numLowPulses * (long) this.numHighPulses;
    }

    private record ClusterInfo(long initialButtonPresses, long loopNumButtonPresses, long highButtonPress) {
    }

    private long partTwo() {
        Module rx = this.modules.get("rx");
        List<Module> finalModules = this.modules.values().stream().filter(m -> m.dst.contains(rx)).toList();
        if (finalModules.size() != 1 || !(finalModules.getFirst() instanceof Conjunction)) {
            throw new AocException(
                    "Not supported: More than one or no modules have rx as their destination or the only module is no Conjunction.");
        }
        Conjunction finalModule = (Conjunction) finalModules.getFirst();
        List<Cluster> clusters = this.broadcaster.dst.stream().map(m -> new Cluster(m, finalModule)).toList();
        List<ClusterInfo> clusterInfos = new ArrayList<>();
        for (Cluster c : clusters) {
            this.reset();
            List<Long> lowToHigh = new ArrayList<>();
            List<Long> highToLow = new ArrayList<>();
            finalModule.onChange = (src, pulseType) -> {
                if (src == c.finalModule) {
                    (pulseType == PulseType.LOW ? highToLow : lowToHigh).add(this.numButtonPresses);
                }
            };
            Map<BitSet, Long> states = new HashMap<>();
            while (!states.containsKey(c.queryState())) {
                states.put(c.queryState(), this.numButtonPresses);
                this.pressButton();
            }
            if (lowToHigh.size() != 1 || highToLow.size() != 1) {
                throw new AocException(
                        "Not supported: More than exactly one switch from low to high and one high to low switch of the final node by this cluster.");
            }
            if (!lowToHigh.getFirst().equals(highToLow.getFirst())) {
                throw new AocException("Not supported: Low to high and high to low not during the same button press.");
            }
            clusterInfos.add(new ClusterInfo(states.get(c.queryState()), numButtonPresses - states.get(c.queryState()),
                    highToLow.getFirst()));
        }
        if (!clusterInfos.stream()
                .allMatch(ci -> ci.initialButtonPresses == clusterInfos.getFirst().initialButtonPresses)) {
            throw new AocException("Not supported: Different number of initial button presses in clusters.");
        }
        if (!clusterInfos.stream().allMatch(ci -> ci.loopNumButtonPresses - 1 == ci.highButtonPress)) {
            throw new AocException(
                    "Not supported: High button press must be the last one in the loop for every cluster");
        }
        long lcm = clusterInfos.stream().mapToLong(ci -> ci.loopNumButtonPresses).reduce(Util::lcm).getAsLong();
        return clusterInfos.getFirst().initialButtonPresses + lcm - 1;
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example", "/2023/day20.txt");
        this.addTask("Build configuration", this::build);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}