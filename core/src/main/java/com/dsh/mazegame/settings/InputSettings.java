package com.dsh.mazegame.settings;

import com.badlogic.gdx.Input;

public class InputSettings {
    private static InputSettings instance;

    private int moveUp = Input.Keys.W;
    private int moveDown = Input.Keys.S;
    private int moveLeft = Input.Keys.A;
    private int moveRight = Input.Keys.D;
    private int sprint = Input.Keys.SHIFT_LEFT;
    private int showMap = Input.Keys.RIGHT_BRACKET;

    private InputSettings() {}

    public static InputSettings getInstance() {
        if (instance == null) {
            instance = new InputSettings();
        }
        return instance;
    }

    public int getMoveUp() { return moveUp; }
    public int getMoveDown() { return moveDown; }
    public int getMoveLeft() { return moveLeft; }
    public int getMoveRight() { return moveRight; }
    public int getSprint() { return sprint; }
    public int getShowMap() { return showMap; }

    public void setMoveUp(int key) { moveUp = key; }
    public void setMoveDown(int key) { moveDown = key; }
    public void setMoveLeft(int key) { moveLeft = key; }
    public void setMoveRight(int key) { moveRight = key; }
    public void setSprint(int key) { sprint = key; }
    public void setShowMap(int key) { showMap = key; }
}
