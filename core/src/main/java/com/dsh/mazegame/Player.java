package com.dsh.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private float x, y, speed;
    private TextureRegion currentFrame;
    private TextureRegion frameUp, frameDown, frameLeft, frameRight;
    private TextureRegion frameUpLeft, frameUpRight, frameDownLeft, frameDownRight;
    private Texture sheetUp, sheetDown, sheetLeft, sheetRight;
    private Texture sheetUpLeft, sheetUpRight, sheetDownLeft, sheetDownRight;
    private final MazeGenerator maze;
    public static final int PLAYER_SIZE = 24;
    private static final int COLLISION_SIZE = 24;

    // --- Observer pattern ---
    public interface HealthListener {
        void onHealthChanged(int newHealth, int maxHealth);
    }

    private int health = 10;
    private int maxHealth = 10;
    private final List<HealthListener> healthListeners = new ArrayList<>();

    public void addHealthListener(HealthListener listener) {
        healthListeners.add(listener);
    }

    public void removeHealthListener(HealthListener listener) {
        healthListeners.remove(listener);
    }

    private void notifyHealthChanged() {
        for (HealthListener listener : healthListeners) {
            listener.onHealthChanged(health, maxHealth);
        }
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setHealth(int value) {
        health = Math.max(0, Math.min(value, maxHealth));
        notifyHealthChanged();
    }

    private boolean damaged = false;
    private float damageTimer = 0f;
    private static final float DAMAGE_FLASH_TIME = 0.2f;

    public void damage(int amount) {
        setHealth(health - amount);
        damaged = true;
        damageTimer = DAMAGE_FLASH_TIME;
    }

    public void heal(int amount) {
        setHealth(health + amount);
    }

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
        sheetUpLeft = new Texture("./core/assets/player/up_left.png");
        sheetUpRight = new Texture("./core/assets/player/up_right.png");
        sheetDownLeft = new Texture("./core/assets/player/down_left.png");
        sheetDownRight = new Texture("./core/assets/player/down_right.png");
    }

    private void initializeFrames() {
        frameUp = new TextureRegion(sheetUp, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameDown = new TextureRegion(sheetDown, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameLeft = new TextureRegion(sheetLeft, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameRight = new TextureRegion(sheetRight, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameUpLeft = new TextureRegion(sheetUpLeft, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameUpRight = new TextureRegion(sheetUpRight, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameDownLeft = new TextureRegion(sheetDownLeft, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
        frameDownRight = new TextureRegion(sheetDownRight, 0, 0, PLAYER_SIZE, PLAYER_SIZE);
    }

    private boolean canMove(float nx, float ny) {
        float centerX = nx + PLAYER_SIZE / 2f;
        float centerY = ny + PLAYER_SIZE / 2f;

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

        boolean up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        if (up) dy += 1;
        if (down) dy -= 1;
        if (left) dx -= 1;
        if (right) dx += 1;
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

            // --- Выбор спрайта по направлению ---
            if (up && left && !right && !down) {
                currentFrame = frameUpLeft;
            } else if (up && right && !left && !down) {
                currentFrame = frameUpRight;
            } else if (down && left && !right && !up) {
                currentFrame = frameDownLeft;
            } else if (down && right && !left && !up) {
                currentFrame = frameDownRight;
            } else if (up && !left && !right && !down) {
                currentFrame = frameUp;
            } else if (down && !left && !right && !up) {
                currentFrame = frameDown;
            } else if (left && !up && !down && !right) {
                currentFrame = frameLeft;
            } else if (right && !up && !down && !left) {
                currentFrame = frameRight;
            }
        }
    }

    public void update(float delta) {
        if (damaged) {
            damageTimer -= delta;
            if (damageTimer <= 0f) {
                damaged = false;
                damageTimer = 0f;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (damaged) {
            batch.setColor(1, 0.3f, 0.3f, 1);
        }
        batch.draw(getCurrentFrame(), getX(), getY(), PLAYER_SIZE, PLAYER_SIZE);
        if (damaged) {
            batch.setColor(1, 1, 1, 1);
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
        sheetUpLeft.dispose();
        sheetUpRight.dispose();
        sheetDownLeft.dispose();
        sheetDownRight.dispose();
    }
}
