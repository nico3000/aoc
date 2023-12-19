package dev.nicotopia.aoc2018;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dev.nicotopia.GraphUtil;
import dev.nicotopia.GraphUtil.HashedAStarInterface;
import dev.nicotopia.GraphUtil.NodeDistancePair;
import dev.nicotopia.Pair;
import dev.nicotopia.Vec2i;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day15 extends DayBase {
    private enum Type {
        ELF, GOBLIN, WALL, FLOOR
    }

    private interface Tile {
        public Type getType();
    }

    private record Floor() implements Tile {
        @Override
        public Type getType() {
            return Type.FLOOR;
        }
    }

    private record Wall() implements Tile {
        @Override
        public Type getType() {
            return Type.WALL;
        }
    }

    private class Entity implements Tile {
        private final Type type;
        private int hitPoints = 200;

        public Entity(Type type) {
            this.type = type;
        }

        public int getAttackPower() {
            return this.type == Type.ELF ? Day15.this.elfAttackPower : 3;
        }

        @Override
        public Type getType() {
            return this.type;
        }
    }

    private Tile[][] arena;
    private int elfAttackPower = 3;

    private NodeDistancePair<Vec2i> shortestDistance(Vec2i from, Vec2i to) {
        return GraphUtil.aStar(new HashedAStarInterface<Vec2i>((p, i) -> {
            Vec2i n = this.getNeighbour(p, i, t -> t == Type.FLOOR);
            return n == null ? null : new NodeDistancePair<Vec2i>(n, 1);
        }, to::manhattanDistanceTo, to::equals), from);
    }

    private Vec2i getNeighbour(Vec2i p, int i, Predicate<Type> accept) {
        if (accept.test(this.getTileType(new Vec2i(p.x(), p.y() - 1))) && i-- == 0) {
            return new Vec2i(p.x(), p.y() - 1);
        }
        if (accept.test(this.getTileType(new Vec2i(p.x() - 1, p.y()))) && i-- == 0) {
            return new Vec2i(p.x() - 1, p.y());
        }
        if (accept.test(this.getTileType(new Vec2i(p.x() + 1, p.y()))) && i-- == 0) {
            return new Vec2i(p.x() + 1, p.y());
        }
        if (accept.test(this.getTileType(new Vec2i(p.x(), p.y() + 1))) && i-- == 0) {
            return new Vec2i(p.x(), p.y() + 1);
        }
        return null;
    }

    private Stream<Vec2i> getFloorNeighbours(Vec2i p) {
        return IntStream.range(0, 4).mapToObj(i -> this.getNeighbour(p, i, t -> t == Type.FLOOR))
                .filter(n -> n != null);
    }

    private Type getTileType(int x, int y) {
        return 0 <= y && y < this.arena.length && 0 <= x && x < this.arena[y].length ? this.arena[y][x].getType()
                : Type.WALL;
    }

    private Type getTileType(Vec2i p) {
        return this.getTileType(p.x(), p.y());
    }

    private int nearest(NodeDistancePair<Vec2i> a, NodeDistancePair<Vec2i> b) {
        return a.distance() == b.distance() ? this.readingOrder(a.node(), b.node())
                : Integer.compare(a.distance(), b.distance());
    }

    private int readingOrder(Vec2i a, Vec2i b) {
        return a.y() == b.y() ? Integer.compare(a.x(), b.x()) : Integer.compare(a.y(), b.y());
    }

    private Stream<Vec2i> getEntityPositions() {
        return Vec2i.streamFromRectangle(0, 0, this.arena[0].length, this.arena.length).filter(this::isEntity)
                .sorted(this::readingOrder);
    }

    private Stream<Vec2i> getEnemyPositions(Vec2i p) {
        return this.getEntityPositions().filter(e -> this.getTileType(e) != this.getTileType(p));
    }

    private boolean isEntity(Vec2i p) {
        return this.getTileType(p) == Type.ELF || this.getTileType(p) == Type.GOBLIN;
    }

    private int compareHitPoints(Vec2i a, Vec2i b) {
        Entity ea = (Entity) this.arena[a.y()][a.x()];
        Entity eb = (Entity) this.arena[b.y()][b.x()];
        return ea.hitPoints == eb.hitPoints ? this.readingOrder(a, b) : Integer.compare(ea.hitPoints, eb.hitPoints);
    }

    private void buildArena() {
        this.arena = this.getPrimaryPuzzleInput().stream().map(l -> l.chars().mapToObj(c -> switch (c) {
            case '#' -> new Wall();
            case '.' -> new Floor();
            case 'E' -> new Entity(Type.ELF);
            case 'G' -> new Entity(Type.GOBLIN);
            default -> throw new AocException("Illegal tile: %c", c);
        }).toArray(Tile[]::new)).toArray(Tile[][]::new);
    }

    private boolean round() {
        boolean somethingHappened = false;
        List<Vec2i> entities = this.getEntityPositions().toList();
        for (Vec2i e : entities) {
            if (this.getTileType(e) == Type.FLOOR) {
                continue;
            } else if (this.getEnemyPositions(e).count() == 0) {
                return false;
            }
            if (this.tryToAttack(e)) {
                somethingHappened = true;
            } else {
                Optional<Vec2i> targetPos = this.getEnemyPositions(e).map(this::getFloorNeighbours).flatMap(s -> s)
                        .distinct().map(o -> this.shortestDistance(e, o)).filter(r -> r != null).sorted(this::nearest)
                        .findFirst().map(NodeDistancePair::node);
                if (targetPos.isPresent()) {
                    somethingHappened = true;
                    Vec2i dest = this.getFloorNeighbours(e)
                            .map(n -> new Pair<>(n, this.shortestDistance(n, targetPos.get())))
                            .filter(p -> p.second() != null).sorted((a, b) -> this.nearest(a.second(), b.second()))
                            .map(Pair::first).findFirst().get();
                    Tile prev = this.arena[e.y()][e.x()];
                    this.arena[e.y()][e.x()] = this.arena[dest.y()][dest.x()];
                    this.arena[dest.y()][dest.x()] = prev;
                    this.tryToAttack(dest);
                }
            }
        }
        return somethingHappened;
    }

    private boolean tryToAttack(Vec2i entityPos) {
        Optional<Vec2i> enemyPos = this.getEnemyPositions(entityPos).filter(v -> v.manhattanDistanceTo(entityPos) == 1)
                .sorted(this::compareHitPoints).findFirst();
        if (!enemyPos.isPresent()) {
            return false;
        }
        Entity enemy = ((Entity) this.arena[enemyPos.get().y()][enemyPos.get().x()]);
        enemy.hitPoints -= ((Entity) this.arena[entityPos.y()][entityPos.x()]).getAttackPower();
        if (enemy.hitPoints <= 0) {
            this.arena[enemyPos.get().y()][enemyPos.get().x()] = new Floor();
        }
        return true;
    }

    private OptionalInt fight(BooleanSupplier earlyOut) {
        this.buildArena();
        int count = 0;
        while (this.round()) {
            if (earlyOut.getAsBoolean()) {
                return OptionalInt.empty();
            }
            ++count;
        }
        return OptionalInt.of(count * Vec2i.streamFromRectangle(0, 0, this.arena[0].length, this.arena.length)
                .mapToInt(v -> this.arena[v.y()][v.x()] instanceof Entity e ? e.hitPoints : 0).sum());
    }

    private int getElfCount() {
        return (int) this.getEntityPositions().filter(v -> this.getTileType(v) == Type.ELF).count();
    }

    private int partTwo() {
        for (;;) {
            this.buildArena();
            int elfCount = this.getElfCount();
            ++this.elfAttackPower;
            OptionalInt outcome = this.fight(() -> this.getElfCount() < elfCount);
            if (outcome.isPresent()) {
                return outcome.getAsInt();
            }
        }
    }

    @Override
    public void run() {
        this.addPresetFromResource("Example 1", "/2018/day15e1.txt");
        this.addPresetFromResource("Example 2", "/2018/day15e2.txt");
        this.addPresetFromResource("Example 3", "/2018/day15e3.txt");
        this.addPresetFromResource("Example 4", "/2018/day15e4.txt");
        this.addPresetFromResource("Example 5", "/2018/day15e5.txt");
        this.addPresetFromResource("Example 6", "/2018/day15e6.txt");
        this.addTask("Part one", () -> this.fight(() -> false).getAsInt());
        this.addTask("Part two", this::partTwo);
    }
}