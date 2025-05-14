package com.dsh.mazegame.trap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dsh.mazegame.MazeGenerator;
import com.dsh.mazegame.Player;
import com.dsh.mazegame.difficulty.DifficultyStrategy;

import static com.badlogic.gdx.math.MathUtils.random;

public class PitTrap implements Trap {
    private static Texture textureOpen;
    private static Texture textureClosed;
    private final int x, y;
    private boolean triggered = false;
    private final DifficultyStrategy difficultyStrategy;
    private boolean qteActive = false;
    private float qteTimer = 0f;
    private final float QTE_TIME = 0.75f;
    private int qteKey = Input.Keys.SPACE;
    private boolean qteSuccess = false;
    private boolean blocking = false;

    public PitTrap(int x, int y, DifficultyStrategy difficultyStrategy) {
        this.x = x;
        this.y = y;
        this.difficultyStrategy = difficultyStrategy;
        if (textureOpen == null) {
            textureOpen = new Texture("./core/assets/trap.png");
        }
        if (textureClosed == null) {
            textureClosed = new Texture("./core/assets/trap_closed.png");
        }
    }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }

    @Override
    public void render(SpriteBatch batch) {
        if (!qteActive && !triggered) {
            batch.draw(textureOpen, x * MazeGenerator.TILE_SIZE, y * MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
        } else {
            batch.draw(textureClosed, x * MazeGenerator.TILE_SIZE, y * MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
        }
    }

    @Override
    public void onStep(Player player) {
        if (triggered) return;

        if (!qteActive) {
            qteActive = true;
            qteTimer = 0f;
            qteSuccess = false;
            blocking = true;

            int[] availableKeys = difficultyStrategy.getAvailableQteKeys();
            qteKey = availableKeys[random.nextInt(availableKeys.length)];
        }

        if (qteActive) {
            qteTimer += Gdx.graphics.getDeltaTime();
            if (Gdx.input.isKeyJustPressed(qteKey)) {
                qteSuccess = true;
                qteActive = false;
                triggered = true;
                blocking = false;
            } else if (qteTimer > difficultyStrategy.getTrapDuration()) {
                if (!qteSuccess) {
                    player.damage(1);
                    triggered = true;
                    qteActive = false;
                    blocking = false;
                }
            }
        }
    }

    public void resetIfPlayerLeft(int playerCellX, int playerCellY) {
        if ((playerCellX != x || playerCellY != y) && !triggered) {
            qteActive = false;
            qteTimer = 0f;
            qteSuccess = false;
            blocking = false;
        }
    }

    public boolean isQteActive() {
        return qteActive;
    }

    public int getQteKey() {
        return qteKey;
    }

    public boolean isBlocking() {
        return qteActive && !triggered;
    }
}
