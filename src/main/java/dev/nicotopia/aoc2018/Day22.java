package dev.nicotopia.aoc2018;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import dev.nicotopia.aoc.DayBase;
import dev.nicotopia.aoc.algebra.Vec2i;
import dev.nicotopia.aoc.graphlib.AStar;
import dev.nicotopia.aoc.graphlib.AStarDataStructure;
import dev.nicotopia.aoc.graphlib.HashedAStarDataStructure;
import dev.nicotopia.aoc.graphlib.NodeDistancePair;

public class Day22 extends DayBase {
    private enum RegionType {
        ROCKY, WET, NARROW
    }

    private enum Equipment {
        CLIMBING_GEAR, TORCH, NEITHER
    }

    private record CaveNode(Vec2i pos, Equipment equipped) {
    }

    private int depth;
    private Vec2i target;
    private final Map<Vec2i, Integer> cave = new HashMap<>();
    private final Map<CaveNode, List<NodeDistancePair<CaveNode>>> neighbours = new HashMap<>();

    private NodeDistancePair<CaveNode> getNeighbour(CaveNode node, int idx) {
        List<NodeDistancePair<CaveNode>> nodeNeighbours = this.neighbours.get(node);
        if (nodeNeighbours == null) {
            this.neighbours.put(node, nodeNeighbours = this.buildNeighbourList(node));
        }
        return idx < nodeNeighbours.size() ? nodeNeighbours.get(idx) : null;
    }

    private List<NodeDistancePair<CaveNode>> buildNeighbourList(CaveNode node) {
        RegionType fromRegionType = this.getRegionType(node.pos);
        List<NodeDistancePair<CaveNode>> l = new ArrayList<>(8);
        if (node.pos.y() != 0) {
            this.addNeighbours(l, fromRegionType, node.equipped, new Vec2i(node.pos.x(), node.pos.y() - 1));
        }
        if (node.pos.x() != 0) {
            this.addNeighbours(l, fromRegionType, node.equipped, new Vec2i(node.pos.x() - 1, node.pos.y()));
        }
        this.addNeighbours(l, fromRegionType, node.equipped, new Vec2i(node.pos.x(), node.pos.y() + 1));
        this.addNeighbours(l, fromRegionType, node.equipped, new Vec2i(node.pos.x() + 1, node.pos.y()));
        return l;
    }

    private void addNeighbours(List<NodeDistancePair<CaveNode>> l, RegionType fromRegionType, Equipment fromEquipped,
            Vec2i neighbourPos) {
        BiPredicate<RegionType, Equipment> isAllowed = (rt, e) -> switch (rt) {
            case ROCKY -> e == Equipment.CLIMBING_GEAR || e == Equipment.TORCH;
            case WET -> e == Equipment.CLIMBING_GEAR || e == Equipment.NEITHER;
            case NARROW -> e == Equipment.TORCH || e == Equipment.NEITHER;
        };
        RegionType toRegionType = this.getRegionType(neighbourPos);
        for (Equipment e : Equipment.values()) {
            if (isAllowed.test(fromRegionType, e) && isAllowed.test(toRegionType, e)) {
                l.add(new NodeDistancePair<>(new CaveNode(neighbourPos, e), e != fromEquipped ? 8 : 1));
            }
        }
    }

    private void processInput() {
        this.depth = Integer.valueOf(this.getPrimaryPuzzleInput().getFirst().substring("depth: ".length()));
        String[] split = this.getPrimaryPuzzleInput().get(1).substring("target: ".length()).split(",");
        this.target = new Vec2i(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        this.cave.put(new Vec2i(0, 0), 0);
        this.cave.put(this.target, 0);
    }

    private int getErosionLevel(Vec2i pos) {
        return (this.getGeologicalIndex(pos) + this.depth) % 20183;
    }

    private int getGeologicalIndex(Vec2i pos) {
        Integer geoIdx = this.cave.get(pos);
        if (geoIdx != null) {
            return geoIdx;
        }
        geoIdx = pos.y() == 0 ? pos.x() * 16807
                : pos.x() == 0 ? pos.y() * 48271
                        : this.getErosionLevel(new Vec2i(pos.x() - 1, pos.y()))
                                * this.getErosionLevel(new Vec2i(pos.x(), pos.y() - 1));
        this.cave.put(pos, geoIdx);
        return geoIdx;
    }

    private RegionType getRegionType(Vec2i pos) {
        return RegionType.values()[this.getErosionLevel(pos) % 3];
    }

    private int partOne() {
        return Vec2i.streamFromRectangle(0, 0, this.target.x() + 1, this.target.y() + 1)
                .mapToInt(p -> this.getErosionLevel(p) % 3).sum();
    }

    private int partTwo() {
        CaveNode start = new CaveNode(new Vec2i(0, 0), Equipment.TORCH);
        AStarDataStructure<CaveNode> asds = new HashedAStarDataStructure<>(n -> n.pos.manhattanDistanceTo(this.target),
                t -> t.pos.equals(this.target) && t.equipped == Equipment.TORCH);
        NodeDistancePair<CaveNode> result = AStar.run(this::getNeighbour, start, asds);
        return result.node().equipped != Equipment.TORCH ? result.distance() + 7 : result.distance();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}