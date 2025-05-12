package com.dsh.mazegame.utils;

import java.io.*;
import java.util.*;

public class Leaderboard {
    private static final String FILE_PATH = "./core/assets/leaderboard.txt";
    private static final int MAX_ENTRIES = 10;
    private static Leaderboard instance;
    private final List<Entry> scores = new ArrayList<>();

    private Leaderboard() {
        loadScores();
    }

    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }
        return instance;
    }

    public void addScore(String name, float time) {
        scores.add(new Entry(name, time));
        scores.sort(Comparator.comparingDouble(e -> e.time));
        if (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }
        saveScores();
    }

    public String[] getTopScores() {
        String[] result = new String[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            Entry entry = scores.get(i);
            result[i] = (i + 1) + ". " + entry.name + " - " + entry.time + "s";
        }
        return result;
    }

    private void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                scores.add(new Entry(parts[0], Float.parseFloat(parts[1])));
            }
        } catch (IOException e) {}
    }

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Entry entry : scores) {
                writer.write(entry.name + "," + entry.time);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Entry {
        String name;
        float time;

        Entry(String name, float time) {
            this.name = name;
            this.time = time;
        }
    }
}
