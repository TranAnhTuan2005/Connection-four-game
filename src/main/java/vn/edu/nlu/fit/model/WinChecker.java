package vn.edu.nlu.fit.model;

public interface WinChecker {
    boolean checkWin(Board board, int row, int col, int playerId);

    boolean checkWin(Board board, int playerId);
}
