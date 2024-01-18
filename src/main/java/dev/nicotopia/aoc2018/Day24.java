package dev.nicotopia.aoc2018;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.nicotopia.Util;
import dev.nicotopia.aoc.AocException;
import dev.nicotopia.aoc.DayBase;

public class Day24 extends DayBase {
    private enum Faction {
        IMMUNE_SYSTEM, INFECTION
    }

    private final List<Group> originalGroups = new ArrayList<>();
    private final List<Group> remaining = new ArrayList<>();
    private int boost = 0;

    private class Group {
        private final Set<String> immunities = new HashSet<>();
        private final Set<String> weaknesses = new HashSet<>();
        private final Faction faction;
        private final int initiative;
        private final String damageType;
        private final int attackDamage;
        private final int numHitPoints;
        private final int numUnitsAtCombatStart;
        private int numUnits;

        public Group(Faction faction, String desc) {
            this.faction = faction;
            Matcher m = Pattern.compile(
                    "(\\d+) units each with (\\d+) hit points (?:\\((.*)\\) )?with an attack that does (\\d+) ([a-z]+) damage at initiative (\\d+)")
                    .matcher(desc);
            if (!m.matches()) {
                throw new AocException("Not a valid group description: %s", desc);
            }
            this.initiative = Integer.valueOf(m.group(6));
            this.damageType = m.group(5);
            this.attackDamage = Integer.valueOf(m.group(4));
            this.numHitPoints = Integer.valueOf(m.group(2));
            this.numUnitsAtCombatStart = Integer.valueOf(m.group(1));
            if (m.group(3) != null) {
                Matcher m1 = Pattern.compile("((?:weak)|(?:immune)) to ([a-z]+(:?, [a-z]+)*)").matcher(m.group(3));
                while (m1.find()) {
                    (m1.group(1).equals("weak") ? this.weaknesses : this.immunities)
                            .addAll(Arrays.asList(m1.group(2).split(", ")));
                }
            }
        }

        public int getEffectivePower() {
            return this.numUnits * ((this.faction == Faction.IMMUNE_SYSTEM ? Day24.this.boost : 0) + this.attackDamage);
        }

        public int getDamageDealtTo(Group target) {
            return target.immunities.contains(this.damageType) ? 0
                    : this.getEffectivePower() * (target.weaknesses.contains(this.damageType) ? 2 : 1);
        }

        public boolean attack(Group target) {
            int before = target.numUnits;
            target.numUnits = Math.max(0, target.numUnits - this.getDamageDealtTo(target) / target.numHitPoints);
            return before != target.numUnits;
        }
    }

    private void processInput() {
        Faction faction = null;
        for (String line : this.getPrimaryPuzzleInput()) {
            if (line.equals("Immune System:")) {
                faction = Faction.IMMUNE_SYSTEM;
            } else if (line.equals("Infection:")) {
                faction = Faction.INFECTION;
            } else if (!line.isEmpty()) {
                this.originalGroups.add(new Group(faction, line));
            }
        }
    }

    private Map<Group, Group> targetSelection() {
        this.remaining.sort(
                (a, b) -> a.getEffectivePower() == b.getEffectivePower() ? Integer.compare(b.initiative, a.initiative)
                        : Integer.compare(b.getEffectivePower(), a.getEffectivePower()));
        Set<Group> validTargets = new HashSet<>(this.remaining);
        Map<Group, Group> selectedTargets = new HashMap<>();
        for (Group attacker : this.remaining) {
            Optional<Group> selected = validTargets.stream().filter(defender -> defender.faction != attacker.faction)
                    .max((a, b) -> {
                        int dmgToA = attacker.getDamageDealtTo(a);
                        int dmgToB = attacker.getDamageDealtTo(b);
                        if (dmgToA != dmgToB) {
                            return Integer.compare(dmgToA, dmgToB);
                        } else if (a.getEffectivePower() != b.getEffectivePower()) {
                            return Integer.compare(a.getEffectivePower(), b.getEffectivePower());
                        } else {
                            return Integer.compare(a.initiative, b.initiative);
                        }
                    });
            if (selected.isPresent()) {
                validTargets.remove(selected.get());
                selectedTargets.put(attacker, selected.get());
            }
        }
        return selectedTargets;
    }

    private boolean fight() {
        var targetSelection = this.targetSelection();
        long numChanges = targetSelection.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey().initiative, a.getKey().initiative))
                .filter(e -> e.getKey().numUnits != 0 && e.getKey().attack(e.getValue())).count();
        remaining.removeIf(g -> g.numUnits == 0);
        return numChanges != 0;
    }

    private Faction getCombatWinner() {
        this.remaining.clear();
        this.remaining.addAll(this.originalGroups);
        this.remaining.forEach(g -> g.numUnits = g.numUnitsAtCombatStart);
        while (this.remaining.stream().map(g -> g.faction).distinct().count() == 2) {
            if (!this.fight()) {
                return null;
            }
        }
        return this.remaining.isEmpty() ? null : this.remaining.getFirst().faction;
    }

    private int partOne() {
        this.getCombatWinner();
        return this.remaining.stream().mapToInt(g -> g.numUnits).sum();
    }

    private int partTwo() {
        this.boost = (int) Util.binarySearch(v -> {
            this.boost = (int) v;
            return this.getCombatWinner() != Faction.IMMUNE_SYSTEM;
        });
        this.getCombatWinner();
        return this.remaining.stream().mapToInt(g -> g.numUnits).sum();
    }

    @Override
    public void run() {
        this.addDefaultExamplePresets();
        this.addTask("Process input", this::processInput);
        this.addTask("Part one", this::partOne);
        this.addTask("Part two", this::partTwo);
    }
}
