package com.dsh.mazegame.difficulty;

public class DifficultyStrategyFactory {
    private static DifficultyStrategyFactory instance;
    private final DifficultyStrategy[] strategies;
    private int currentStrategyIndex = 1; // По умолчанию Medium

    private DifficultyStrategyFactory() {
        strategies = new DifficultyStrategy[]{
            new EasyDifficultyStrategy(),
            new MediumDifficultyStrategy(),
            new HardDifficultyStrategy()
        };
    }

    public static DifficultyStrategyFactory getInstance() {
        if (instance == null) {
            instance = new DifficultyStrategyFactory();
        }
        return instance;
    }

    public DifficultyStrategy getCurrentStrategy() {
        return strategies[currentStrategyIndex];
    }

    public void nextStrategy() {
        currentStrategyIndex = (currentStrategyIndex + 1) % strategies.length;
    }

    public void previousStrategy() {
        currentStrategyIndex = (currentStrategyIndex + strategies.length - 1) % strategies.length;
    }

    public String getCurrentDifficultyName() {
        return strategies[currentStrategyIndex].getName();
    }
}
