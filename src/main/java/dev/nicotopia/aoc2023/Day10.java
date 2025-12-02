package dev.nicotopia.aoc2023;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;

public class Day10 extends DayBase {
    private enum Classification {
        UNKNOWN, LOOP, LEFT, RIGHT,
    }

    private Vec2i start;
    private char[][] arena;
    private Classification classifications[][];

    private void processInput() {
        this.arena = this.getPrimaryPuzzleInputAs2DCharArray();
        for (int rowIdx = 0; rowIdx < this.arena.length; ++rowIdx) {
            int colIdx = String.valueOf(this.arena[rowIdx]).indexOf('S');
            if (colIdx != -1) {
                char top = rowIdx != 0 ? this.arena[rowIdx - 1][colIdx] : '.';
                char right = colIdx != this.arena[rowIdx].length - 1 ? this.arena[rowIdx][colIdx + 1] : '.';
                char bottom = rowIdx != this.arena.length - 1 ? this.arena[rowIdx + 1][colIdx] : '.';
                char left = colIdx != 0 ? this.arena[rowIdx][colIdx - 1] : '.';
                int type = (top == '7' || top == 'F' || top == '|' ? 1 : 0)
                        | (right == '7' || right == 'J' || right == '-' ? 2 : 0)
                        | (bottom == 'J' || bottom == '|' || bottom == 'L' ? 4 : 0)
                        | (left == 'F' || left == '-' || left == 'L' ? 8 : 0);
                this.arena[rowIdx][colIdx] = switch (type) {
                    case 3 -> 'L';
                    case 5 -> '|';
                    case 6 -> 'F';
                    case 9 -> 'J';
                    case 10 -> '-';
                    case 12 -> '7';
                    default -> throw new AocException("Start tile not reached by exactly two tiles.");
                };
                this.start = new Vec2i(colIdx, rowIdx);
                break;
            }
        }
        this.classifications = new Classification[this.arena.length][this.arena[0].length];
        Arrays.stream(this.classifications).forEach(row -> Arrays.fill(row, Classification.UNKNOWN));
    }

    private enum Origin {
        FROM_LEFT, FROM_TOP, FROM_RIGHT, FROM_BOTTOM
    }

    private void forEachLoopTile(BiConsumer<Vec2i, Origin> tileConsumer) {
        Vec2i c = this.start;
        Origin origin = null;
        do {
            switch (this.arena[c.y()][c.x()]) {
                case 'L':
                    if (origin == null || origin == Origin.FROM_TOP) {
                        c = new Vec2i(c.x() + 1, c.y());
                        origin = Origin.FROM_LEFT;
                    } else if (origin == Origin.FROM_RIGHT) {
                        c = new Vec2i(c.x(), c.y() - 1);
                        origin = Origin.FROM_BOTTOM;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                case '|':
                    if (origin == null || origin == Origin.FROM_TOP) {
                        c = new Vec2i(c.x(), c.y() + 1);
                        origin = Origin.FROM_TOP;
                    } else if (origin == Origin.FROM_BOTTOM) {
                        c = new Vec2i(c.x(), c.y() - 1);
                        origin = Origin.FROM_BOTTOM;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                case 'F':
                    if (origin == null || origin == Origin.FROM_RIGHT) {
                        c = new Vec2i(c.x(), c.y() + 1);
                        origin = Origin.FROM_TOP;
                    } else if (origin == Origin.FROM_BOTTOM) {
                        c = new Vec2i(c.x() + 1, c.y());
                        origin = Origin.FROM_LEFT;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                case 'J':
                    if (origin == null || origin == Origin.FROM_TOP) {
                        c = new Vec2i(c.x() - 1, c.y());
                        origin = Origin.FROM_RIGHT;
                    } else if (origin == Origin.FROM_LEFT) {
                        c = new Vec2i(c.x(), c.y() - 1);
                        origin = Origin.FROM_BOTTOM;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                case '-':
                    if (origin == null || origin == Origin.FROM_LEFT) {
                        c = new Vec2i(c.x() + 1, c.y());
                        origin = Origin.FROM_LEFT;
                    } else if (origin == Origin.FROM_RIGHT) {
                        c = new Vec2i(c.x() - 1, c.y());
                        origin = Origin.FROM_RIGHT;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                case '7':
                    if (origin == null || origin == Origin.FROM_LEFT) {
                        c = new Vec2i(c.x(), c.y() + 1);
                        origin = Origin.FROM_TOP;
                    } else if (origin == Origin.FROM_BOTTOM) {
                        c = new Vec2i(c.x() - 1, c.y());
                        origin = Origin.FROM_RIGHT;
                    } else {
                        throw new AocException("Illegal tile connection");
                    }
                    break;
                default:
                    throw new AocException("Illegal tile");
            }
            tileConsumer.accept(c, origin);
        } while (!this.start.equals(c));
    }

    private int partOne() {
        LongAdder adder = new LongAdder();
        this.forEachLoopTile((c, o) -> {
            adder.increment();
            this.classifications[c.y()][c.x()] = Classification.LOOP;
        });
        return adder.intValue() / 2;
    }

    private void classifyLoopTileNeighbours(Vec2i c, Origin o) {
        if (c.x() != 0 && this.classifications[c.y()][c.x() - 1] == Classification.UNKNOWN) {
            if (o == Origin.FROM_TOP) {
                this.classifications[c.y()][c.x() - 1] = Classification.RIGHT;
            } else if (o == Origin.FROM_BOTTOM) {
                this.classifications[c.y()][c.x() - 1] = Classification.LEFT;
            } else {
                this.classifications[c.y()][c.x() - 1] = switch (this.arena[c.y()][c.x()]) {
                    case 'L' -> Classification.LEFT;
                    case 'F' -> Classification.RIGHT;
                    default -> throw new AocException("Illegal tile connection");
                };
            }
        }
        if (c.x() != this.arena[c.y()].length - 1
                && this.classifications[c.y()][c.x() + 1] == Classification.UNKNOWN) {
            if (o == Origin.FROM_TOP) {
                this.classifications[c.y()][c.x() + 1] = Classification.LEFT;
            } else if (o == Origin.FROM_BOTTOM) {
                this.classifications[c.y()][c.x() + 1] = Classification.RIGHT;
            } else {
                this.classifications[c.y()][c.x() + 1] = switch (this.arena[c.y()][c.x()]) {
                    case 'J' -> Classification.RIGHT;
                    case '7' -> Classification.LEFT;
                    default -> throw new AocException("Illegal tile connection");
                };
            }
        }
        if (c.y() != 0 && this.classifications[c.y() - 1][c.x()] == Classification.UNKNOWN) {
            if (o == Origin.FROM_LEFT) {
                this.classifications[c.y() - 1][c.x()] = Classification.LEFT;
            } else if (o == Origin.FROM_RIGHT) {
                this.classifications[c.y() - 1][c.x()] = Classification.RIGHT;
            } else {
                this.classifications[c.y() - 1][c.x()] = switch (this.arena[c.y()][c.x()]) {
                    case '7' -> Classification.RIGHT;
                    case 'F' -> Classification.LEFT;
                    default -> throw new AocException("Illegal tile connection");
                };
            }
        }
        if (c.y() != this.arena.length - 1 && this.classifications[c.y() + 1][c.x()] == Classification.UNKNOWN) {
            if (o == Origin.FROM_LEFT) {
                this.classifications[c.y() + 1][c.x()] = Classification.RIGHT;
            } else if (o == Origin.FROM_RIGHT) {
                this.classifications[c.y() + 1][c.x()] = Classification.LEFT;
            } else {
                this.classifications[c.y() + 1][c.x()] = switch (this.arena[c.y()][c.x()]) {
                    case 'J' -> Classification.LEFT;
                    case 'L' -> Classification.RIGHT;
                    default -> throw new AocException("Illegal tile connection");
                };
            }
        }
    }

    private boolean polygonFillClassifications(Classification c) {
        boolean hitBorder = false;
        Stack<Vec2i> stack = new Stack<>();
        Vec2i.streamFromRectangle(0, 0, this.classifications.length, this.classifications[0].length)
                .filter(p -> this.classifications[p.y()][p.x()] == c).forEach(stack::push);
        while (!stack.isEmpty()) {
            Vec2i p = stack.pop();
            this.classifications[p.y()][p.x()] = c;
            hitBorder |= p.x() == 0 || p.y() == 0 || p.x() == this.classifications[0].length - 1
                    || p.y() == this.classifications.length - 1;
            if (p.x() != 0 && this.classifications[p.y()][p.x() - 1] == Classification.UNKNOWN) {
                stack.push(new Vec2i(p.x() - 1, p.y()));
            }
            if (p.y() != 0 && this.classifications[p.y() - 1][p.x()] == Classification.UNKNOWN) {
                stack.push(new Vec2i(p.x(), p.y() - 1));
            }
            if (p.x() != this.classifications[0].length - 1
                    && this.classifications[p.y()][p.x() + 1] == Classification.UNKNOWN) {
                stack.push(new Vec2i(p.x() + 1, p.y()));
            }
            if (p.y() != this.classifications.length - 1
                    && this.classifications[p.y() + 1][p.x()] == Classification.UNKNOWN) {
                stack.push(new Vec2i(p.x(), p.y() + 1));
            }
        }
        return hitBorder;
    }

    private int partTwo() {
        Vec2i.streamFromRectangle(0, 0, this.arena.length, this.arena[0].length)
                .filter(t -> classifications[t.y()][t.x()] != Classification.LOOP)
                .forEach(t -> this.arena[t.y()][t.x()] = ' ');
        this.forEachLoopTile(this::classifyLoopTileNeighbours);
        boolean leftHitBorder = this.polygonFillClassifications(Classification.LEFT);
        boolean rightHitBorder = this.polygonFillClassifications(Classification.RIGHT);
        if (leftHitBorder == rightHitBorder) {
            throw new AocException("Both regions hit the border or both did not");
        }
        Classification toCount = leftHitBorder ? Classification.RIGHT : Classification.LEFT;
        return (int) Arrays.stream(this.classifications).map(Arrays::stream)
                .mapToLong(row -> row.filter(c -> c == toCount).count()).sum();
    }

    @Override
    public String toString() {
        return Arrays.stream(this.classifications).map(row -> Arrays.stream(row).map(c -> switch (c) {
            case LEFT -> "L";
            case RIGHT -> "R";
            case LOOP -> "#";
            case UNKNOWN -> " ";
        }).collect(Collectors.joining())).collect(Collectors.joining("\n"));
    }

    @Override
    public void run() {
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}