package vn.edu.nlu.fit.model;

import lombok.Getter;

@Getter
public class GameState {
    private Player winner;
    private boolean isGameOver;
    private boolean isDraw;

    public GameState() {
        this.isGameOver = false;
        this.winner = null;
        this.isDraw = false;
    }

    public void setWinner(Player player) {
        this.winner = player;
        this.isGameOver = true;
        this.isDraw = false;
    }

    public void setDraw() {
        this.isDraw = true;
        this.isGameOver = true;
        this.winner = null;
    }

    public void reset() {
        this.isGameOver = false;
        this.isDraw = false;
        this.winner = null;
    }
}
