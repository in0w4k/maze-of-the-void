package com.dsh.mazegame.trap;

import com.dsh.mazegame.difficulty.DifficultyStrategy;
import java.util.Random;

public class TrapFactory {
    private final Random random = new Random();
    private final DifficultyStrategy difficultyStrategy;

    public TrapFactory(DifficultyStrategy difficultyStrategy) {
        this.difficultyStrategy = difficultyStrategy;
    }

    public Trap createRandomTrap(int x, int y) {
        if (random.nextBoolean()) {
            return new SpikeTrap(x, y, difficultyStrategy);
        } else {
            return new PitTrap(x, y, difficultyStrategy);
        }
    }
}
