package com.dsh.mazegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dsh.mazegame.states.GameStateManager;

public class MazeGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private GameStateManager stateManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        stateManager = new GameStateManager();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        stateManager.update(delta);
        stateManager.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        stateManager.dispose();
    }
}
