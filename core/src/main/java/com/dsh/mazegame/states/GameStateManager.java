package com.dsh.mazegame.states;

public class GameStateManager {
    private GameState currentState;

    public GameStateManager() {
        currentState = new MenuState(this, false);
    }

    public void setState(GameState state) {
        if (currentState != null) {
            currentState.dispose();
        }
        currentState = state;
    }

    public void update(float delta) {
        currentState.update(delta);
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        currentState.render(batch);
    }

    public void dispose() {
        if (currentState != null) {
            currentState.dispose();
        }
    }
}
