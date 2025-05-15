package com.dsh.mazegame.settings.commands;

import com.dsh.mazegame.settings.InputSettings;

public class ChangeKeyCommand implements InputCommand {
    private final InputSettings settings;
    private final String keyName;
    private final int newKey;
    private int oldKey;

    public ChangeKeyCommand(InputSettings settings, String keyName, int newKey) {
        this.settings = settings;
        this.keyName = keyName;
        this.newKey = newKey;
    }

    @Override
    public void execute() {
        switch (keyName) {
            case "moveUp":
                oldKey = settings.getMoveUp();
                settings.setMoveUp(newKey);
                break;
            case "moveDown":
                oldKey = settings.getMoveDown();
                settings.setMoveDown(newKey);
                break;
            case "moveLeft":
                oldKey = settings.getMoveLeft();
                settings.setMoveLeft(newKey);
                break;
            case "moveRight":
                oldKey = settings.getMoveRight();
                settings.setMoveRight(newKey);
                break;
            case "sprint":
                oldKey = settings.getSprint();
                settings.setSprint(newKey);
                break;
            case "showMap":
                oldKey = settings.getShowMap();
                settings.setShowMap(newKey);
                break;
        }
    }

    @Override
    public void undo() {
        switch (keyName) {
            case "moveUp":
                settings.setMoveUp(oldKey);
                break;
            case "moveDown":
                settings.setMoveDown(oldKey);
                break;
            case "moveLeft":
                settings.setMoveLeft(oldKey);
                break;
            case "moveRight":
                settings.setMoveRight(oldKey);
                break;
            case "sprint":
                settings.setSprint(oldKey);
                break;
            case "showMap":
                settings.setShowMap(oldKey);
                break;
        }
    }
}




