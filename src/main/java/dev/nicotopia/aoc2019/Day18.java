package dev.nicotopia.aoc2019;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.IntStream;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.Dijkstra;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day18 extends DayBase {
    private class IntRef {
        public int value;

        public IntRef(int value) {
            this.value = value;
        }
    }

    private class Vault {
        private final char[][] layout;
        private final Vec2i entrance;
        private final Map<Vec2i, Character> keys = new HashMap<>();
        private final Map<Vec2i, Character> doors = new HashMap<>();
        private final Map<State, Map<Character, Long>> shortestDistancesPerState = new HashMap<>();

        public Vault(char[][] layout) {
            this.layout = new char[layout.length][];
            Vec2i entrance = null;
            for (int y = 0; y < layout.length; ++y) {
                this.layout[y] = new char[layout[y].length];
                for (int x = 0; x < layout[y].length; ++x) {
                    switch (layout[y][x]) {
                        case '#':
                        case '.':
                            this.layout[y][x] = layout[y][x];
                            break;
                        case '@':
                            this.layout[y][x] = '.';
                            entrance = new Vec2i(x, y);
                            break;
                        default:
                            this.layout[y][x] = '.';
                            if (Character.isLowerCase(layout[y][x])) {
                                this.keys.put(new Vec2i(x, y), layout[y][x]);
                            } else {
                                this.doors.put(new Vec2i(x, y), layout[y][x]);
                            }
                            break;
                    }
                }
            }
            this.entrance = entrance;
        }

        public Vec2i getEntrancePos() {
            return this.entrance;
        }

        public boolean isPassable(Vec2i pos, State state) {
            Character door = this.doors.get(pos);
            return this.layout[pos.y()][pos.x()] == '.'
                    && (door == null || state.keys.contains(Character.toLowerCase(door)));
        }

        private Map<Character, Long> getShortestPathToKeys(State state) {
            Map<Character, Long> result = this.shortestDistancesPerState.get(state);
            if (result != null) {
                return result;
            }
            long distances[][] = IntStream.range(0, this.layout.length).mapToObj(i -> new long[this.layout[i].length])
                    .toArray(long[][]::new);
            Dijkstra.run(p -> distances[p.y()][p.x()], (p, d) -> distances[p.y()][p.x()] = d,
                    () -> Arrays.stream(distances).forEach(r -> Arrays.fill(r, Integer.MAX_VALUE)), (p, i) -> {
                        if (this.isPassable(new Vec2i(p.x(), p.y() - 1), state) && i-- == 0) {
                            return new NodeDistancePair<>(new Vec2i(p.x(), p.y() - 1), 1);
                        } else if (this.isPassable(new Vec2i(p.x() + 1, p.y()), state) && i-- == 0) {
                            return new NodeDistancePair<>(new Vec2i(p.x() + 1, p.y()), 1);
                        } else if (this.isPassable(new Vec2i(p.x(), p.y() + 1), state) && i-- == 0) {
                            return new NodeDistancePair<>(new Vec2i(p.x(), p.y() + 1), 1);
                        } else if (this.isPassable(new Vec2i(p.x() - 1, p.y()), state) && i-- == 0) {
                            return new NodeDistancePair<>(new Vec2i(p.x() - 1, p.y()), 1);
                        }
                        return null;
                    }, state.pos);
            this.shortestDistancesPerState.put(state, result = new HashMap<>());
            for (Vec2i keyPos : this.keys.keySet()) {
                if (!state.keys.contains(this.keys.get(keyPos))
                        && distances[keyPos.y()][keyPos.x()] != Integer.MAX_VALUE) {
                    result.put(this.keys.get(keyPos), distances[keyPos.y()][keyPos.x()]);
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < this.layout.length; ++y) {
                for (int x = 0; x < this.layout[y].length; ++x) {
                    Character c = x == this.entrance.x() && y == this.entrance.y() ? '@' : null;
                    c = c != null ? c : this.keys.get(new Vec2i(x, y));
                    c = c != null ? c : this.doors.get(new Vec2i(x, y));
                    sb.append(c == null ? this.layout[y][x] : c);
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    private class State {
        private final Vault vault;
        private final Vec2i pos;
        private final Set<Character> keys = new HashSet<>();
        private final Map<Character, Long> distances;

        public State(Vault vault) {
            this.vault = vault;
            this.pos = this.vault.getEntrancePos();
            this.distances = this.vault.getShortestPathToKeys(this);
        }

        public State(State prevState, char newKey) {
            if (prevState.isKeyCollected(newKey)) {
                throw new AocException("Key %c collected twice.", newKey);
            }
            this.vault = prevState.vault;
            this.pos = this.vault.keys.entrySet().stream().filter(e -> e.getValue() == newKey).findAny()
                    .map(Entry::getKey).orElse(prevState.pos);
            this.keys.addAll(prevState.keys);
            this.keys.add(newKey);
            this.distances = this.vault.getShortestPathToKeys(this);
        }

        public boolean isKeyCollected(char key) {
            return this.keys.contains(key);
        }

        public OptionalLong getDistanceTo(char key) {
            Long d = this.distances.get(key);
            return d == null ? OptionalLong.empty() : OptionalLong.of(d);
        }

        public boolean isFinal() {
            return this.keys.containsAll(this.vault.keys.values());
        }

        public long estimate() {
            return this.distances.values().stream().mapToLong(Long::valueOf).max().orElse(0);
        }

        public NodeDistancePair<State> getNeighbour(int index) {
            return this.getNeighbour(new IntRef(index));
        }

        public NodeDistancePair<State> getNeighbour(IntRef index) {
            for (char key : this.vault.keys.values()) {
                OptionalLong d = this.getDistanceTo(key);
                if (d.isPresent() && index.value-- == 0) {
                    return new NodeDistancePair<>(new State(this, key), d.getAsLong());
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            return this.pos.hashCode() ^ this.keys.hashCode();
        }

        @Override
        public boolean equals(Object otherObj) {
            return otherObj instanceof State other && this.pos.equals(other.pos) && this.keys.equals(other.keys);
        }
    }

    private long partOne(Vault vault) {
        return AStar.run(State::getNeighbour, new State(vault),
                new HashedAStarDataStructure<>(State::estimate, State::isFinal)).distance();
    }

    private class StateP2 {
        private final State[] states = new State[4];

        private StateP2() {
        }

        public StateP2(char[][] originalVaultLayout) {
            int entranceY = IntStream.range(0, originalVaultLayout.length)
                    .filter(y -> String.valueOf(originalVaultLayout[y]).indexOf('@') != -1).findAny().getAsInt();
            int entranceX = String.valueOf(originalVaultLayout[entranceY]).indexOf('@');
            char layout0[][] = new char[entranceY + 1][];
            char layout1[][] = new char[entranceY + 1][];
            for (int y = 0; y < layout0.length; ++y) {
                layout0[y] = Arrays.copyOfRange(originalVaultLayout[y], 0, entranceX + 1);
                layout1[y] = Arrays.copyOfRange(originalVaultLayout[y], entranceX, originalVaultLayout[y].length);
            }
            layout0[layout0.length - 2][layout0[layout0.length - 2].length - 1] = '#';
            layout0[layout0.length - 1][layout0[layout0.length - 1].length - 2] = '#';
            layout0[layout0.length - 1][layout0[layout0.length - 1].length - 1] = '#';
            layout0[layout0.length - 2][layout0[layout0.length - 2].length - 2] = '@';
            layout1[layout1.length - 2][0] = '#';
            layout1[layout1.length - 1][1] = '#';
            layout1[layout1.length - 1][0] = '#';
            layout1[layout1.length - 2][1] = '@';
            this.states[0] = new State(new Vault(layout0));
            this.states[1] = new State(new Vault(layout1));
            char layout2[][] = new char[originalVaultLayout.length - entranceY][];
            char layout3[][] = new char[originalVaultLayout.length - entranceY][];
            for (int y = 0; y < layout2.length; ++y) {
                layout2[y] = Arrays.copyOfRange(originalVaultLayout[y + entranceY], 0, entranceX + 1);
                layout3[y] = Arrays.copyOfRange(originalVaultLayout[y + entranceY], entranceX,
                        originalVaultLayout[y].length);
            }
            layout2[1][layout2[1].length - 1] = '#';
            layout2[0][layout2[0].length - 2] = '#';
            layout2[0][layout2[0].length - 1] = '#';
            layout2[1][layout2[1].length - 2] = '@';
            layout3[1][0] = '#';
            layout3[0][1] = '#';
            layout3[0][0] = '#';
            layout3[1][1] = '@';
            this.states[2] = new State(new Vault(layout2));
            this.states[3] = new State(new Vault(layout3));
        }

        public boolean isFinal() {
            return this.states[0].isFinal() && this.states[1].isFinal() && this.states[2].isFinal()
                    && this.states[3].isFinal();
        }

        public long estimate() {
            long sum = 0;
            for (int i = 0; i < 4; ++i) {
                sum += this.states[i].estimate();
            }
            return sum;
        }

        public NodeDistancePair<StateP2> getNeighbour(int index) {
            Set<Character> currentKeys = new HashSet<>();
            for (int i = 0; i < 4; ++i) {
                currentKeys.addAll(this.states[i].keys);
            }
            IntRef indexRef = new IntRef(index);
            for (int i = 0; i < 4; ++i) {
                NodeDistancePair<State> s = this.states[i].getNeighbour(indexRef);
                if (s != null) {
                    char newKey = s.node().keys.stream().filter(k -> !currentKeys.contains(k)).findAny().get();
                    StateP2 newState = new StateP2();
                    newState.states[i] = s.node();
                    for (int j = 0; j < 4; ++j) {
                        if (i != j) {
                            newState.states[j] = new State(this.states[j], newKey);
                        }
                    }
                    return new NodeDistancePair<>(newState, s.distance());
                }
            }
            return null;
        }

        @Override
        public int hashCode() {
            return this.states[0].hashCode() ^ this.states[1].hashCode() ^ this.states[2].hashCode()
                    ^ this.states[3].hashCode();
        }

        @Override
        public boolean equals(Object otherObj) {
            return otherObj instanceof StateP2 other && Arrays.equals(this.states, other.states);
        }
    }

    private long partTwo(char[][] vaultLayout) {
        return AStar.run(StateP2::getNeighbour, new StateP2(vaultLayout),
                new HashedAStarDataStructure<>(StateP2::estimate, StateP2::isFinal)).distance();
    }

    @Override
    public void run() {
        char[][] layout = this.getPrimaryPuzzleInputAs2DCharArray();
        this.addTask("Part one", () -> this.partOne(new Vault(layout)));
        this.addTask("Part two", () -> this.partTwo(layout));
    }
}
