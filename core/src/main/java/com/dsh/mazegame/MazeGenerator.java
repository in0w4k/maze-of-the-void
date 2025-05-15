package com.dsh.mazegame;

import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

import com.dsh.mazegame.difficulty.DifficultyStrategy;
import com.dsh.mazegame.trap.Trap;
import com.dsh.mazegame.trap.TrapFactory;

public class MazeGenerator {
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int TILE_SIZE = 32;

    public final int width;
    public final int height;
    public int[][] maze;
    public int startX, startY, endX, endY;

    private final DifficultyStrategy difficultyStrategy;

    public List<Trap> traps = new ArrayList<>();

    private final Random random = new Random();

    public MazeGenerator(int width, int height, DifficultyStrategy difficultyStrategy) {
        this.width = width;
        this.height = height;
        this.difficultyStrategy = difficultyStrategy;
        this.maze = new int[width][height];
        generateMaze();
    }

    private void generateMaze() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                maze[x][y] = WALL;
            }
        }

        startX = 1 + random.nextInt(width - 2);
        startY = 1 + random.nextInt(height - 2);
        maze[startX][startY] = PATH;

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        boolean[][] visited = new boolean[width][height];
        visited[startX][startY] = true;

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            int[][] directions = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
            shuffleArray(directions, random);

            boolean foundPath = false;
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX > 0 && newX < width - 1 && newY > 0 && newY < height - 1 && maze[newX][newY] == WALL) {
                    maze[x + dir[0]/2][y + dir[1]/2] = PATH;
                    maze[newX][newY] = PATH;
                    visited[newX][newY] = true;
                    stack.push(new int[]{newX, newY});
                    foundPath = true;
                    break;
                }
            }

            if (!foundPath) {
                stack.pop();
            }
        }

        addDeadEnds();

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

        addBranches();

        generateTraps();
    }

    private void addDeadEnds() {
        int deadEnds = (width * height) / 4; // настройка тупиков

        for (int i = 0; i < deadEnds; i++) {
            int x = 1 + random.nextInt(width - 2);
            int y = 1 + random.nextInt(height - 2);

            if (maze[x][y] == WALL) {
                int pathCount = 0;
                if (maze[x + 1][y] == PATH) pathCount++;
                if (maze[x - 1][y] == PATH) pathCount++;
                if (maze[x][y + 1] == PATH) pathCount++;
                if (maze[x][y - 1] == PATH) pathCount++;
                if (pathCount == 1) {
                    maze[x][y] = PATH;
                }
            }
        }
    }

    private void addBranches() {
        int branches = (width * height) / 4; // настройка ответвлений

        for (int i = 0; i < branches; i++) {
            int x = 1 + random.nextInt(width - 2);
            int y = 1 + random.nextInt(height - 2);

            if (maze[x][y] == WALL) {
                if (maze[x - 1][y] == PATH && maze[x + 1][y] == PATH && maze[x][y - 1] == WALL && maze[x][y + 1] == WALL) {
                    maze[x][y] = PATH;
                } else if (maze[x][y - 1] == PATH && maze[x][y + 1] == PATH && maze[x - 1][y] == WALL && maze[x + 1][y] == WALL) {
                    maze[x][y] = PATH;
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

    private void generateTraps() {
        TrapFactory factory = new TrapFactory(difficultyStrategy);
        int trapCount = difficultyStrategy.getTrapsCount(width, height);
        int attempts = 0;
        int maxAttempts = trapCount * 2;

        while (traps.size() < trapCount && attempts < maxAttempts) {
            int x = 1 + random.nextInt(width - 2);
            int y = 1 + random.nextInt(height - 2);

            boolean trapExists = false;
            for (Trap existingTrap : traps) {
                if (existingTrap.getX() == x && existingTrap.getY() == y) {
                    trapExists = true;
                    break;
                }
            }

            if (!trapExists && maze[x][y] == PATH &&
                !(x == startX && y == startY) &&
                !(x == endX && y == endY)) {
                Trap trap = factory.createRandomTrap(x, y);
                traps.add(trap);
            }

            attempts++;
        }
    }
}
