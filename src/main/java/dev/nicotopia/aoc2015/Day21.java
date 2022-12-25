package dev.nicotopia.aoc2015;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class Day21 {
    private record Item(String name, int cost, int damage, int armor) {
    }

    private static final List<Item> weapons = Arrays.asList(
            new Item("Dagger", 8, 4, 0),
            new Item("Shortsword", 10, 5, 0),
            new Item("Warhammer", 25, 6, 0),
            new Item("Longsword", 40, 7, 0),
            new Item("Greataxe", 74, 8, 0));
    private static final List<Item> armors = Arrays.asList(
            new Item("nothing", 0, 0, 0),
            new Item("Leather", 13, 0, 1),
            new Item("Chainmail", 31, 0, 2),
            new Item("Splintmail", 53, 0, 3),
            new Item("Bandedmail", 75, 0, 4),
            new Item("Platemail", 102, 0, 5));
    private static final List<Item> rings = Arrays.asList(
            new Item("nothing1", 0, 0, 0),
            new Item("nothing2", 0, 0, 0),
            new Item("Damage +1", 25, 1, 0),
            new Item("Damage +2", 50, 2, 0),
            new Item("Damage +3", 100, 3, 0),
            new Item("Defense +1", 20, 0, 1),
            new Item("Defense +2", 40, 0, 2),
            new Item("Defense +3", 80, 0, 3));

    public static void main(String[] args) {
        find(0, new Stack<>(), 0);
        System.out.printf("Part one: %d\nPart two: %d\n", minCostWin, maxCostLose);
    }

    private static final int bossHp = 103;
    private static final int bossDmg = 9;
    private static final int bossArmor = 2;
    private static int minCostWin = Integer.MAX_VALUE;
    private static int maxCostLose = 0;

    private static void find(int shopIdx, Stack<Item> currentSet, int setCost) {
        fight(currentSet, setCost);
        List<Item> shop = switch (shopIdx) {
            case 0 -> weapons;
            case 1 -> armors;
            default -> null;
        };
        if (shop != null) {
            shop.stream().forEach(item -> {
                currentSet.push(item);
                find(shopIdx + 1, currentSet, setCost + item.cost);
                currentSet.pop();
            });
        } else if (shopIdx == 2) {
            rings.stream().forEach(r0 -> {
                currentSet.push(r0);
                rings.stream().filter(r1 -> r1 != r0).forEach(r1 -> {
                    currentSet.push(r1);
                    find(shopIdx + 1, currentSet, setCost + r0.cost + r1.cost);
                    currentSet.pop();
                });
                currentSet.pop();
            });
        }
    }

    private static boolean fight(Collection<Item> currentSet, int setCost) {
        int playerDmg = currentSet.stream().mapToInt(Item::damage).sum();
        int playerArmor = currentSet.stream().mapToInt(Item::armor).sum();
        int playerHp = 100;
        int playerRounds = (int) Math.ceil((float) bossHp / (float) Math.max(1, playerDmg - bossArmor));
        int bossRounds = (int) Math.ceil((float) playerHp / (float) Math.max(1, bossDmg - playerArmor));
        if (playerRounds <= bossRounds) {
            minCostWin = Math.min(minCostWin, setCost);
            return true;
        } else {
            maxCostLose = Math.max(maxCostLose, setCost);
            return false;
        }
    }
}