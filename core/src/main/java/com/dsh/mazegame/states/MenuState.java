package com.dsh.mazegame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MenuState implements GameState {
    private final BitmapFont font;
    private final boolean isGameOver;
    private final GameStateManager stateManager;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    public MenuState(GameStateManager stateManager, boolean isGameOver) {
        this.stateManager = stateManager;
        this.isGameOver = isGameOver;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    @Override
    public void update(float delta) {
        camera.update();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            stateManager.setState(new PlayState(stateManager));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Устанавливаем цвет фона
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // Тёмно-серый цвет
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Отрисовка фона
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1); // Тёмно-серый цвет
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        // Отрисовка текста
        String title = isGameOver ? "Congratulations! You completed the maze!" : "Maze Game";
        String startText = "Press ENTER to start";
        String exitText = "Press ESC to exit";

        float titleY = Gdx.graphics.getHeight() * 0.7f;
        float startY = Gdx.graphics.getHeight() * 0.4f;
        float exitY = Gdx.graphics.getHeight() * 0.3f;

        font.draw(batch, title, Gdx.graphics.getWidth() / 2f - 300, titleY);
        font.draw(batch, startText, Gdx.graphics.getWidth() / 2f - 300, startY);
        font.draw(batch, exitText, Gdx.graphics.getWidth() / 2f - 300, exitY);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
