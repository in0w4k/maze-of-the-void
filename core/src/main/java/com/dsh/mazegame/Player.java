package com.dsh.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    private float x, y, speed;
    private TextureRegion currentFrame;
    private TextureRegion frameUp, frameDown, frameLeft, frameRight;
    private Texture sheetUp, sheetDown, sheetLeft, sheetRight;
    private final MazeGenerator maze;
    public static final int PLAYER_SIZE = 24; // Новый размер спрайта игрока
    private static final int COLLISION_SIZE = 24; // Размер области коллизий

    public Player(MazeGenerator maze, float startX, float startY) {
        x = startX;
        y = startY;
        speed = 100f;
        this.maze = maze;
        loadTextures();
        initializeFrames();
        currentFrame = frameDown;
    }

    private void loadTextures() {
        sheetUp = new Texture("./core/assets/player/up.png");
        sheetDown = new Texture("./core/assets/player/down.png");
        sheetLeft = new Texture("./core/assets/player/left.png");
        sheetRight = new Texture("./core/assets/player/right.png");
    }

    private void initializeFrames() {
        // Используем только первый кадр из каждого спрайта
        frameUp = new TextureRegion(sheetUp, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameDown = new TextureRegion(sheetDown, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameLeft = new TextureRegion(sheetLeft, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameRight = new TextureRegion(sheetRight, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
    }

    private boolean canMove(float nx, float ny) {
        // Вычисляем центр области коллизий
        float centerX = nx + PLAYER_SIZE / 2f;
        float centerY = ny + PLAYER_SIZE / 2f;

        // Проверяем несколько точек вокруг центра
        float[] checkPointsX = {
            centerX,                    // центр
            centerX - (float) COLLISION_SIZE /2, // левый край
            centerX + (float) COLLISION_SIZE /2, // правый край
            centerX,                    // центр
            centerX                     // центр
        };

        float[] checkPointsY = {
            centerY,                    // центр
            centerY,                    // центр
            centerY,                    // центр
            centerY - (float) COLLISION_SIZE /2, // нижний край
            centerY + (float) COLLISION_SIZE /2  // верхний край
        };

        for (int i = 0; i < checkPointsX.length; i++) {
            int mazeX = (int) (checkPointsX[i] / MazeGenerator.TILE_SIZE);
            int mazeY = (int) (checkPointsY[i] / MazeGenerator.TILE_SIZE);

            // Проверяем границы лабиринта
            if (mazeX < 0 || mazeX >= maze.width || mazeY < 0 || mazeY >= maze.height) {
                return false;
            }

            // Проверяем столкновение со стеной
            if (maze.maze[mazeX][mazeY] == MazeGenerator.WALL) {
                return false;
            }
        }
        return true;
    }

    public void handleInput(float delta) {
        float dx = 0, dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dy += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speed = 200f;
        } else {
            speed = 100f;
        }

        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {
            dx /= length;
            dy /= length;

            // Пробуем двигаться по X и Y отдельно для более плавного скольжения вдоль стен
            float newX = x + dx * speed * delta;
            float newY = y + dy * speed * delta;

            // Проверяем движение по X
            if (canMove(newX, y)) {
                x = newX;
            }
            // Проверяем движение по Y
            if (canMove(x, newY)) {
                y = newY;
            }

            // Выбор спрайта по направлению
            if (Math.abs(dx) > Math.abs(dy)) {
                currentFrame = dx > 0 ? frameRight : frameLeft;
            } else {
                currentFrame = dy > 0 ? frameUp : frameDown;
            }
        }
    }

    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
        sheetUp.dispose();
        sheetDown.dispose();
        sheetLeft.dispose();
        sheetRight.dispose();
    }
}
