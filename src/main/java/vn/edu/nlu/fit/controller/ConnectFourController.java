package vn.edu.nlu.fit.controller;

import vn.edu.nlu.fit.enums.GameMode;
import vn.edu.nlu.fit.heuristic.ConnectFourHeuristic;
import vn.edu.nlu.fit.model.*;
import vn.edu.nlu.fit.view.ConnectFourView;

import java.awt.*;

public class ConnectFourController {
    private final ConnectFourGame model;
    private final ConnectFourView view;
    private GameMode currentMode;
    public ConnectFourController(ConnectFourGame model, ConnectFourView view) {
        this.model = model;
        this.view = view;

        this.currentMode = GameMode.PVP;
        startPvP();
        updateStatusLabel();

        this.addButtonAction();
    }

    public void addButtonAction() {
        for (int col = 0; col < model.getCols(); col++) {
            int column = col;
            view.getColButton(col).addActionListener(e -> handleColumnClick(column));
        }

        view.getModeComboBox().addActionListener(e -> handleComboBoxAction(view.getSelectedGameMode()));

        view.getResetButton().addActionListener(e -> resetRound());
    }

    public void handleComboBoxAction(GameMode mode) {
        switch (mode) {
            case PVP:
                currentMode = GameMode.PVP;
                startPvP();
                view.setGameModeTitleText(GameMode.PVP.toString());
                break;

            case PVE:
                currentMode = GameMode.PVE;
                startPvE();
                view.setGameModeTitleText(GameMode.PVE.toString());
                break;
        }
        resetRound();
    }

    public void handleColumnClick(int col) {
        if (model.isGameOver()) {
            return;
        }

        Move move = model.makeMove(col);
        if (move == null) {
            view.showMessage("Cột này đã đầy!");
            return;
        }
        // Cập nhật view
        view.updateCell(move.getRow(), move.getCol(), move.getPlayer());

        checkGameState();

        if (!model.isGameOver() && currentMode == GameMode.PVE && model.getCurrentPlayer().isAI()) {

            int aiCol = ((AIPlayer) model.getCurrentPlayer()).chooseColumn(model.getBoard());

            Move aiMove = model.makeMove(aiCol);

            if (aiMove == null) return;

            view.updateCell(aiMove.getRow(), aiMove.getCol(), aiMove.getPlayer());
            checkGameState();
        }
    }


    public void checkGameState() {
        GameState state = model.getGameState();
        if (state.isGameOver()) {
            if (state.getWinner() != null) {
                handleWin(state.getWinner());
            } else if (state.isDraw()) {
                handleDraw();
            }
        } else {
            updateStatusLabel();
        }
    }

    public void handleWin(Player winner) {
        String message = winner.getName() + " thắng!";
        view.updateStatus("Game kết thúc: " + message);
        view.showMessage(message);
    }

    public void handleDraw() {
        String message = "Hòa! Bàn cờ đầy.";
        view.updateStatus(message);
        view.showMessage(message);
    }

    public void resetRound() {
        model.reset();
        updateAllCells();
        updateStatusLabel();
    }

    public void startPvP() {
        Player player1 = new HumanPlayer(1, "Người chơi Đỏ", new Color(220, 40, 40));
        Player player2 = new HumanPlayer(2, "Người chơi Vàng", new Color(255, 210, 25));
        model.setPlayers(player1, player2);
    }

    public void startPvE() {
        Player human = new HumanPlayer(1, "Bạn", new Color(220, 40, 40));

        Player ai = new AIPlayer(2, "Máy", new Color(255, 210, 25),
                                                        human.getId(),
                                                        model.getWinChecker(),
                                                        new ConnectFourHeuristic(human.getId(), 2));

        model.setPlayers(human, ai);
    }


    public void updateStatusLabel() {
        Player currentPlayer = model.getCurrentPlayer();
        view.updateStatus("Lượt: " + currentPlayer.getName());
    }

    public void updateAllCells() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                view.updateCell(r, c, null);
            }
        }
        view.repaint();
    }
}