package dev.nicotopia.aoc2017;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day07 {
    private record TowerDesc(String name, int weight, List<String> subTowerNames) {
    }

    private static class Tower {
        private final String name;
        private final int weight;
        private final List<Tower> subTowers = new LinkedList<>();

        private Tower(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public int getRecursiveWeight() {
            return this.weight + this.subTowers.stream().mapToInt(Tower::getRecursiveWeight).sum();
        }

        public void depthSearch(Consumer<Tower> onVisit) {
            this.subTowers.forEach(st -> st.depthSearch(onVisit));
            onVisit.accept(this);
        }
    }

    public static void main(String[] args) throws IOException {
        List<TowerDesc> towerDescs;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day07.class.getResourceAsStream("/2017/day07.txt")))) {
            Pattern p = Pattern.compile("^([a-z]+)\\s+\\(([0-9]+)\\)(\\s+->\\s+(([a-z]+,\\s+)+[a-z]+))?$");
            towerDescs = br.lines().map(l -> p.matcher(l)).filter(Matcher::matches)
                    .map(m -> new TowerDesc(m.group(1), Integer.valueOf(m.group(2)),
                            m.group(4) == null ? Collections.emptyList() : Arrays.asList(m.group(4).split(", "))))
                    .toList();
        }
        Tower tower = buildTower(towerDescs);
        System.out.println("Root tower: " + tower.name);
        tower.depthSearch(t -> {
            int subTowerWeights[] = t.subTowers.stream().mapToInt(Tower::getRecursiveWeight).toArray();
            int distinct[] = Arrays.stream(subTowerWeights).distinct().toArray();
            if (distinct.length == 2) {
                int sum = Arrays.stream(subTowerWeights).sum();
                int wrongIndex = (subTowerWeights.length - 1) * distinct[0] + distinct[1] == sum ? 1 : 0;
                int delta = distinct[1 - wrongIndex] - distinct[wrongIndex];
                for (int i = 0; i < subTowerWeights.length; ++i) {
                    if (subTowerWeights[i] == distinct[wrongIndex]) {
                        System.out.printf("Program %s should weigh: %s\n", t.subTowers.get(i).name,
                                t.subTowers.get(i).weight + delta);
                    }
                }
            }
        });
    }

    public static Tower buildTower(List<TowerDesc> descs) {
        Map<String, Tower> towers = new HashMap<>();
        descs.forEach(d -> towers.put(d.name, new Tower(d.name, d.weight)));
        descs.forEach(d -> towers.get(d.name).subTowers
                .addAll(towers.values().stream().filter(t -> d.subTowerNames.contains(t.name)).toList()));
        List<Tower> roots = towers.values().stream()
                .filter(t -> descs.stream().noneMatch(d -> d.subTowerNames.contains(t.name))).toList();
        if (roots.size() != 1) {
            System.out.println("None or more than a single root!");
        }
        return roots.stream().findAny().orElse(null);
    }
}
