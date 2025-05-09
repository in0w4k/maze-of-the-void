package com.dsh.mazegame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dsh.mazegame.MazeGenerator;
import com.dsh.mazegame.Player;

public class PlayState implements GameState {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Texture floorTexture, wallTexture, startTexture, endTexture;
    private final Animation<TextureRegion> maskAnimation;
    private float animationTime;
    private final MazeGenerator maze;
    private final Player player;
    private final GameStateManager stateManager;

    public PlayState(GameStateManager stateManager) {
        this.stateManager = stateManager;
        batch = new SpriteBatch();

        // Загружаем текстуры
        floorTexture = new Texture("./core/assets/floor.png");
        wallTexture = new Texture("./core/assets/wall.png");
        startTexture = new Texture("./core/assets/start.png");
        endTexture = new Texture("./core/assets/end.png");

        // Загрузка текстур для анимации маски
        Texture maskTexture1 = new Texture("./core/assets/mask1.png");
        Texture maskTexture2 = new Texture("./core/assets/mask2.png");
        Texture maskTexture3 = new Texture("./core/assets/mask3.png");
        Texture maskTexture4 = new Texture("./core/assets/mask4.png");

        // Создание анимации
        maskAnimation = new Animation<>(0.5f,
            new TextureRegion(maskTexture1),
            new TextureRegion(maskTexture2),
            new TextureRegion(maskTexture3),
            new TextureRegion(maskTexture4)
        );
        maskAnimation.setPlayMode(Animation.PlayMode.LOOP);

        animationTime = 0f;

        // Создаем лабиринт
        maze = new MazeGenerator(21,21);

        // Создаем камеру и viewport с фиксированным размером, чтобы следовать за игроком
        camera = new OrthographicCamera();
        viewport = new FitViewport(3 * MazeGenerator.TILE_SIZE, 3 * MazeGenerator.TILE_SIZE, camera); // Размер области видимости камеры
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Создаем игрока
        player = new Player(maze, maze.startX * MazeGenerator.TILE_SIZE, maze.startY * MazeGenerator.TILE_SIZE);

        // Устанавливаем начальную позицию камеры
        updateCameraPosition();
    }

    @Override
    public void update(float delta) {
        player.handleInput(delta);
        updateCameraPosition();

        // Проверка достижения финиша
        if (isPlayerAtFinish()) {
            stateManager.setState(new MenuState(stateManager, true));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        animationTime += Gdx.graphics.getDeltaTime();

        // Получение текущего кадра анимации
        TextureRegion currentMaskFrame = maskAnimation.getKeyFrame(animationTime);


        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Отрисовка лабиринта
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

        // Отрисовка игрока
        batch.draw(player.getCurrentFrame(), player.getX(), player.getY(), Player.PLAYER_SIZE, Player.PLAYER_SIZE);
        batch.end();



        // Отрисовка маски, привязанной к камере
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Маска адаптируется к текущему viewport
        float maskWidth = viewport.getWorldWidth();
        float maskHeight = viewport.getWorldHeight();
        float maskX = camera.position.x - maskWidth / 2;
        float maskY = camera.position.y - maskHeight / 2;

        batch.draw(currentMaskFrame, maskX, maskY, maskWidth, maskHeight);
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

        // Ограничиваем позицию камеры, чтобы она не выходила за пределы лабиринта
        float cameraX = Math.max(cameraHalfWidth, Math.min(playerCenterX, mazePixelWidth - cameraHalfWidth));
        float cameraY = Math.max(cameraHalfHeight, Math.min(playerCenterY, mazePixelHeight - cameraHalfHeight));

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        floorTexture.dispose();
        wallTexture.dispose();
        startTexture.dispose();
        endTexture.dispose();
        player.dispose();
        for (TextureRegion region : maskAnimation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }
}
