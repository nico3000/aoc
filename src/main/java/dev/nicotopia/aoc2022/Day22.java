package dev.nicotopia.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day22 {
    private record Position(int x, int y) {
    }

    private record PositionAndDirection(Position p, int dir) {
    }

    private record Instruction(int move, int rot) {
    }

    private interface MoveFn {
        public PositionAndDirection move(List<Block> blocks, Position p, int dir, int dx, int dy);
    }

    private static class Block {
        private final Position base;
        private char tiles[][];

        public Block(Position base) {
            this.base = base;
        }

        public void addRow(char row[]) {
            this.tiles = this.tiles == null ? new char[1][] : Arrays.copyOf(tiles, tiles.length + 1);
            this.tiles[this.tiles.length - 1] = row;
        }

        public boolean containsX(int x) {
            return this.base.x <= x && x < this.base.x + this.tiles[0].length;
        }

        public boolean containsY(int y) {
            return this.base.y <= y && y < this.base.y + this.tiles.length;
        }

        public boolean contains(Position p) {
            return this.containsX(p.x) && this.containsY(p.y);
        }

        public boolean isWall(Position p) {
            return this.tiles[p.y - this.base.y][p.x - this.base.x] == '#';
        }
    }

    public static void main(String[] args) throws IOException {
        List<Block> blocks = new ArrayList<>();
        List<Instruction> instructions = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day22.class.getResourceAsStream("/2022/day22.txt")))) {
            Pattern p = Pattern.compile("(\\s*)([\\.#]+)");
            Block b = null;
            for (String line : br.lines().toList()) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    if (b != null && (b.base.x != m.group(1).length() || b.tiles[0].length != m.group(2).length())) {
                        blocks.add(b);
                    }
                    if (b == null || (b.base.x != m.group(1).length() || b.tiles[0].length != m.group(2).length())) {
                        b = new Block(new Position(m.group(1).length(), b == null ? 0 : b.base.y + b.tiles.length));
                    }
                    b.addRow(m.group(2).toCharArray());
                } else if (!line.isBlank()) {
                    m = Pattern.compile("(\\d+)([LR])").matcher(line);
                    int last = 0;
                    while (m.find()) {
                        instructions.add(
                                new Instruction(Integer.valueOf(m.group(1)), m.group(2).charAt(0) == 'L' ? -1 : 1));
                        last = m.end();
                    }
                    instructions.add(new Instruction(Integer.valueOf(line.substring(last)), 0));
                }
            }
            blocks.add(b);
        }
        System.out.println("Part one: " + getPassword(blocks, instructions, Day22::movePartOne));
        System.out.println("Part two: " + getPassword(blocks, instructions, Day22::movePartTwo));
    }

    private static int getPassword(List<Block> blocks, List<Instruction> instructions, MoveFn fn) {
        int blockIdx = 0;
        Position p = blocks.get(blockIdx).base;
        int dir = 2; // 0: <, 1: ^ 2: >, 3: v
        for (Instruction instr : instructions) {
            for (int i = 0; i < instr.move; ++i) {
                int dx = dir == 0 ? -1 : dir == 2 ? 1 : 0;
                int dy = dir == 1 ? -1 : dir == 3 ? 1 : 0;
                PositionAndDirection next = fn.move(blocks, p, dir, dx, dy);
                if (blocks.stream().filter(b -> b.contains(next.p)).findAny().get().isWall(next.p)) {
                    break;
                }
                p = next.p;
                dir = next.dir;
            }
            dir = (dir + instr.rot + 4) % 4;
        }
        return 1000 * (p.y + 1) + 4 * (p.x + 1) + (dir + 2) % 4;
    }

    private static PositionAndDirection movePartOne(List<Block> blocks, Position p, int dir, int dx, int dy) {
        int blockIdx = IntStream.range(0, blocks.size()).filter(i -> blocks.get(i).contains(p)).findAny().getAsInt();
        Block b = blocks.get(blockIdx);
        int x = dx != 0 ? b.base.x + (p.x + dx - b.base.x + b.tiles[0].length) % b.tiles[0].length : p.x;
        int y = p.y + dy;
        if (!b.containsY(y)) {
            do {
                blockIdx = (blockIdx + dy + blocks.size()) % blocks.size();
            } while (!blocks.get(blockIdx).containsX(x));
            y = blocks.get(blockIdx).base.y + (dy < 0 ? blocks.get(blockIdx).tiles.length - 1 : 0);
        }
        return new PositionAndDirection(new Position(x, y), dir);
    }

    private static PositionAndDirection movePartTwo(List<Block> blocks, Position p, int dir, int dx, int dy) {
        int x = p.x + dx;
        int y = p.y + dy;
        if (blocks.stream().anyMatch(b -> b.contains(new Position(x, y)))) {
            return new PositionAndDirection(new Position(x, y), dir);
        } else if (x == 100 && y == 50) {
            if (dir == 3) {
                return new PositionAndDirection(new Position(99, 50), 0);
            } else if (dir == 2) {
                return new PositionAndDirection(new Position(100, 49), 1);
            }
        } else if (x == 49 && y == 99) {
            if (dir == 0) {
                return new PositionAndDirection(new Position(49, 100), 3);
            } else if (dir == 1) {
                return new PositionAndDirection(new Position(50, 99), 2);
            }
        } else if (x == 50 && y == 150) {
            if (dir == 3) {
                return new PositionAndDirection(new Position(49, 150), 0);
            } else if (dir == 2) {
                return new PositionAndDirection(new Position(50, 149), 1);
            }
        } else if (y == -1 && x < 100) {
            return new PositionAndDirection(new Position(0, 100 + x), 2);
        } else if (y == -1 && 100 <= x) {
            return new PositionAndDirection(new Position(x - 100, 199), 1);
        } else if (y < 50 && x == 49) {
            return new PositionAndDirection(new Position(0, 149 - y), 2);
        } else if (y < 50 && x == 150) {
            return new PositionAndDirection(new Position(99, 149 - y), 0);
        } else if (y == 50 && 100 <= x) {
            return new PositionAndDirection(new Position(99, x - 50), 0);
        } else if (y == 99 && x < 50) {
            return new PositionAndDirection(new Position(50, x + 50), 2);
        } else if (y < 100 && x == 49) {
            return new PositionAndDirection(new Position(y - 50, 100), 3);
        } else if (y < 100 && x == 100) {
            return new PositionAndDirection(new Position(y + 50, 49), 1);
        } else if (y < 150 && x == -1) {
            return new PositionAndDirection(new Position(50, 149 - y), 2);
        } else if (y < 150 && x == 100) {
            return new PositionAndDirection(new Position(149, 149 - y), 0);
        } else if (y == 150 && 50 <= x) {
            return new PositionAndDirection(new Position(49, x + 100), 0);
        } else if (y < 200 && x == -1) {
            return new PositionAndDirection(new Position(y - 100, 0), 3);
        } else if (y < 200 && x == 50) {
            return new PositionAndDirection(new Position(y - 100, 149), 1);
        } else if (y == 200) {
            return new PositionAndDirection(new Position(x + 100, 0), 3);
        }
        return null;
    }
}