package com.dsh.mazegame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Music;
import com.dsh.mazegame.MazeGenerator;
import com.dsh.mazegame.Player;
import com.dsh.mazegame.difficulty.DifficultyStrategy;
import com.dsh.mazegame.settings.InputSettings;
import com.dsh.mazegame.utils.Leaderboard;
import com.dsh.mazegame.utils.Stopwatch;
import com.dsh.mazegame.HealthBar;
import com.dsh.mazegame.trap.Trap;
import com.dsh.mazegame.trap.PitTrap;
import com.dsh.mazegame.trap.SpikeTrap;

public class PlayState implements GameState {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthographicCamera uiCamera;
    private final Texture floorTexture, wallTexture, startTexture, endTexture;
    private final Animation<TextureRegion> maskAnimation;
    private float animationTime;
    private final MazeGenerator maze;
    private final Player player;
    private final GameStateManager stateManager;
    private final String playerName;
    private final HealthBar healthBar;

    private final Music music;
    private boolean showFullMaze = false;
    private int lastTrapCellX = -1, lastTrapCellY = -1;
    private final BitmapFont font = new BitmapFont();
    private final com.badlogic.gdx.math.Vector3 tmpVec3 = new com.badlogic.gdx.math.Vector3();
    private PitTrap qtePit = null;

    public PlayState(GameStateManager stateManager, String playerName, int mazeWidth, int mazeHeight, DifficultyStrategy difficultyStrategy) {
        this.stateManager = stateManager;
        this.playerName = playerName;
        Stopwatch.getInstance().reset();
        Stopwatch.getInstance().start();

        // --- Загрузка текстур ---
        floorTexture = new Texture("./core/assets/floor.png");
        wallTexture = new Texture("./core/assets/wall.png");
        startTexture = new Texture("./core/assets/start.png");
        endTexture = new Texture("./core/assets/end.png");

        // --- Загрузка текстур для маски ---
        Texture maskTexture1 = new Texture("./core/assets/mask1.png");
        Texture maskTexture2 = new Texture("./core/assets/mask2.png");
        Texture maskTexture3 = new Texture("./core/assets/mask3.png");
        Texture maskTexture4 = new Texture("./core/assets/mask4.png");

        // --- Создание анимации ---
        maskAnimation = new Animation<>(0.5f,
            new TextureRegion(maskTexture1),
            new TextureRegion(maskTexture2),
            new TextureRegion(maskTexture3),
            new TextureRegion(maskTexture4)
        );
        maskAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animationTime = 0f;

        maze = new MazeGenerator(mazeWidth, mazeHeight, difficultyStrategy);

        // --- Создание камеры ---
        camera = new OrthographicCamera();
        viewport = new FitViewport(3 * MazeGenerator.TILE_SIZE, 3 * MazeGenerator.TILE_SIZE, camera); // Размер области видимости камеры
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // --- UI-камера для интерфейса ---
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, viewport.getScreenWidth(), viewport.getScreenHeight());
        uiCamera.update();

        // --- Создание игрока ---
        player = new Player(maze, maze.startX * MazeGenerator.TILE_SIZE, maze.startY * MazeGenerator.TILE_SIZE);

        // --- HealthBar ---
        healthBar = new HealthBar(player.getMaxHealth());
        player.addHealthListener(healthBar);

        // --- Музыка ---
        music = Gdx.audio.newMusic(Gdx.files.internal("./core/assets/sound.wav"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        updateCameraPosition();
    }

    @Override
    public void update(float delta) {
        float playerCenterX = player.getX() + Player.PLAYER_SIZE / 2f;
        float playerCenterY = player.getY() + Player.PLAYER_SIZE / 2f;
        int playerCellX = (int) (playerCenterX / MazeGenerator.TILE_SIZE);
        int playerCellY = (int) (playerCenterY / MazeGenerator.TILE_SIZE);

        PitTrap currentPit = null;
        Trap currentOtherTrap = null;

        // --- Один проход по ловушкам ---
        for (Trap trap : maze.traps) {
            if (trap instanceof SpikeTrap) {
                ((SpikeTrap) trap).update(delta, player);
            }
            if (trap instanceof PitTrap) {
                PitTrap pit = (PitTrap) trap;
                if (pit.getX() == playerCellX && pit.getY() == playerCellY) {
                    currentPit = pit;
                } else {
                    pit.resetIfPlayerLeft(playerCellX, playerCellY);
                }
            } else {
                if (trap.getX() == playerCellX && trap.getY() == playerCellY) {
                    currentOtherTrap = trap;
                }
            }
        }

        boolean pitBlocking = false;
        qtePit = null;
        if (currentPit != null) {
            currentPit.onStep(player);
            if (currentPit.isBlocking()) {
                pitBlocking = true;
                if (currentPit.isQteActive()) {
                    qtePit = currentPit;
                }
            }
        }

        if (!pitBlocking) {
            player.handleInput(delta);
            player.update(delta);
            updateCameraPosition();
        } else {
            updateCameraPosition();
        }

        if (currentOtherTrap != null) {
            if (lastTrapCellX != playerCellX || lastTrapCellY != playerCellY) {
                currentOtherTrap.onStep(player);
                lastTrapCellX = playerCellX;
                lastTrapCellY = playerCellY;
            }
        } else {
            lastTrapCellX = -1;
            lastTrapCellY = -1;
        }

        // --- Проверка смерти игрока ---
        if (player.getHealth() <= 0) {
            stateManager.setState(new MenuState(stateManager, false, true));
            return;
        }

        if (isPlayerAtFinish()) {
            Stopwatch.getInstance().stop();
            float time = Stopwatch.getInstance().getElapsedTime();
            Leaderboard.getInstance().addScore(playerName, time);
            stateManager.setState(new MenuState(stateManager, true, false));
        }

        InputSettings settings = InputSettings.getInstance();
        if (Gdx.input.isKeyJustPressed(settings.getShowMap())) {
            showFullMaze = !showFullMaze;
            if (showFullMaze) {
                camera.position.set(
                    (maze.width * MazeGenerator.TILE_SIZE) / 2f,
                    (maze.height * MazeGenerator.TILE_SIZE) / 2f,
                    0
                );
                camera.viewportWidth = maze.width * MazeGenerator.TILE_SIZE;
                camera.viewportHeight = maze.height * MazeGenerator.TILE_SIZE;
                camera.update();
            } else {
                viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                updateCameraPosition();
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        animationTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentMaskFrame = maskAnimation.getKeyFrame(animationTime);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // --- Отрисовка лабиринта ---
        for (int x = 0; x < maze.width; x++) {
            for (int y = 0; y < maze.height; y++) {
                float drawX = x * MazeGenerator.TILE_SIZE;
                float drawY = y * MazeGenerator.TILE_SIZE;

                if (x == maze.startX && y == maze.startY) {
                    batch.draw(startTexture, drawX, drawY, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
                } else if (x == maze.endX && y == maze.endY) {
                    batch.draw(endTexture, drawX, drawY, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
                } else if (maze.maze[x][y] == MazeGenerator.WALL) {
                    batch.draw(wallTexture, drawX, drawY, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
                } else {
                    batch.draw(floorTexture, drawX, drawY, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
                }
            }
        }

        for (Trap trap : maze.traps) {
            trap.render(batch);
        }

        player.render(batch);

        if (!showFullMaze) {
            float maskWidth = viewport.getWorldWidth();
            float maskHeight = viewport.getWorldHeight();
            float maskX = camera.position.x - maskWidth / 2;
            float maskY = camera.position.y - maskHeight / 2;
            batch.draw(currentMaskFrame, maskX, maskY, maskWidth, maskHeight);
        }

        batch.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        healthBar.render(batch);

        if (qtePit != null) {
            font.getData().setScale(2.5f);
            String keyName = com.badlogic.gdx.Input.Keys.toString(qtePit.getQteKey());

            float qteUiX = uiCamera.viewportWidth / 2f - 60;
            float qteUiY = uiCamera.viewportHeight - 80;

            font.draw(batch, "Press: " + keyName, qteUiX, qteUiY);
            font.getData().setScale(1f);
        }

        batch.end();
    }

    private boolean isPlayerAtFinish() {
        int playerCellX = (int) (player.getX() / MazeGenerator.TILE_SIZE);
        int playerCellY = (int) (player.getY() / MazeGenerator.TILE_SIZE);
        return playerCellX == maze.endX && playerCellY == maze.endY;
    }

    private void updateCameraPosition() {
        float playerCenterX = player.getX() + Player.PLAYER_SIZE / 2f;
        float playerCenterY = player.getY() + Player.PLAYER_SIZE / 2f;

        float mazePixelWidth = maze.width * MazeGenerator.TILE_SIZE;
        float mazePixelHeight = maze.height * MazeGenerator.TILE_SIZE;

        float cameraHalfWidth = camera.viewportWidth / 2;
        float cameraHalfHeight = camera.viewportHeight / 2;

        float cameraX = Math.max(cameraHalfWidth, Math.min(playerCenterX, mazePixelWidth - cameraHalfWidth));
        float cameraY = Math.max(cameraHalfHeight, Math.min(playerCenterY, mazePixelHeight - cameraHalfHeight));

        if (camera.position.x != cameraX || camera.position.y != cameraY) {
            camera.position.set(cameraX, cameraY, 0);
            camera.update();
        }
    }

    @Override
    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        startTexture.dispose();
        endTexture.dispose();
        player.dispose();
        for (TextureRegion region : maskAnimation.getKeyFrames()) {
            region.getTexture().dispose();
        }
        healthBar.dispose();
        if (music != null) {
            music.stop();
            music.dispose();
        }
        font.dispose();
    }
}
