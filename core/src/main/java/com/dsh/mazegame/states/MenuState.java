package com.dsh.mazegame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dsh.mazegame.difficulty.DifficultyStrategyFactory;
import com.dsh.mazegame.utils.Leaderboard;

public class MenuState implements GameState {
    private final BitmapFont font;
    private final boolean isGameOver;
    private final GameStateManager stateManager;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private String playerName = "";
    private final BitmapFont leaderboardFont = new BitmapFont();
    private final Music music;
    private Animation<TextureRegion> backgroundAnimation;
    private float animationTime = 0f;
    private final DifficultyStrategyFactory difficultyFactory = DifficultyStrategyFactory.getInstance();


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
    private boolean inKeyBindings = false;
    private int selectedKeyBinding = 0;
    private final String[] keyBindings = {
        "Move Up",
        "Move Down",
        "Move Left",
        "Move Right",
        "Sprint",
        "Show Map"
    };
    private boolean waitingForKey = false;

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

        loadBackgroundAnimation();

        music = Gdx.audio.newMusic(Gdx.files.internal("./core/assets/menu.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
    }

    private void loadBackgroundAnimation() {
        Texture[] backgroundFrames = new Texture[8];
        for (int i = 0; i < 8; i++) {
            backgroundFrames[i] = new Texture("./core/assets/menu-export" + (i + 1) + ".png");
        }

        TextureRegion[] backgroundRegions = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            backgroundRegions[i] = new TextureRegion(backgroundFrames[i]);
        }

        backgroundAnimation = new Animation<>(0.5f, backgroundRegions);
        backgroundAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float delta) {
        camera.update();

        animationTime += delta;

        if (inLeaderboard) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                inLeaderboard = false;
            }
            return;
        }

        if (inSettings) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                mazeSizeIndex = (mazeSizeIndex + mazeSizes.length - 1) % mazeSizes.length;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                mazeSizeIndex = (mazeSizeIndex + 1) % mazeSizes.length;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                difficultyFactory.previousStrategy();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                difficultyFactory.nextStrategy();
            }

            mazeWidth = mazeSizes[mazeSizeIndex][0];
            mazeHeight = mazeSizes[mazeSizeIndex][1];
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                inSettings = false;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                inKeyBindings = true;
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
                        stateManager.setState(new PlayState(
                            stateManager,
                            playerName,
                            mazeWidth,
                            mazeHeight,
                            difficultyFactory.getCurrentStrategy()
                        ));
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

        // Name
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
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.0f;
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.begin();

        TextureRegion currentFrame = backgroundAnimation.getKeyFrame(animationTime);

        batch.draw(currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float menuWidth = 600f;
        float menuX = (screenWidth - menuWidth) / 2;
        float titleY = screenHeight * 0.85f;
        float startY = screenHeight * 0.7f;
        float stepY = screenHeight * 0.05f;

        if (inLeaderboard) {
            font.draw(batch, "Leaderboard (ESC to return):", menuX, titleY);
            String[] leaderboard = Leaderboard.getInstance().getTopScores();
            for (int i = 0; i < leaderboard.length; i++) {
                leaderboardFont.draw(batch, leaderboard[i], menuX, startY - i * stepY);
            }
            batch.end();
            return;
        }

        if (inKeyBindings) {
            font.draw(batch, "Key Bindings (ESC to return)", menuX, titleY);
            font.setColor(Color.WHITE);
            font.draw(batch, "CTRL+Z to undo last change", menuX, titleY - stepY);


            batch.end();
            return;
        }

        if (inSettings) {
            font.draw(batch, "Settings (ESC to return)", menuX, titleY);
            font.draw(batch, "Maze Size: " + mazeSizeNames[mazeSizeIndex] + " (LEFT/RIGHT)", menuX, startY);
            font.draw(batch, "Difficulty: " + difficultyFactory.getCurrentDifficultyName() + " (A/D)", menuX, startY - stepY);
            font.draw(batch, "Key Bindings (ENTER)", menuX, startY - stepY * 2);
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
        font.draw(batch, title, menuX, titleY);

        for (int i = 0; i < menuItems.length; i++) {
            float y = startY - i * stepY;
            if (i == selected) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }

            if (i == 1) {
                String nameField = "Name: " + (selected == 1 ? playerName + "_" : playerName);
                font.draw(batch, nameField, menuX, y);
            } else {
                font.draw(batch, menuItems[i], menuX, y);
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
        if (backgroundAnimation != null) {
            for (TextureRegion region : backgroundAnimation.getKeyFrames()) {
                if (region != null && region.getTexture() != null) {
                    region.getTexture().dispose();
                }
            }
        }
    }
}
