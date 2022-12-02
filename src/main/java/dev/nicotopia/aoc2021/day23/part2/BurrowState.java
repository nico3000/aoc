package dev.nicotopia.aoc2021.day23.part2;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * ##################################
 * #16 17 .. 18 .. 19 .. 20 .. 21 22#
 * ###### 12 ## 13 ## 14 ## 15 ######
 *      # 08 ## 09 ## 10 ## 11 #
 *      # 04 ## 05 ## 06 ## 07 #
 *      # 00 ## 01 ## 02 ## 03 #
 *      ########################
 */
public class BurrowState {
    private final byte[] cells = new byte[23];
    private final BitSet hash = new BitSet(69);
    private final int deltaCost;

    public BurrowState() {
        this.deltaCost = 0;
    }

    public BurrowState(BitSet hash, int deltaCost) {
        this.deltaCost = deltaCost;
        this.hash.or(hash);
        for (int i = 0; i < this.cells.length; ++i) {
            byte code[] = this.hash.get(3 * i, 3 * i + 3).toByteArray();
            this.cells[i] = code.length == 0 ? 0 : code[0];
        }
    }

    private BurrowState(byte cells[], int moveFrom, int moveTo, int deltaCost) {
        for (int i = 0; i < this.cells.length; ++i) {
            this.cells[i] = cells[i];
        }
        this.cells[moveTo] = this.cells[moveFrom];
        this.cells[moveFrom] = 0;
        this.deltaCost = deltaCost;
        this.generateHash();
    }

    public void generateHash() {
        this.hash.clear();
        for (int i = 0; i < this.cells.length; ++i) {
            this.hash.set(3 * i, (this.cells[i] & 1) != 0);
            this.hash.set(3 * i + 1, (this.cells[i] & 2) != 0);
            this.hash.set(3 * i + 2, (this.cells[i] & 4) != 0);
        }
    }

    public void set(int cell, char type) {
        this.cells[cell] = (byte) (type == 0 ? 0 : type - 'A' + 1);
    }

    public BitSet getHash() {
        return this.hash;
    }

    public int getDeltaCost() {
        return this.deltaCost;
    }

    public void print() {
        System.out.printf("#############\n#%s", this.getCellSymbol(16));
        for (int i = 17; i < this.cells.length - 2; ++i) {
            System.out.print(this.getCellSymbol(i) + ".");
        }
        System.out.printf("%s%s#\n###", this.getCellSymbol(21), this.getCellSymbol(22));
        for (int r = 0; r < 4; ++r) {
            for (int i = 0; i < 4; ++i) {
                System.out.print(this.getCellSymbol(4 * (3 - r) + i) + "#");
            }
            System.out.print(r == 0 ? "##\n  #" : "\n  #");
        }
        System.out.println("########");
    }

    private String getCellSymbol(int cell) {
        return this.cells[cell] == 0 ? "." : String.valueOf((char) ('A' + this.cells[cell] - 1));
    }

    @Override
    public int hashCode() {
        return this.hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof BurrowState other && this.hash.equals(other.hash));
    }

    public Set<BurrowState> getReachableStates() {
        Set<BurrowState> reachable = new HashSet<>();
        boolean holeDone[] = new boolean[] { true, true, true, true, };
        int holeTops[] = new int[4];
        for (int i = 0; i < 4; ++i) {
            while (holeTops[i] < 4 && this.cells[i + 4 * holeTops[i]] != 0) {
                holeDone[i] &= this.cells[i + 4 * holeTops[i]] == i + 1;
                ++holeTops[i];
            }
            if (!holeDone[i]) {
                this.gatherReachableStatesForPosition(i + 4 * (holeTops[i] - 1), reachable);
            }
        }
        for (int i = 16; i < this.cells.length; ++i) {
            if (this.cells[i] != 0 && holeDone[this.cells[i] - 1]) {
                this.gatherReachableDirectPathState(i, this.cells[i] - 1 + 4 * holeTops[this.cells[i] - 1], reachable);
            }
        }
        return reachable;
    }

    private void gatherReachableStatesForPosition(int pos, Set<BurrowState> dst) {
        int hallwayLeft = 17 + pos % 4;
        int hallwayRight = hallwayLeft + 1;
        int baseCost = (5 - pos / 4) * this.getTypeCost(this.cells[pos]);

        int cost = baseCost;
        while (hallwayLeft != 15 && this.cells[hallwayLeft] == 0) {
            dst.add(new BurrowState(this.cells, pos, hallwayLeft, cost));
            cost += (--hallwayLeft == 16 ? 1 : 2) * this.getTypeCost((this.cells[pos]));
        }

        cost = baseCost;
        while (hallwayRight != 23 && this.cells[hallwayRight] == 0) {
            dst.add(new BurrowState(this.cells, pos, hallwayRight, cost));
            cost += (++hallwayRight == 22 ? 1 : 2) * this.getTypeCost((this.cells[pos]));
        }
    }

    private void gatherReachableDirectPathState(int from, int to, Set<BurrowState> dst) {
        int intermediate = 17 + this.cells[from] - 1;
        int delta = 1;
        if (intermediate < from) {
            ++intermediate;
            delta = -1;
        }
        int pos = from;
        int stepCount = 0;
        while (pos != intermediate) {
            stepCount += pos == 16 || pos == 22 ? 1 : 2;
            pos += delta;
            if (this.cells[pos] != 0) {
                return;
            }
        }
        stepCount += 5 - to / 4;
        dst.add(new BurrowState(this.cells, from, to, stepCount * this.getTypeCost(this.cells[from])));
    }

    private int getTypeCost(byte type) {
        return switch (type) {
            case 1 -> 1;
            case 2 -> 10;
            case 3 -> 100;
            case 4 -> 1000;
            default -> 0;
        };
    }

    public static BurrowState getExample() {
        BurrowState burrow = new BurrowState();
        burrow.set(0, 'A');
        burrow.set(1, 'D');
        burrow.set(2, 'C');
        burrow.set(3, 'A');
        burrow.set(4, 'D');
        burrow.set(5, 'B');
        burrow.set(6, 'A');
        burrow.set(7, 'C');
        burrow.set(8, 'D');
        burrow.set(9, 'C');
        burrow.set(10, 'B');
        burrow.set(11, 'A');
        burrow.set(12, 'B');
        burrow.set(13, 'C');
        burrow.set(14, 'B');
        burrow.set(15, 'D');
        burrow.generateHash();
        return burrow;
    }

    public static BurrowState getTask() {
        BurrowState burrow = new BurrowState();
        burrow.set(0, 'B');
        burrow.set(1, 'A');
        burrow.set(2, 'B');
        burrow.set(3, 'C');
        burrow.set(4, 'D');
        burrow.set(5, 'B');
        burrow.set(6, 'A');
        burrow.set(7, 'C');
        burrow.set(8, 'D');
        burrow.set(9, 'C');
        burrow.set(10, 'B');
        burrow.set(11, 'A');
        burrow.set(12, 'C');
        burrow.set(13, 'D');
        burrow.set(14, 'D');
        burrow.set(15, 'A');
        burrow.generateHash();
        return burrow;
    }

    public static BurrowState getFinished() {
        BurrowState burrow = new BurrowState();
        burrow.set(0, 'A');
        burrow.set(1, 'B');
        burrow.set(2, 'C');
        burrow.set(3, 'D');
        burrow.set(4, 'A');
        burrow.set(5, 'B');
        burrow.set(6, 'C');
        burrow.set(7, 'D');
        burrow.set(8, 'A');
        burrow.set(9, 'B');
        burrow.set(10, 'C');
        burrow.set(11, 'D');
        burrow.set(12, 'A');
        burrow.set(13, 'B');
        burrow.set(14, 'C');
        burrow.set(15, 'D');
        burrow.generateHash();
        return burrow;
    }

    public static void main(String args[]) {
        BurrowState burrow = BurrowState.getTask();
        burrow.print();
        Stack<BurrowState> stack = new Stack<>();
        Map<BitSet, Integer> costs = new HashMap<>();
        stack.push(burrow);
        costs.put(burrow.getHash(), 0);
        while (!stack.isEmpty()) {
            BurrowState state = stack.pop();
            int baseCost = costs.get(state.getHash());
            for (BurrowState next : state.getReachableStates()) {
                Integer oldCost = costs.get(next.getHash());
                int newCost = baseCost + next.getDeltaCost();
                if (oldCost == null || newCost < oldCost) {
                    costs.put(next.getHash(), newCost);
                    stack.push(next);
                }
            }
        }
        Integer cost = costs.get(BurrowState.getFinished().getHash());
        System.out.println(cost);
    }
}
