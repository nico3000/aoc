package dev.nicotopia.aoc2021;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Stack;

public class Day18 {
    private static final class Number {
        private Number parent;
        private Number left;
        private Number right;
        private int leafVal;

        public Number() {
        }

        public Number(Number left, Number right) {
            this.setChildren(left, right);
        }

        public Number(int leafVal) {
            this.leafVal = leafVal;
        }

        public Number(int leftLeafVal, int rightLeafVal) {
            this(new Number(leftLeafVal), new Number(rightLeafVal));
        }

        public void setChildren(Number left, Number right) {
            (this.left = left).parent = this;
            (this.right = right).parent = this;
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        public Number getLeftLeaf() {
            Number c = this;
            while (c.parent != null && c.parent.left == c) {
                c = c.parent;
            }
            c = c.parent != null ? c.parent.left : null;
            while (c != null && !c.isLeaf()) {
                c = c.right;
            }
            return c;
        }

        public Number getRightLeaf() {
            Number c = this;
            while (c.parent != null && c.parent.right == c) {
                c = c.parent;
            }
            c = c.parent != null ? c.parent.right : null;
            while (c != null && !c.isLeaf()) {
                c = c.left;
            }
            return c;
        }

        public void explode() {
            Number leftLeaf = this.getLeftLeaf();
            if (leftLeaf != null) {
                leftLeaf.leafVal += this.left.leafVal;
            }
            Number rightLeaf = this.getRightLeaf();
            if (rightLeaf != null) {
                rightLeaf.leafVal += this.right.leafVal;
            }
            this.parent.replaceChild(this, new Number(0));
        }

        public void split() {
            this.parent.replaceChild(this, new Number(this.leafVal / 2, this.leafVal / 2 + this.leafVal % 2));
        }

        public void replaceChild(Number oldChild, Number newChild) {
            if (this.left == oldChild) {
                (this.left = newChild).parent = this;
            } else {
                (this.right = newChild).parent = this;
            }
        }

        public boolean checkForExplosion(int depth) {
            if (depth < 4 && !this.isLeaf()) {
                return this.left.checkForExplosion(depth + 1) || this.right.checkForExplosion(depth + 1);
            } else if (depth == 4 && !this.isLeaf()) {
                this.explode();
                return true;
            } else {
                return false;
            }
        }

        public boolean checkForSplit() {
            if (!this.isLeaf()) {
                return this.left.checkForSplit() || this.right.checkForSplit();
            } else if (9 < this.leafVal) {
                this.split();
                return true;
            } else {
                return false;
            }
        }

        public void reduce() {
            while (this.checkForExplosion(0) || this.checkForSplit()) {
            }
        }

        public String toString() {
            return this.isLeaf() ? String.valueOf(this.leafVal) : String.format("[%s,%s]", this.left, this.right);
        }

        public int getMagnitude() {
            return this.isLeaf() ? this.leafVal : 3 * this.left.getMagnitude() + 2 * this.right.getMagnitude();
        }

        public Number copy() {
            return this.isLeaf() ? new Number(this.leafVal) : new Number(this.left.copy(), this.right.copy());
        }

        public static Number add(Number left, Number right) {
            Number n = new Number(left.copy(), right.copy());
            n.reduce();
            return n;
        }

        public static Number addInPlace(Number left, Number right) {
            Number n = new Number(left, right);
            n.reduce();
            return n;
        }
    }

    public static void main(String args[]) throws IOException {
        List<Number> numbers;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Day18.class.getResourceAsStream("/2021/day18.txt")))) {
            numbers = br.lines().map(Day18::parseNumber).toList();
        }
        long begin = System.nanoTime();
        Number sum = null;
        int max = 0;
        for (int i = 0; i < numbers.size(); ++i) {
            sum = i == 0 ? numbers.get(i) : Number.addInPlace(sum, numbers.get(i));
            for (int j = 0; j < numbers.size(); ++j) {
                if (i != j) {
                    max = Math.max(max, Number.add(numbers.get(i), numbers.get(j)).getMagnitude());
                }
            }
        }
        long end = System.nanoTime();
        System.out.printf("magnitudes: sum=%d, max=%d, time: %.3f ms\n", sum.getMagnitude(), max,
                1e-6f * (float) (end - begin));
    }

    private static Number parseNumber(String number) {
        Stack<Number> s = new Stack<>();
        for (byte c : number.getBytes()) {
            if (c == '[') {
                s.push(new Number());
            } else if (c == ']') {
                Number r = s.pop();
                Number l = s.pop();
                (s.peek().left = l).parent = s.peek();
                (s.peek().right = r).parent = s.peek();
            } else if (c != ',') {
                s.push(new Number(c - '0'));
            }
        }
        return s.pop();
    }
}