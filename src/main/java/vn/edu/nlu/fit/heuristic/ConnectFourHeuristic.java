package vn.edu.nlu.fit.heuristic;

import vn.edu.nlu.fit.model.Board;

public class ConnectFourHeuristic implements Heuristic{
    private static final int SCORE_FOUR_IN_A_ROW = 10000;
    private static final int SCORE_THREE_IN_A_ROW = 500;
    private static final int SCORE_TWO_IN_A_ROW = 50;

    private static final int CENTER_COLUMN_WEIGHT = 20;

    private static final int OPPONENT_FOUR_THREAT = 100000;
    private static final int OPPONENT_THREE_THREAT = 1000;

    private int aiId;
    private int opponentId;


    public ConnectFourHeuristic(int aiId, int opponentId) {
        this.aiId = aiId;
        this.opponentId = opponentId;
    }

    @Override
    public int evaluate(Board board) {
        int score = 0;

        score += scoreCenterColumn(board, aiId);

        score += scoreWinningWindows(board);

        return score;
    }

    // tính điểm cho cột giữa vì nó có nhiều cơ hội tạo thành chuỗi 4 ô hơn
    private int scoreCenterColumn(Board board, int playerId) {
        int centerCol = board.getCols() / 2;
        int count = 0;

        for (int r = 0; r < board.getRows(); r++) {
            if (board.getCell(r, centerCol) == playerId) count++;
        }
        return count * CENTER_COLUMN_WEIGHT;
    }

    /*
     duyệt tất cả các cửa sổ 4 ô trên toàn bàn cờ và tính điểm
     */
    private int scoreWinningWindows(Board board) {
        int score = 0;

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col <= board.getCols() - 4; col++) {
                score += scoreHorizontal(board, row, col);
            }
        }

        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row <= board.getRows() - 4; row++) {
                score += scoreVertical(board, row, col);
            }
        }

        for (int row = 0; row <= board.getRows() - 4; row++) {
            for (int col = 0; col <= board.getCols() - 4; col++) {
                score += scoreDiagonalDown(board, row, col);
            }
        }

        for (int row = 3; row < board.getRows(); row++) {
            for (int col = 0; col <= board.getCols() - 4; col++) {
                score += scoreDiagonalUp(board, row, col);
            }
        }

        return score;
    }

    private int scoreHorizontal(Board board, int row, int col) {
        return scoreFourCell(board,
                row, col,
                row, col + 1,
                row, col + 2,
                row, col + 3
        );
    }

    private int scoreVertical(Board board, int row, int col) {
        return scoreFourCell(board,
                row, col,
                row + 1, col,
                row + 2, col,
                row + 3, col
        );
    }

    private int scoreDiagonalDown(Board board, int row, int col) {
        return scoreFourCell(
                board,
                row, col,
                row + 1, col + 1,
                row + 2, col + 2,
                row + 3, col + 3
        );
    }

    private int scoreDiagonalUp(Board board, int row, int col) {
        return scoreFourCell(
                board,
                row, col,
                row - 1, col + 1,
                row - 2, col + 2,
                row - 3, col + 3
        );
    }


    private int scoreFourCell(Board board, int r1, int c1, int r2, int c2, int r3, int c3, int r4, int c4) {
        int aiCount = 0;
        int opponentCount = 0;
        int emptyCount = 0;

        int emptyRow = -1;
        int emptyCol = -1;

        int[][] cells = {{r1, c1}, {r2, c2}, {r3, c3}, {r4, c4}};

        for (int[] pos : cells) {
            int value = board.getCell(pos[0], pos[1]);

            if (value == aiId)
                aiCount++;
            else if (value == opponentId)
                opponentCount++;
            else {
                emptyCount++;
                emptyRow = pos[0];
                emptyCol = pos[1];
            }
        }

        if (aiCount > 0 && opponentCount > 0)
            return 0;


        if (aiCount == 4)
            return SCORE_FOUR_IN_A_ROW;
        if (opponentCount == 4)
            return -OPPONENT_FOUR_THREAT;

//      tính điểm cho các chuỗi 3 ô có thể trở thành 4 ô
//      -> check xem ô trống còn lại có đi được hay không (phải có quân bên dưới thì mới đi được) -> mới tính điểm
        if (emptyCount == 1) {
            boolean playable = board.canDropAt(emptyRow, emptyCol);

            if (aiCount == 3 && playable) return SCORE_THREE_IN_A_ROW;

            if (opponentCount == 3 && playable) return -OPPONENT_THREE_THREAT;
        }


        if (aiCount == 2 && emptyCount == 2) return SCORE_TWO_IN_A_ROW;

        return 0;
    }
}
