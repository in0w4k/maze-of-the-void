package com.dsh.mazegame.utils;

public class Stopwatch {
    private static Stopwatch instance;
    private long startTime;
    private long elapsedTime;
    private boolean running;

    private Stopwatch() {}

    public static Stopwatch getInstance() {
        if (instance == null) {
            instance = new Stopwatch();
        }
        return instance;
    }

    public void start() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    public void stop() {
        if (running) {
            elapsedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void reset() {
        elapsedTime = 0;
        running = false;
    }

    public float getElapsedTime() {
        if (running) {
            return (elapsedTime + System.currentTimeMillis() - startTime) / 1000f;
        }
        return elapsedTime / 1000f;
    }
}
