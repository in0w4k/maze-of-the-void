package com.dsh.mazegame.difficulty;

import com.badlogic.gdx.Input;

public class EasyDifficultyStrategy implements DifficultyStrategy {
    @Override
    public float getTrapDuration() {
        return 2f;
    }

    @Override
    public int getTrapsCount(int mazeWidth, int mazeHeight) {
        return (mazeWidth * mazeHeight) / 12;
    }

    @Override
    public int[] getAvailableQteKeys() {
        return new int[]{
            Input.Keys.NUM_1,
            Input.Keys.NUM_2,
            Input.Keys.NUM_3,
            Input.Keys.NUM_4
        };
    }

    @Override
    public String getName() {
        return "Easy";
    }
}


