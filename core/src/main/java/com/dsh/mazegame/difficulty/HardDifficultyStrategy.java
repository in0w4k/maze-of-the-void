package com.dsh.mazegame.difficulty;

import com.badlogic.gdx.Input;

public class HardDifficultyStrategy implements DifficultyStrategy {
    @Override
    public float getTrapDuration() {
        return 1f;
    }

    @Override
    public int getTrapsCount(int mazeWidth, int mazeHeight) {
        return (mazeWidth * mazeHeight) / 4;
    }

    @Override
    public int[] getAvailableQteKeys() {
        return new int[]{
            Input.Keys.NUM_1,
            Input.Keys.NUM_2,
            Input.Keys.NUM_3,
            Input.Keys.NUM_4,
            Input.Keys.UP,
            Input.Keys.DOWN,
            Input.Keys.LEFT,
            Input.Keys.RIGHT
        };
    }

    @Override
    public String getName() {
        return "Hard";
    }
}
