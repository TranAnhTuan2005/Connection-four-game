package vn.edu.nlu.fit.model;

public class ConnectFourWinChecker implements WinChecker {
    private int winLength;

    public ConnectFourWinChecker(int winLength) {
        this.winLength = winLength;
    }

    @Override
    public boolean checkWin(Board board, int row, int col, int playerId) {
        if (board.getCell(row, col) != playerId) {
            return false;
        }

        return checkRows(board, row, col, playerId) || checkCols(board, row, col, playerId) || checkDiagonalRight(board, row, col, playerId) || checkDiagonalLeft(board, row, col, playerId);
    }


    //for minimax
    @Override
    public boolean checkWin(Board board, int playerId) {
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                if (board.getCell(r, c) == playerId) {
                    if (checkWin(board, r, c, playerId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    public boolean checkRows(Board board, int row, int col, int playerId) {
        int count = 1;

        for (int c = col - 1; c >= 0; c--) {
            if (board.getCell(row, c) != playerId) break;
            count++;
        }

        for (int c = col + 1; c < board.getCols(); c++) {
            if (board.getCell(row, c) != playerId) break;
            count++;
        }

        return count >= winLength;

    }


    public boolean checkCols(Board board, int row, int col, int playerId) {
        int count = 1;

        for (int r = row - 1; r >= 0; r--) {
            if (board.getCell(r, col) != playerId) break;
            count++;
        }

        for (int r = row + 1; r < board.getRows(); r++) {
            if (board.getCell(r, col) != playerId) break;
            count++;
        }

        return count >= winLength;
    }


    private boolean checkDiagonalRight(Board board, int row, int col, int playerId) {
        int count = 1;

        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
            if (board.getCell(r, c) != playerId) break;
            count++;
        }

        for (int r = row + 1, c = col + 1; r < board.getRows() && c < board.getCols(); r++, c++) {
            if (board.getCell(r, c) != playerId) break;
            count++;
        }

        return count >= winLength;
    }


    private boolean checkDiagonalLeft(Board board, int row, int col, int playerId) {
        int count = 1;

        for (int r = row - 1, c = col + 1; r >= 0 && c < board.getCols(); r--, c++) {
            if (board.getCell(r, c) != playerId) break;
            count++;
        }

        for (int r = row + 1, c = col - 1; r < board.getRows() && c >= 0; r++, c--) {
            if (board.getCell(r, c) != playerId) break;
            count++;
        }

        return count >= winLength;
    }


}