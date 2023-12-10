package dev.nicotopia;

import java.util.ArrayList;
import java.util.List;

public class GiantList<E> {
    private static int MIN_SUB_LIST_SIZE = 100000;
    private static int MAX_SUB_LIST_SIZE = 200000;

    private final List<List<E>> subLists = new ArrayList<>();
    private int size = 0;
    private int lastSearchSubListIdx;
    private int lastSearchIdxInSubList;

    public GiantList() {
        this.subLists.add(new ArrayList<>());
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public E get(int idx) {
        if (this.size <= idx) {
            throw new IndexOutOfBoundsException();
        }
        this.search(idx);
        return this.subLists.get(this.lastSearchSubListIdx).get(this.lastSearchIdxInSubList);
    }

    public void add(E element) {
        this.add(this.size, element);
    }

    public void add(int idx, E element) {
        if (idx < 0 || this.size < idx) {
            throw new IndexOutOfBoundsException();
        }
        this.search(idx);
        List<E> subList = this.subLists.get(this.lastSearchSubListIdx);
        subList.add(this.lastSearchIdxInSubList, element);
        ++this.size;
        this.checkSubList(this.lastSearchSubListIdx);
    }

    public E remove(int idx) {
        if (idx < 0 || this.size <= idx) {
            throw new IndexOutOfBoundsException();
        }
        this.search(idx);
        List<E> subList = this.subLists.get(this.lastSearchSubListIdx);
        E element = subList.remove(this.lastSearchIdxInSubList);
        --this.size;
        this.checkSubList(this.lastSearchSubListIdx);
        return element;
    }

    private void search(int idx) {
        int count = 0;
        this.lastSearchIdxInSubList = 0;
        this.lastSearchSubListIdx = 0;
        while (count < idx) {
            if (count + this.subLists.get(this.lastSearchSubListIdx).size() <= idx) {
                count += this.subLists.get(this.lastSearchSubListIdx++).size();
            } else {
                this.lastSearchIdxInSubList = idx - count;
                break;
            }
        }
        if (this.lastSearchSubListIdx == this.subLists.size()) {
            this.lastSearchIdxInSubList = this.subLists.get(--this.lastSearchSubListIdx).size();
        }
    }

    private void checkSubList(int subListIdx) {
        if (this.subLists.get(subListIdx).size() < MIN_SUB_LIST_SIZE && this.subLists.size() != 1) {
            int leftSize = 0 < subListIdx ? this.subLists.get(subListIdx - 1).size() : Integer.MAX_VALUE;
            int rightSize = subListIdx + 1 < this.subLists.size() ? this.subLists.get(subListIdx + 1).size()
                    : Integer.MAX_VALUE;
            if (leftSize < rightSize) {
                this.subLists.get(subListIdx).addAll(0, this.subLists.remove(subListIdx - 1));
            } else {
                this.subLists.get(subListIdx + 1).addAll(0, this.subLists.remove(subListIdx));
            }
            this.checkSubList(subListIdx);
        } else if (MAX_SUB_LIST_SIZE < this.subLists.get(subListIdx).size()) {
            List<E> oldList = this.subLists.remove(subListIdx);
            List<E> leftList = new ArrayList<>(oldList.subList(0, oldList.size() / 2));
            List<E> rightList = new ArrayList<>(oldList.subList(oldList.size() / 2, oldList.size()));
            this.subLists.add(subListIdx, rightList);
            this.subLists.add(subListIdx, leftList);
        }
    }
}