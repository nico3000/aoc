package dev.nicotopia.aoc;

public class Timer {
    private enum Status {
        STOPPED, STARTED, PAUSED
    }

    private Status status = Status.STOPPED;
    private long beginNanoTime;
    private long nanos = 0;

    public void start() {
        this.nanos = 0;
        this.beginNanoTime = System.nanoTime();
        this.status = Status.STARTED;
    }

    public void resume() {
        this.pause();
    }

    public void pause() {
        switch (this.status) {
            case STOPPED:
                break;
            case STARTED:
                this.status = Status.PAUSED;
                this.nanos += System.nanoTime() - this.beginNanoTime;
                break;
            case PAUSED:
                this.status = Status.STARTED;
                this.beginNanoTime = System.nanoTime();
                break;
        }
    }

    public long stop() {
        switch (this.status) {
            case STOPPED:
                this.nanos = 0;
                break;
            case STARTED:
                this.nanos += System.nanoTime() - this.beginNanoTime;
                break;
            case PAUSED:
                break;
        }
        this.status = Status.STOPPED;
        return this.nanos;
    }
}
