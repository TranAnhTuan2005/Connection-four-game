package vn.edu.nlu.fit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerManager
 */
public class PlayerManager {
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    public void setPlayers(Player p1, Player p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Player cannot null");
        }

        if (p1.getId() == p2.getId()) {
            throw new IllegalArgumentException("Two players cannot have the same IDs");
        }

        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = player1;
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == null) {
            throw new IllegalStateException("player is not set yet");
        }
        return currentPlayer;
    }

    public Player getOpponent() {
        return currentPlayer == player1 ? player2 : player1;
    }

    public void switchToNextPlayer() {
        currentPlayer = getOpponent();
    }

    public void reset() {
        currentPlayer = player1;
    }

    public boolean isAIPlaying() {
        return currentPlayer.isAI();
    }
}

