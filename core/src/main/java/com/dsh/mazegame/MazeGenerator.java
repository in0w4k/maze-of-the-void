package com.dsh.mazegame;

import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int TILE_SIZE = 32;

    public final int width;
    public final int height;
    public int[][] maze;
    public int startX, startY, endX, endY;

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new int[width][height];
        generateMaze();
    }

    private void generateMaze() {
        // Инициализация лабиринта стенами
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                maze[x][y] = WALL;
            }
        }

        // Выбираем случайную начальную точку
        Random random = new Random();
        startX = 1 + random.nextInt(width - 2);
        startY = 1 + random.nextInt(height - 2);
        maze[startX][startY] = PATH;

        // Используем стек для отслеживания текущего пути
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            // Проверяем все возможные направления
            int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
            shuffleArray(directions, random);

            boolean foundPath = false;
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX > 0 && newX < width - 1 && newY > 0 && newY < height - 1 && maze[newX][newY] == WALL) {
                    // Прорубаем путь
                    maze[x + dir[0]/2][y + dir[1]/2] = PATH;
                    maze[newX][newY] = PATH;
                    stack.push(new int[]{newX, newY});
                    foundPath = true;
                    break;
                }
            }

            if (!foundPath) {
                stack.pop();
            }
        }

        // Находим конечную точку (самую дальнюю от начала)
        int maxDistance = -1;
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (maze[x][y] == PATH) {
                    int distance = Math.abs(x - startX) + Math.abs(y - startY);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        endX = x;
                        endY = y;
                    }
                }
            }
        }
    }

    private void shuffleArray(int[][] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
