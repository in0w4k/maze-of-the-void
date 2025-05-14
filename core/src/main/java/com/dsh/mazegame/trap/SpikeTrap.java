package com.dsh.mazegame.trap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dsh.mazegame.MazeGenerator;
import com.dsh.mazegame.Player;
import com.dsh.mazegame.difficulty.DifficultyStrategy;

public class SpikeTrap implements Trap {
    private final Texture texture;
    private final Texture textureHidden;
    private final int x, y;
    private final DifficultyStrategy difficultyStrategy;
    private boolean visible = false;
    private float timer = 0f;
    private boolean damageDealt = false;


    public SpikeTrap(int x, int y, DifficultyStrategy difficultyStrategy) {
        this.x = x;
        this.y = y;
        this.difficultyStrategy = difficultyStrategy;
        this.texture = new Texture("./core/assets/spike.png");
        this.textureHidden = new Texture("./core/assets/spike_hidden.png");
    }

    private boolean isPlayerColliding(Player player) {
        float trapCenterX = (x * MazeGenerator.TILE_SIZE) + (MazeGenerator.TILE_SIZE / 2f);
        float trapCenterY = (y * MazeGenerator.TILE_SIZE) + (MazeGenerator.TILE_SIZE / 2f);

        float playerCenterX = player.getX() + (Player.PLAYER_SIZE / 2f);
        float playerCenterY = player.getY() + (Player.PLAYER_SIZE / 2f);

        return playerCenterX >= x * MazeGenerator.TILE_SIZE &&
            playerCenterX < (x + 1) * MazeGenerator.TILE_SIZE &&
            playerCenterY >= y * MazeGenerator.TILE_SIZE &&
            playerCenterY < (y + 1) * MazeGenerator.TILE_SIZE;
    }


    public void update(float delta, Player player) {

        timer += delta;

        if (!visible && timer > difficultyStrategy.getTrapDuration() / 2) {
            visible = true;
            timer = 0f;
            damageDealt = false;
            if (isPlayerColliding(player)) {
                player.damage(1);
                damageDealt = true;
            }
        }
        else if (visible && timer > difficultyStrategy.getTrapDuration()) {
            visible = false;
            timer = 0f;
            damageDealt = false;
        }

        if (visible && !damageDealt && isPlayerColliding(player)) {
            player.damage(1);
            damageDealt = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Texture tex = visible ? texture : textureHidden;
        batch.draw(tex, x * MazeGenerator.TILE_SIZE, y * MazeGenerator.TILE_SIZE,
            MazeGenerator.TILE_SIZE, MazeGenerator.TILE_SIZE);
    }

    @Override
    public void onStep(Player player) { }

    @Override
    public int getX() { return x; }

    @Override
    public int getY() { return y; }
}
