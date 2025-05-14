package com.dsh.mazegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HealthBar implements Player.HealthListener {
    private int health;
    private int maxHealth;
    private final Texture heartTexture;
    private static final int HEART_SIZE = 32;
    private static final int PADDING = 10;

    public HealthBar(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.heartTexture = new Texture("./core/assets/heart.png");
    }

    @Override
    public void onHealthChanged(int newHealth, int maxHealth) {
        this.health = newHealth;
        this.maxHealth = maxHealth;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < maxHealth; i++) {
            float x = 20 + i * (HEART_SIZE + PADDING);
            float y = HEART_SIZE + 20;
            if (i < health) {
                batch.draw(heartTexture, x, y, HEART_SIZE, HEART_SIZE);
            } else {
                batch.setColor(1, 1, 1, 0.3f);
                batch.draw(heartTexture, x, y, HEART_SIZE, HEART_SIZE);
                batch.setColor(1, 1, 1, 1);
            }
        }
    }

    public void dispose() {
        heartTexture.dispose();
    }
}
