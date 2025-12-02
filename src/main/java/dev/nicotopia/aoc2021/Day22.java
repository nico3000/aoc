package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.aoc.algebra.Interval;

public class Day22 {
    private static record Region(int inclFromX, int exclToX, int inclFromY, int exclToY, int inclFromZ, int exclToZ) {
        public boolean contains(Region other) {
            return this.inclFromX <= other.inclFromX && other.exclToX <= this.exclToX
                    && this.inclFromY <= other.inclFromY && other.exclToY <= this.exclToY
                    && this.inclFromZ <= other.inclFromZ && other.exclToZ <= this.exclToZ;
        }

        public boolean intersects(Region other) {
            return Math.max(this.inclFromX, other.inclFromX) < Math.min(this.exclToX, other.exclToX)
                    && Math.max(this.inclFromY, other.inclFromY) < Math.min(this.exclToY, other.exclToY)
                    && Math.max(this.inclFromZ, other.inclFromZ) < Math.min(this.exclToZ, other.exclToZ);
        }

        public Region conjunct(Region other) {
            return new Region(Math.max(this.inclFromX, other.inclFromX), Math.min(this.exclToX, other.exclToX),
                    Math.max(this.inclFromY, other.inclFromY), Math.min(this.exclToY, other.exclToY),
                    Math.max(this.inclFromZ, other.inclFromZ), Math.min(this.exclToZ, other.exclToZ));
        }

        public List<Region> cut(Region other) {
            List<Region> r = new LinkedList<>();
            if (!this.intersects(other)) {
                r.add(this);
            } else if (!other.contains(this)) {
                Region o = this.conjunct(other);
                for (int z = 0; z < 3; ++z) {
                    Interval zInt = switch (z) {
                        case 0 -> new Interval(this.inclFromZ, o.inclFromZ);
                        case 1 -> new Interval(o.inclFromZ, o.exclToZ);
                        case 2 -> new Interval(o.exclToZ, this.exclToZ);
                        default -> throw new RuntimeException();
                    };
                    for (int y = 0; y < 3; ++y) {
                        Interval yInt = switch (y) {
                            case 0 -> new Interval(this.inclFromY, o.inclFromY);
                            case 1 -> new Interval(o.inclFromY, o.exclToY);
                            case 2 -> new Interval(o.exclToY, this.exclToY);
                            default -> throw new RuntimeException();
                        };
                        for (int x = 0; x < 3; ++x) {
                            Interval xInt = switch (x) {
                                case 0 -> new Interval(this.inclFromX, o.inclFromX);
                                case 1 -> new Interval(o.inclFromX, o.exclToX);
                                case 2 -> new Interval(o.exclToX, this.exclToX);
                                default -> throw new RuntimeException();
                            };
                            if (x != 1 || y != 1 || z != 1) {
                                r.add(new Region(xInt.beg(), xInt.end(), yInt.beg(), yInt.end(), zInt.beg(),
                                        zInt.end()));
                            }
                        }
                    }
                }
            }
            return Region.merge(r.stream().filter(Region::isValid).toList());
        }

        public boolean isValid() {
            return this.inclFromX < this.exclToX && this.inclFromY < this.exclToY && this.inclFromZ < this.exclToZ;
        }

        public Optional<Region> merge(Region other) {
            Optional<Region> merged = Optional.empty();
            if (this.inclFromX == other.inclFromX && this.exclToX == other.exclToX && this.inclFromY == other.inclFromY
                    && this.exclToY == other.exclToY) {
                if (this.exclToZ == other.inclFromZ) {
                    merged = Optional.of(new Region(this.inclFromX, this.exclToX, this.inclFromY, this.exclToY,
                            this.inclFromZ, other.exclToZ));
                } else if (this.inclFromZ == other.exclToZ) {
                    merged = Optional.of(new Region(this.inclFromX, this.exclToX, this.inclFromY, this.exclToY,
                            other.inclFromZ, this.exclToZ));
                }
            } else if (this.inclFromY == other.inclFromY && this.exclToY == other.exclToY
                    && this.inclFromZ == other.inclFromZ && this.exclToZ == other.exclToZ) {
                if (this.exclToX == other.inclFromX) {
                    merged = Optional.of(new Region(this.inclFromX, other.exclToX, this.inclFromY, this.exclToY,
                            this.inclFromZ, this.exclToZ));
                } else if (this.inclFromX == other.exclToX) {
                    merged = Optional.of(new Region(other.inclFromX, this.exclToX, this.inclFromY, this.exclToY,
                            this.inclFromZ, this.exclToZ));
                }
            } else if (this.inclFromX == other.inclFromX && this.exclToX == other.exclToX
                    && this.inclFromZ == other.inclFromZ && this.exclToZ == other.exclToZ) {
                if (this.exclToY == other.inclFromY) {
                    merged = Optional.of(new Region(this.inclFromX, this.exclToX, this.inclFromY, other.exclToY,
                            this.inclFromZ, this.exclToZ));
                } else if (this.inclFromY == other.exclToY) {
                    merged = Optional.of(new Region(this.inclFromX, this.exclToX, other.inclFromY, this.exclToY,
                            this.inclFromZ, this.exclToZ));
                }
            }
            return merged;
        }

        public static List<Region> merge(List<Region> toMerge) {
            if (toMerge.size() <= 1) {
                return toMerge;
            }
            List<Region> working;
            List<Region> temp = new LinkedList<>(toMerge);
            int before;
            do {
                before = temp.size();
                working = temp;
                temp = new LinkedList<>();
                for (int i = 0; i < working.size(); ++i) {
                    boolean mergeFound = false;
                    for (int j = i + 1; !mergeFound && j < working.size(); ++j) {
                        Optional<Region> mergedRegion = working.get(i).merge(working.get(j));
                        if (mergeFound = mergedRegion.isPresent()) {
                            temp.add(mergedRegion.get());
                            working.remove(j);
                        }
                    }
                    if (!mergeFound) {
                        temp.add(working.get(i));
                    }
                }
            } while (before != temp.size());
            return temp;
        }
    }

    private static record Step(Region region, int val) {
    }

    public static void main(String args[]) throws IOException {
        List<Step> steps;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day21.class.getResourceAsStream("/2021/day22.txt")))) {
            Pattern p = Pattern
                    .compile("(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)");
            steps = br.lines().map(p::matcher).filter(Matcher::matches)
                    .map(m -> new Step(new Region(Integer.valueOf(m.group(2)), Integer.valueOf(m.group(3)) + 1,
                            Integer.valueOf(m.group(4)), Integer.valueOf(m.group(5)) + 1, Integer.valueOf(m.group(6)),
                            Integer.valueOf(m.group(7)) + 1), m.group(1).equals("on") ? 1 : 0))
                    .toList();
        }

        long begin = System.nanoTime();
        long partTwo = getOnCount(steps);
        Region small = new Region(-50, 51, -50, 51, -50, 51);
        long partOne = getOnCount(steps.stream().map(s -> new Step(s.region.conjunct(small), s.val))
                .filter(s -> s.region.isValid()).toList());
        long end = System.nanoTime();
        System.out.printf("part one: %d, part two: %d, time: %.3f ms", partOne, partTwo, 1e-6f * (float) (end - begin));
    }

    private static long getOnCount(List<Step> steps) {
        List<Region> regions = Collections.emptyList();
        for (Step s : steps) {
            if (s.val == 1) {
                if (regions.isEmpty()) {
                    regions = Arrays.asList(s.region);
                } else {
                    List<Region> newRegions = Arrays.asList(s.region);
                    for (Region r : regions) {
                        newRegions = newRegions.stream().collect(LinkedList::new, (l, nr) -> l.addAll(nr.cut(r)),
                                LinkedList::addAll);
                    }
                    regions = new LinkedList<>(regions);
                    regions.addAll(newRegions);
                }
            } else {
                List<Region> newRegions = new LinkedList<>();
                for (Region r : regions) {
                    newRegions.addAll(r.cut(s.region));
                }
                regions = newRegions;
            }
        }
        long count = 0;
        for (Region r : regions) {
            count += (long) (r.exclToX - r.inclFromX) * (long) (r.exclToY - r.inclFromY)
                    * (long) (r.exclToZ - r.inclFromZ);
        }
        return count;
    }
}