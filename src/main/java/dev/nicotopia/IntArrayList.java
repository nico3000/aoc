package dev.nicotopia;

public class IntArrayList {
    private int data[] = new int[16];
    private int size = 0;

    public void add(int v) {
        this.ensureCapacity(this.size + 1);
        this.data[this.size++] = v;
    }

    public void add(int idx, int v) {
        this.ensureCapacity(this.size + 1);
        System.arraycopy(this.data, idx, this.data, idx + 1, this.size - idx);
        ++this.size;
        this.data[idx] = v;
    }

    public int remove(int idx) {
        int v = this.data[idx];
        System.arraycopy(this.data, idx + 1, this.data, idx, this.size - 1 - idx);
        --this.size;
        return v;
    }

    public int get(int idx) {
        return this.data[idx];
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private void ensureCapacity(int minSize) {
        int desiredCapacity = 2 * Integer.highestOneBit(minSize);
        if (this.data.length < desiredCapacity) {
            System.out.printf("Resize %d -> %d\n", this.data.length, desiredCapacity);
            int old[] = this.data;
            this.data = new int[desiredCapacity];
            System.arraycopy(old, 0, this.data, 0, this.size);
        }
    }
}
