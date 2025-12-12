package dev.nicotopia.aoc2019;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import dev.nicotopia.Compass4;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.Dialog;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day17 extends DayBase {
    private class Robot {
        private Arena arena;
        private Vec2i pos;
        private Compass4 dir;

        private Robot() {
        }

        public Robot(Arena arena) {
            this.arena = arena;
            Optional<Vec2i> optPos = Vec2i.streamFromRectangle(0, 0, this.arena.getWidth(), this.arena.getHeight())
                    .filter(p -> this.arena.get(p) != '.' && this.arena.get(p) != '#').findAny();
            if (optPos.isEmpty()) {
                throw new RuntimeException("Robot not found");
            }
            this.pos = optPos.get();
            this.dir = switch (this.arena.get(this.pos)) {
                case '^' -> Compass4.N;
                case '>' -> Compass4.E;
                case 'v' -> Compass4.S;
                case '<' -> Compass4.W;
                default -> throw new RuntimeException("Illegal char");
            };
        }

        public char getNextTile() {
            return this.arena.get(this.getForwardPos());
        }

        public Robot forward() {
            this.pos = this.getForwardPos();
            return this;
        }

        public Robot left() {
            this.dir = switch (this.dir) {
                case Compass4.N -> Compass4.W;
                case Compass4.E -> Compass4.N;
                case Compass4.S -> Compass4.E;
                case Compass4.W -> Compass4.S;
            };
            return this;
        }

        public Robot right() {
            this.dir = switch (this.dir) {
                case Compass4.N -> Compass4.E;
                case Compass4.E -> Compass4.S;
                case Compass4.S -> Compass4.W;
                case Compass4.W -> Compass4.N;
            };
            return this;
        }

        public boolean isDone() {
            return this.getNextTile() == '.' && this.clone().left().getNextTile() == '.'
                    && this.clone().right().getNextTile() == '.';
        }

        public String autoMove() {
            String cmdStr = "";
            int numSteps = 0;
            String command = null;
            while (!this.isDone()) {
                if (this.getNextTile() == '#') {
                    if (16 <= ++numSteps) {
                        throw new AocException("Number of steps in a single line must be less than 16.");
                    }
                    this.forward();
                } else {
                    if (command != null) {
                        cmdStr += String.format("%s%x", command, numSteps);
                        numSteps = 0;
                    }
                    this.left();
                    if (this.getNextTile() == '#') {
                        command = "L";
                    } else {
                        this.right().right();
                        command = "R";
                    }
                }
            }
            if (command != null) {
                cmdStr += String.format("%s%x", command, numSteps);
            }
            return cmdStr;
        }

        @Override
        public String toString() {
            return this.pos + ", " + this.dir;
        }

        @Override
        public Robot clone() {
            Robot robot = new Robot();
            robot.arena = this.arena;
            robot.pos = this.pos;
            robot.dir = this.dir;
            return robot;
        }

        private Vec2i getForwardPos() {
            return switch (this.dir) {
                case Compass4.N -> new Vec2i(this.pos.x(), this.pos.y() - 1);
                case Compass4.E -> new Vec2i(this.pos.x() + 1, this.pos.y());
                case Compass4.S -> new Vec2i(this.pos.x(), this.pos.y() + 1);
                case Compass4.W -> new Vec2i(this.pos.x() - 1, this.pos.y());
            };
        }
    }

    private class Arena {
        private final char[][] arena;

        public Arena(List<Long> machineOutput) {
            this.arena = Arrays.stream(
                    machineOutput.stream().map(v -> "" + (char) v.intValue()).collect(Collectors.joining()).split("\n"))
                    .map(l -> l.toCharArray()).toArray(char[][]::new);
        }

        public int getHeight() {
            return this.arena.length;
        }

        public int getWidth() {
            return this.arena.length == 0 ? 0 : this.arena[0].length;
        }

        public char get(Vec2i p) {
            return 0 <= p.x() && p.x() < this.getWidth() && 0 <= p.y() && p.y() < this.getHeight()
                    ? this.arena[p.y()][p.x()]
                    : '.';
        }

        public boolean isIntersection(int x, int y) {
            return this.arena[y][x] == '#' && x != 0 && this.arena[y][x - 1] == '#' && x != this.getWidth() - 1
                    && this.arena[y][x + 1] == '#' && y != 0 && this.arena[y - 1][x] == '#'
                    && y != this.getHeight() - 1 && this.arena[y + 1][x] == '#';
        }

        @Override
        public String toString() {
            return Arrays.stream(this.arena).map(String::valueOf).collect(Collectors.joining("\n"));
        }
    }

    private int partOne(Arena arena) {
        return Vec2i.streamFromRectangle(0, 0, arena.getWidth(), arena.getHeight())
                .filter(c -> arena.isIntersection(c.x(), c.y())).mapToInt(c -> c.x() * c.y()).sum();
    }

    private long partTwo(Arena arena, IntcodeMachine machine, boolean debug) {
        Robot robot = new Robot(arena);
        List<int[]> mvtFns = new LinkedList<>();
        List<String> mainRoutine = new LinkedList<>();
        mainRoutine.add(robot.autoMove());
        if (!this.check(mainRoutine, mvtFns)) {
            throw new AocException("Algorithm did not work :(");
        }
        // System.out.println(mainRoutine.stream().collect(Collectors.joining(",")));
        // mvtFns.forEach(fn -> Arrays.stream(fn).forEach(c -> System.out.print(Character.toString(c))));
        if (debug) {
            this.outputString = new StringBuilder();
        }
        Queue<Integer> input = new LinkedList<>(IntStream.range(0, 5).flatMap(i -> IntStream.of(switch (i) {
            case 0 -> this.toMachineInput(mainRoutine.stream().collect(Collectors.joining()));
            case 4 -> new int[] { debug ? 'y' : 'n', '\n' };
            default -> mvtFns.get(i - 1);
        })).boxed().toList());
        machine.set(0, 2);
        machine.execute(input::poll, this::onOutput);
        return this.finalOutput;
    }

    private StringBuilder outputString = null;
    private long finalOutput;

    private void onOutput(long v) {
        if (this.outputString != null && v == '\n' && this.finalOutput == '\n') {
            String text = this.outputString.toString();
            this.outputString.setLength(0);
            if (Dialog.showInfo("Debug output", text, DayBase.MONOSPACED_FONT, "Next", "Don't show again") == 1) {
                this.outputString = null;
            }
        }
        if (this.outputString != null) {
            this.outputString.append((char) v);
        }
        this.finalOutput = v;
    }

    private boolean check(List<String> mainRoutine, List<int[]> mvtFns) {
        List<String> remCmdList = mainRoutine.stream().filter(s -> s.length() != 1).toList();
        if (mvtFns.size() == 3) {
            return remCmdList.isEmpty();
        } else if (remCmdList.isEmpty()) {
            return mvtFns.size() <= 3;
        }
        for (String cmd : remCmdList) {
            for (int l = cmd.length(); l != 0; l -= 2) {
                final int MAX_LEN = 20;
                String mvtFn = cmd.substring(0, l);
                int mvtFnCode[] = this.toMachineInput(mvtFn);
                if (MAX_LEN < mvtFnCode.length - 1) {
                    continue;
                }
                String mvtFnLabel = Character.toString('A' + mvtFns.size());
                mvtFns.addLast(mvtFnCode);
                List<String> newMainRoutine = this.removeSubCmd(mainRoutine, mvtFn, mvtFnLabel);
                if (this.check(newMainRoutine, mvtFns)) {
                    mainRoutine.clear();
                    mainRoutine.addAll(newMainRoutine);
                    return true;
                }
                mvtFns.removeLast();
            }
        }
        return false;
    }

    private List<String> removeSubCmd(List<String> mainRoutine, String mvtFn, String mvtFnLabel) {
        List<String> newMainRoutine = new LinkedList<>();
        for (String cmd : mainRoutine) {
            while (!cmd.isEmpty()) {
                int idx = cmd.indexOf(mvtFn);
                if (idx == -1) {
                    newMainRoutine.add(cmd);
                    break;
                }
                if (idx != 0) {
                    newMainRoutine.add(cmd.substring(0, idx));
                }
                newMainRoutine.add(mvtFnLabel);
                cmd = cmd.substring(idx + mvtFn.length());
            }
        }
        return newMainRoutine;
    }

    private int[] toMachineInput(String s) {
        return IntStream.range(0, s.length()).flatMap(i -> {
            char sep = i == s.length() - 1 ? '\n' : ',';
            int v = s.charAt(i);
            return 'a' <= v && v <= 'f' ? IntStream.of('1', '0' + v - 'a', sep) : IntStream.of(v, sep);
        }).toArray();
    }

    @Override
    public void run() {
        IntcodeMachine machine = new IntcodeMachine(this.getPrimaryPuzzleInput().getFirst());
        Arena arena = new Arena(machine.clone().execute());
        this.addTask("Part one", () -> this.partOne(arena));
        boolean debug = Dialog.showYesNoQuestion("Debug mode", "Enable debug mode for part 2 and show steps?");
        this.addTask("Part two", () -> this.partTwo(arena, machine.clone(), debug));
    }
}