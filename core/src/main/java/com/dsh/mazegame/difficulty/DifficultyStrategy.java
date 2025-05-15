package com.dsh.mazegame.difficulty;

public interface DifficultyStrategy {
    float getTrapDuration();
    int getTrapsCount(int mazeWidth, int mazeHeight);
    int[] getAvailableQteKeys();
    String getName();
}

