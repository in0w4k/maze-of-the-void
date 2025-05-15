package com.dsh.mazegame.difficulty;

import com.badlogic.gdx.Input;

public class MediumDifficultyStrategy implements DifficultyStrategy {
    @Override
    public float getTrapDuration() {
        return 1.5f;
    }

    @Override
    public int getTrapsCount(int mazeWidth, int mazeHeight) {
        return (mazeWidth * mazeHeight) / 8;
    }

    @Override
    public int[] getAvailableQteKeys() {
        return new int[]{
            Input.Keys.UP,
            Input.Keys.DOWN,
            Input.Keys.LEFT,
            Input.Keys.RIGHT,
            Input.Keys.SPACE,
            Input.Keys.R
        };
    }

    @Override
    public String getName() {
        return "Medium";
    }
}
