package com.example.paidg;

public abstract class Game {
    private String name;
    private int gameId;

    public Game(String name, int gameId) {
        this.name = name;
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public int getGameId() {
        return gameId;
    }

    public abstract void download();
}
