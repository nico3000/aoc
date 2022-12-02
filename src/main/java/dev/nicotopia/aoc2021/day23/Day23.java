package dev.nicotopia.aoc2021.day23;

import java.io.IOException;

import dev.nicotopia.aoc2021.day23.Burrow.Amphipod;
import dev.nicotopia.aoc2021.day23.Burrow.Amphipod.Move;
import dev.nicotopia.aoc2021.day23.Burrow.Amphipod.Status;

public class Day23 {
    // private static Burrow getExample() {
    //     Burrow burrow = new Burrow();
    //     burrow.addAmphipod(0, 'A');
    //     burrow.addAmphipod(1, 'D');
    //     burrow.addAmphipod(2, 'C');
    //     burrow.addAmphipod(3, 'A');
    //     burrow.addAmphipod(4, 'B');
    //     burrow.addAmphipod(5, 'C');
    //     burrow.addAmphipod(6, 'B');
    //     burrow.addAmphipod(7, 'D');
    //     return burrow;
    // }

    private static Burrow getTask() {
        Burrow burrow = new Burrow();
        burrow.addAmphipod(0, 'B');
        burrow.addAmphipod(1, 'A');
        burrow.addAmphipod(2, 'B');
        burrow.addAmphipod(3, 'C');
        burrow.addAmphipod(4, 'C');
        burrow.addAmphipod(5, 'D');
        burrow.addAmphipod(6, 'D');
        burrow.addAmphipod(7, 'A');
        return burrow;
    }

    public static void main(String args[]) throws IOException {
        Burrow b = new Burrow();
        b.addAmphipod(14, 'C');
        //b.addAmphipod(4, 'C');
        //b.addAmphipod(11, 'B');
        b.print();
        /*for (Move m : b.getFromPosition(14).getPossibleMoves()) {
            System.out.printf("to: %d, cost: %d\n", m.toPos(), m.cost());
        }*/
        System.out.println(b.getFromPosition(14).getDirectPathToDestCost());

        Day23.getTask().print();
        System.out.println("Searching for any solution...");
        while (Day23.solve(Day23.getTask(), 0)) {
            System.out.printf("Found a solution with a cost of %d.\n", Day23.currentCost);
            Day23.maxCost = Day23.currentCost - 1;
            Day23.currentCost = 0;
            System.out.println("Searching for a better solution...");
        }
        System.out.println("No solution found.");
    }

    private static int currentCost = 0;
    private static int maxCost = Integer.MAX_VALUE;

    private static boolean solve(Burrow burrow, int depth) {
        if (Day23.maxCost < Day23.currentCost) {
            return false;
        }
        boolean allFinished = true;
        for (int i = 0; i < 15; ++i) {
            Amphipod a = burrow.getFromPosition(i);
            allFinished &= a == null || a.getStatus() == Status.FINISHED;
            if (a != null && a.getStatus() != Amphipod.Status.FINISHED && a.isDirectPathToDestFree()) {
                int deltaCost = a.getDirectPathToDestCost();
                int dst = a.getDestinationHole();
                if (burrow.getFromPosition(dst) == null) {
                    deltaCost += a.getMoveCost();
                    burrow.move(i, dst);
                } else {
                    burrow.move(i, dst += 4);
                }
                Day23.currentCost += deltaCost;
                //System.out.printf("%2d: move %2d -> %2d (its destination)\n", depth, i, dst);
                //burrow.print();
                if (Day23.solve(burrow, depth + 1)) {
                    return true;
                } else {
                    //System.out.printf("%2d: oh wait, nope\n", depth);
                    burrow.move(dst, i);
                    Day23.currentCost -= deltaCost;
                }
            }
        }
        if (allFinished) {
            return true;
        }
        for (int i = 0; i < 15; ++i) {
            Amphipod a = burrow.getFromPosition(i);
            if (a != null && a.getStatus() == Status.IN_ROOM) {
                for (Move move : a.getPossibleMoves()) {
                    burrow.move(i, move.toPos());
                    Day23.currentCost += move.cost();
                    //System.out.printf("%2d: %2d -> %2d\n", depth, i, move.toPos());
                    //burrow.print();
                    if (Day23.solve(burrow, depth + 1)) {
                        return true;
                    } else {
                        //System.out.printf("%2d: oh wait, nope\n", depth);
                        burrow.move(move.toPos(), i);
                        Day23.currentCost -= move.cost();
                    }
                }
            }
        }
        return false;
    }
}
