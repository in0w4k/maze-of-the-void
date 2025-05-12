package com.dsh.mazegame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dsh.mazegame.utils.Leaderboard;

public class MenuState implements GameState {
    private final BitmapFont font;
    private final boolean isGameOver;
    private final GameStateManager stateManager;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private String playerName = "";
    private final BitmapFont leaderboardFont = new BitmapFont();
    private Music music;
    private Texture backgroundTexture;

    private int selected = 0;
    private final String[] menuItems = {
        "Start Game",
        "Name",
        "Leaderboard",
        "Settings",
        "Exit"
    };
    private boolean inLeaderboard = false;
    private boolean inSettings = false;

    private int mazeWidth = 21;
    private int mazeHeight = 21;
    private int mazeSizeIndex = 0;
    private final int[][] mazeSizes = {
        {21, 21},    // small
        {31, 31},  // medium
        {41, 41},  // large
        {51, 51}   // giant
    };
    private final String[] mazeSizeNames = {"Small (21x21)", "Medium (31x31)", "Large (41x41)", "Giant (51x51)"};

    private final boolean wasDeath;

    public MenuState(GameStateManager stateManager, boolean isGameOver) {
        this(stateManager, isGameOver, false);
    }

    public MenuState(GameStateManager stateManager, boolean isGameOver, boolean wasDeath) {
        this.stateManager = stateManager;
        this.isGameOver = isGameOver;
        this.wasDeath = wasDeath;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2);
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        backgroundTexture = new Texture("./core/assets/menu.png");

        music = Gdx.audio.newMusic(Gdx.files.internal("./core/assets/menu.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
    }

    @Override
    public void update(float delta) {
        camera.update();

        if (inLeaderboard) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                inLeaderboard = false;
            }
            return;
        }

        if (inSettings) {
            // --- Паттерн "Команда" для смены размера лабиринта ---
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                mazeSizeIndex = (mazeSizeIndex + mazeSizes.length - 1) % mazeSizes.length;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                mazeSizeIndex = (mazeSizeIndex + 1) % mazeSizes.length;
            }
            mazeWidth = mazeSizes[mazeSizeIndex][0];
            mazeHeight = mazeSizes[mazeSizeIndex][1];
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                inSettings = false;
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selected = (selected - 1 + menuItems.length) % menuItems.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selected = (selected + 1) % menuItems.length;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            switch (selected) {
                case 0: // Start Game
                    if (!playerName.isEmpty()) {
                        stateManager.setState(new PlayState(stateManager, playerName, mazeWidth, mazeHeight));
                    }
                    break;
                case 2: // Leaderboard
                    inLeaderboard = true;
                    break;
                case 3: // Settings
                    inSettings = true;
                    break;
                case 4: // Exit
                    Gdx.app.exit();
                    break;
            }
        }

        // Ввод имени только если выбран пункт "Name"
        if (selected == 1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
                playerName = playerName.substring(0, playerName.length() - 1);
            } else {
                for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
                    if (Gdx.input.isKeyJustPressed(key) && playerName.length() < 10) {
                        playerName += (char)('A' + (key - Input.Keys.A));
                    }
                }
                for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
                    if (Gdx.input.isKeyJustPressed(key) && playerName.length() < 10) {
                        playerName += (char)('0' + (key - Input.Keys.NUM_0));
                    }
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (inLeaderboard) {
            font.draw(batch, "Leaderboard (ESC to return):", Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() * 0.8f);
            String[] leaderboard = Leaderboard.getInstance().getTopScores();
            for (int i = 0; i < leaderboard.length; i++) {
                leaderboardFont.draw(batch, leaderboard[i], Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() * 0.7f - i * 30);
            }
            batch.end();
            return;
        }

        if (inSettings) {
            font.draw(batch, "Settings (ESC to return)", Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() * 0.8f);
            font.draw(batch, "Maze Size: " + mazeSizeNames[mazeSizeIndex] + " (LEFT/RIGHT)", Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() * 0.7f);
            batch.end();
            return;
        }

        String title;
        if (isGameOver) {
            title = "Congratulations! You completed the maze!";
        } else if (wasDeath) {
            title = "Game Over! You lost all health!";
        } else {
            title = "Maze Game";
        }
        font.draw(batch, title, Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() * 0.85f);

        float startY = Gdx.graphics.getHeight() * 0.7f;
        float stepY = 60f;

        for (int i = 0; i < menuItems.length; i++) {
            float y = startY - i * stepY;
            if (i == selected) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }

            if (i == 1) {
                String nameField = "Name: " + (selected == 1 ? playerName + "_" : playerName);
                font.draw(batch, nameField, Gdx.graphics.getWidth() / 2f - 300, y);
            } else {
                font.draw(batch, menuItems[i], Gdx.graphics.getWidth() / 2f - 300, y);
            }
        }
        font.setColor(Color.WHITE);

        batch.end();
    }

    @Override
    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
        leaderboardFont.dispose();
        if (music != null) {
            music.stop();
            music.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
