package com.dsh.mazegame.settings.commands;

import java.util.Stack;

public class InputCommandManager {
    private static InputCommandManager instance;
    private final Stack<InputCommand> undoStack = new Stack<>();
    private final Stack<InputCommand> redoStack = new Stack<>();

    private InputCommandManager() {}

    public static InputCommandManager getInstance() {
        if (instance == null) {
            instance = new InputCommandManager();
        }
        return instance;
    }

    public void executeCommand(InputCommand command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            InputCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            InputCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}



