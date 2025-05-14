package com.dsh.mazegame.trap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dsh.mazegame.Player;

public interface Trap {
    int getX();
    int getY();
    void render(SpriteBatch batch);
    void onStep(Player player);
}
