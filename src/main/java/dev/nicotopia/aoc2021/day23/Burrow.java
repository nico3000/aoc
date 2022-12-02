package dev.nicotopia.aoc2021.day23;

import java.util.LinkedList;
import java.util.List;

import dev.nicotopia.aoc2021.day23.Burrow.Amphipod.Status;

/**
 * <pre>
 * "Burrow position indexing"
 * #############
 * #89.a.b.c.de#
 * ###4#5#6#7###
 *   #0#1#2#3#
 *   #########
 * </pre>
 */
public class Burrow {
    private final Amphipod[] positions = new Amphipod[15];

    public Amphipod getFromPosition(int pos) {
        return this.positions[pos];
    }

    public void addAmphipod(int pos, char type) {
        this.positions[pos] = new Amphipod(type, pos);
    }

    public void move(int from, int to) {
        this.positions[to] = this.positions[from];
        this.positions[from] = null;
        this.positions[to].setPosition(to);
    }

    private boolean isHoleReady(int hole) {
        return this.positions[hole] == null
                || (this.positions[hole].status == Status.FINISHED && this.positions[hole + 4] == null);
    }

    public void print() {
        System.out.printf("#############\n");
        System.out.printf("#%s%s.%s.%s.%s.%s%s#\n", this.getSymbol(8), this.getSymbol(9), this.getSymbol(10),
                this.getSymbol(11), this.getSymbol(12), this.getSymbol(13), this.getSymbol(14));
        System.out.printf("###%s#%s#%s#%s###\n", this.getSymbol(4), this.getSymbol(5), this.getSymbol(6),
                this.getSymbol(7));
        System.out.printf("  #%s#%s#%s#%s#\n", this.getSymbol(0), this.getSymbol(1), this.getSymbol(2),
                this.getSymbol(3));
        System.out.printf("  #########\n");
    }

    private String getSymbol(int pos) {
        return this.positions[pos] == null ? "." : String.valueOf(this.positions[pos].type);
    }

    public class Amphipod {
        public enum Status {
            IN_ROOM,
            IN_HALLWAY,
            FINISHED,
        }

        private final char type;
        private int pos;
        private Status status;
        private final List<Integer> directPathToDest = new LinkedList<>();
        private int directPathToDestCost = 0;

        public Amphipod(char type, int pos) {
            this.type = type;
            this.setPosition(pos);
        }

        public Status getStatus() {
            return this.status;
        }

        public int getMoveCost() {
            return switch (this.type) {
                case 'A' -> 1;
                case 'B' -> 10;
                case 'C' -> 100;
                case 'D' -> 1000;
                default -> 0;
            };
        }

        public void setPosition(int pos) {
            this.pos = pos;
            if (8 <= this.pos) {
                this.status = Status.IN_HALLWAY;
            } else if (this.pos % 4 == this.type - 'A'
                    && (this.pos < 4 || (Burrow.this.positions[this.pos - 4].status == Status.FINISHED))) {
                this.status = Status.FINISHED;
            } else {
                this.status = Status.IN_ROOM;
            }
            this.updateDirectPathToDestination();
        }

        public record Move(int toPos, int cost) {
        }

        public List<Move> getPossibleMoves() {
            List<Move> moves = new LinkedList<>();
            assert (this.status != Status.IN_HALLWAY);
            if (this.pos < 4 && Burrow.this.getFromPosition(this.pos + 4) != null) {
                return moves;
            }
            int left = this.pos % 4 + 9;
            int right = left + 1;
            int baseCost = this.pos < 4 ? 3 * this.getMoveCost() : 2 * this.getMoveCost();
            int cost = baseCost;
            while (left != 7 && Burrow.this.getFromPosition(left) == null) {
                moves.add(new Move(left, cost));
                cost += (--left == 8 ? 1 : 2) * this.getMoveCost();
            }
            cost = baseCost;
            while (right != 0xf && Burrow.this.getFromPosition(right) == null) {
                moves.add(new Move(right, cost));
                cost += (++right == 0xe ? 1 : 2) * this.getMoveCost();
            }
            return moves;
        }

        public boolean isDirectPathToDestFree() {
            if (this.directPathToDest.isEmpty() || !Burrow.this.isHoleReady(this.getDestinationHole())) {
                return false;
            }
            for (int p : this.directPathToDest) {
                if (Burrow.this.getFromPosition(p) != null) {
                    return false;
                }
            }
            return true;
        }

        public int getDirectPathToDestCost() {
            return this.directPathToDestCost;
        }

        public int getDestinationHole() {
            return this.type - 'A';
        }

        private void updateDirectPathToDestination() {
            this.directPathToDest.clear();
            this.directPathToDestCost = 0;
            if (this.status == Status.IN_HALLWAY || this.type - 'A' != this.pos % 4) {
                int pos = this.pos;
                if (pos < 4) {
                    this.directPathToDest.add(pos += 4);
                    this.directPathToDestCost += this.getMoveCost();
                }
                if (pos < 8) {
                    pos += pos % 4 < this.type - 'A' ? 6 : 5;
                    this.directPathToDest.add(pos);
                    this.directPathToDestCost += 2 * this.getMoveCost();
                }
                int hallwayDst = 9 + this.type - 'A';
                int delta = 1;
                if (hallwayDst < pos) {
                    ++hallwayDst;
                    delta = -1;
                }
                while (pos != hallwayDst) {
                    this.directPathToDestCost += (pos == 0x8 || pos == 0xe ? 1 : 2) * this.getMoveCost();
                    pos += delta;
                    this.directPathToDest.add(pos);

                }
                this.directPathToDest.add(this.type - 'A' + 4);
                this.directPathToDestCost += 2 * this.getMoveCost();
            }
        }
    }
}
