package dev.nicotopia.aoc2016;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 {
    private static class Node {
        private static Pattern PATTERN = Pattern
                .compile("^/dev/grid/node-x([0-9]+)-y([0-9]+)\\s+([0-9]+)T\\s+([0-9]+)T\\s+([0-9]+)T\\s+([0-9]+)%$");

        private final int x;
        private final int y;
        private final int size;
        private int used;

        public Node(String line) {
            Matcher m = PATTERN.matcher(line);
            if (!m.matches()) {
                throw new IllegalArgumentException("Malformed line: " + line);
            }
            this.x = Integer.valueOf(m.group(1));
            this.y = Integer.valueOf(m.group(2));
            this.size = Integer.valueOf(m.group(3));
            this.used = Integer.valueOf(m.group(4));
            int avail = Integer.valueOf(m.group(5));
            if (avail != this.size - this.used) {
                throw new IllegalArgumentException();
            }
        }
    }

    private record NodePair(Node a, Node b) {
    }

    private static final Map<String, Integer> distances = new HashMap<>();
    private static final PriorityQueue<String> visited = new PriorityQueue<>((first, second) -> {
        Integer d0 = distances.get(first);
        Integer d1 = distances.get(second);
        return Integer.compare(d0 == null ? Integer.MAX_VALUE : d0, d1 == null ? Integer.MAX_VALUE : d1);
    });
    private static Node grid[][];
    private static Node emptyNode;
    private static Node goalDataNode;
    private static int minDistance = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        List<Node> nodes;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day22.class.getResourceAsStream("/2016/day22.txt")))) {
            /*String cmd = */br.readLine();
            /*String header = */br.readLine();
            nodes = br.lines().map(Node::new).toList();
        }
        List<NodePair> pairs = new LinkedList<>();
        for (Node a : nodes) {
            for (Node b : nodes) {
                if (a != b && a.used != 0 && a.used <= b.size - b.used) {
                    pairs.add(new NodePair(a, b));
                }
            }
        }
        System.out.println("Part one, pair count: " + pairs.size());
        int maxX = nodes.stream().mapToInt(n -> n.x).max().getAsInt();
        int maxY = nodes.stream().mapToInt(n -> n.y).max().getAsInt();
        grid = new Node[maxY + 1][maxX + 1];
        nodes.forEach(n -> grid[n.y][n.x] = n);
        goalDataNode = grid[0][maxX];
        emptyNode = nodes.stream().filter(n -> n.used == 0).findAny().get();

        int minSize = nodes.stream().mapToInt(n -> n.size).min().getAsInt();
        nodes.stream().filter(n -> minSize < n.used).forEach(n -> grid[n.y][n.x] = null);
        printGrid();
        System.exit(0);

        String state = serializeState();
        minDistance = Integer.MAX_VALUE;
        int maxSearchDistance = 16;
        do {
            System.out.println("Searching with a maximum search distance of " + maxSearchDistance + "...");
            findMinDistance(state, maxSearchDistance);
            maxSearchDistance += 4;
        } while (minDistance == Integer.MAX_VALUE);
        System.out.println(minDistance);
    }

    private static void findMinDistance(String startState, int maxSearchDistance) {
        visited.clear();
        distances.clear();
        visited.offer(startState);
        distances.put(startState, 0);
        while (!visited.isEmpty()) {
            String current = visited.poll();
            restoreState(current);
            int distance = distances.get(current);
            if (grid[0][0] == goalDataNode) {
                minDistance = Math.min(distance, minDistance);
            } else if (distance < Math.min(minDistance, maxSearchDistance)) {
                if (emptyNode.x != 0 && grid[emptyNode.y][emptyNode.x - 1].used < emptyNode.size) {
                    update(distance, grid[emptyNode.y][emptyNode.x - 1]);
                }
                if (emptyNode.y != 0 && grid[emptyNode.y - 1][emptyNode.x].used < emptyNode.size) {
                    update(distance, grid[emptyNode.y - 1][emptyNode.x]);
                }
                if (emptyNode.x != grid[emptyNode.y].length - 1
                        && grid[emptyNode.y][emptyNode.x + 1].used < emptyNode.size) {
                    update(distance, grid[emptyNode.y][emptyNode.x + 1]);
                }
                if (emptyNode.y != grid.length - 1 && grid[emptyNode.y + 1][emptyNode.x].used < emptyNode.size) {
                    update(distance, grid[emptyNode.y + 1][emptyNode.x]);
                }
            }
        }
    }

    private static void update(int currentDistance, Node next) {
        Node current = emptyNode;
        current.used = next.used;
        next.used = 0;
        emptyNode = next;
        if (goalDataNode == next) {
            goalDataNode = current;
        }
        String ser = serializeState();
        Integer distance = distances.get(ser);
        if (distance == null) {
            visited.add(ser);
        }
        if (distance == null || currentDistance + 1 < distance) {
            distances.put(ser, currentDistance + 1);
        }
        next.used = current.used;
        current.used = 0;
        emptyNode = current;
        if (goalDataNode == current) {
            goalDataNode = next;
        }
    }

    private static void printGrid() {
        for (int y = 0; y < grid.length; ++y) {
            for (int x = 0; x < grid[y].length; ++x) {
                System.out.print(x == 0 && y == 0 ? '(' : ' ');
                if (grid[y][x] == null) {
                    System.out.print('#');
                } else if (grid[y][x].used == 0) {
                    System.out.print('_');
                } else if (grid[y][x] == goalDataNode) {
                    System.out.print('G');
                } else {
                    System.out.print('.');
                }
                System.out.print(x == 0 && y == 0 ? ')' : ' ');
            }
            System.out.println();
        }
    }

    private static String serializeState() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            for (int y = 0; y < grid.length; ++y) {
                for (int x = 0; x < grid[y].length; ++x) {
                    baos.write((byte) (grid[y][x].used >> 8));
                    baos.write((byte) grid[y][x].used);
                }
            }
            baos.write(emptyNode.x);
            baos.write(emptyNode.y);
            baos.write(goalDataNode.x);
            baos.write(goalDataNode.y);
            return new String(baos.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void restoreState(String serialized) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized.getBytes())) {
            for (int y = 0; y < grid.length; ++y) {
                for (int x = 0; x < grid[y].length; ++x) {
                    byte b0 = (byte) bais.read();
                    byte b1 = (byte) bais.read();
                    grid[y][x].used = (int) (b0 << 8) | (int) b1;
                }
            }
            int emptyX = bais.read();
            int emptyY = bais.read();
            int goalDataNodeX = bais.read();
            int goalDataNodeY = bais.read();
            emptyNode = grid[emptyY][emptyX];
            goalDataNode = grid[goalDataNodeY][goalDataNodeX];
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
