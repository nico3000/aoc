package dev.nicotopia.aoc2015;

import java.util.Stack;

public class Day22 {
    private record Effect(int startTurn, int endTurn, int armorDelta, int bossDamage, int manaDelta) {
        public boolean isActive(int turn) {
            return this.startTurn <= turn && turn < this.endTurn;
        }
    }

    public static void main(String[] args) {
        nextTurn(0, 50, 500, 0, 51, 9, new Stack<>(), 0);
        System.out.println("Part one: " + minManaSpent);
        minManaSpent = Integer.MAX_VALUE;
        nextTurn(0, 50, 500, 0, 51, 9, new Stack<>(), -1);
        System.out.println("Part two: " + minManaSpent);
    }

    private static int minManaSpent = Integer.MAX_VALUE;

    private static void nextTurn(int turn, int playerHealth, int mana, int manaSpent, int bossHealth, int bossDamage,
            Stack<Effect> effects, int playerHealthDeltaPerPlayerTurn) {
        if (minManaSpent <= manaSpent) {
            return;
        }
        int playerArmor = 0;
        for (Effect effect : effects.stream().filter(e -> e.isActive(turn)).toList()) {
            playerArmor += effect.armorDelta;
            bossHealth -= effect.bossDamage;
            mana += effect.manaDelta;
        }
        if (bossHealth <= 0) {
            // win
            minManaSpent = manaSpent;
            System.out.println("New min mana spent value: " + manaSpent);
        }
        if (turn % 2 == 1) {
            playerHealth -= Math.max(1, bossDamage - playerArmor);
            if (0 < playerHealth) {
                nextTurn(turn + 1, playerHealth, mana, manaSpent, bossHealth, bossDamage, effects,
                        playerHealthDeltaPerPlayerTurn);
            }
        } else {
            playerHealth += playerHealthDeltaPerPlayerTurn;
            if (playerHealth <= 0) {
                return;
            }
            if (53 <= mana) {
                // magic missile
                nextTurn(turn + 1, playerHealth, mana - 53, manaSpent + 53, bossHealth - 4, bossDamage,
                        effects, playerHealthDeltaPerPlayerTurn);
            }
            if (73 <= mana) {
                // drain
                nextTurn(turn + 1, playerHealth + 2, mana - 73, manaSpent + 73, bossHealth - 2, bossDamage,
                        effects, playerHealthDeltaPerPlayerTurn);
            }
            if (113 <= mana && effects.stream().noneMatch(e -> e.isActive(turn + 1) && e.armorDelta != 0)) {
                // shield
                effects.push(new Effect(turn + 1, turn + 7, 7, 0, 0));
                nextTurn(turn + 1, playerHealth, mana - 113, manaSpent + 113, bossHealth, bossDamage, effects,
                        playerHealthDeltaPerPlayerTurn);
                effects.pop();
            }
            if (173 <= mana && effects.stream().noneMatch(e -> e.isActive(turn + 1) && e.bossDamage != 0)) {
                // poison
                effects.push(new Effect(turn + 1, turn + 7, 0, 3, 0));
                nextTurn(turn + 1, playerHealth, mana - 173, manaSpent + 173, bossHealth, bossDamage, effects,
                        playerHealthDeltaPerPlayerTurn);
                effects.pop();
            }
            if (229 <= mana && effects.stream().noneMatch(e -> e.isActive(turn + 1) && e.manaDelta != 0)) {
                // recharge
                effects.push(new Effect(turn + 1, turn + 6, 0, 0, 101));
                nextTurn(turn + 1, playerHealth, mana - 229, manaSpent + 229, bossHealth, bossDamage, effects,
                        playerHealthDeltaPerPlayerTurn);
                effects.pop();
            }
        }
    }
}