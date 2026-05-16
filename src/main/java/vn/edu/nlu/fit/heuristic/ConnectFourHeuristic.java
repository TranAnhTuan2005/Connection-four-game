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
    // [UC9 - 9.0] Nhận trạng thái bàn cờ giả lập từ UC8 (AIPlayer.minimaxAlphaBeta)
    @Override
    public int evaluate(Board board) {
        int score = 0;
    // [UC9 - 9.1] Tính điểm ưu tiên vị trí cột trung tâm
        score += scoreCenterColumn(board, aiId);
        // [UC9 - 9.2] Quét bàn cờ theo 4 hướng: ngang, dọc, chéo xuôi, chéo ngược
        score += scoreWinningWindows(board);
        // [UC9 - 9.4] Tổng hợp và trả về điểm số cuối cùng
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
        // [UC9 - 9.2] Quét ngang
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col <= board.getCols() - 4; col++) {
                score += scoreHorizontal(board, row, col);
            }
        }
        // [UC9 - 9.2] Quét dọc
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = 0; row <= board.getRows() - 4; row++) {
                score += scoreVertical(board, row, col);
            }
        }
        // [UC9 - 9.2] Quét chéo xuôi
        for (int row = 0; row <= board.getRows() - 4; row++) {
            for (int col = 0; col <= board.getCols() - 4; col++) {
                score += scoreDiagonalDown(board, row, col);
            }
        }
        // [UC9 - 9.2] Quét chéo ngược
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
        // [UC9 - 9.3] Đếm số quân AI, đối thủ, ô trống trong cửa sổ 4 ô
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
        // [UC9 - 9.3] Cửa sổ hỗn hợp (có cả AI lẫn đối thủ) → bỏ qua
        if (aiCount > 0 && opponentCount > 0)
            return 0;

        // [UC9 - 9.3] 4 quân AI → +10.000 điểm
        if (aiCount == 4)
            return SCORE_FOUR_IN_A_ROW;
        // [UC9 - 9.3] 4 quân đối thủ → -100.000 điểm
        if (opponentCount == 4)
            return -OPPONENT_FOUR_THREAT;

//      tính điểm cho các chuỗi 3 ô có thể trở thành 4 ô
//      -> check xem ô trống còn lại có đi được hay không (phải có quân bên dưới thì mới đi được) -> mới tính điểm
        if (emptyCount == 1) {
            // [UC9 - 9.3] Kiểm tra canDropAt: ô trống có bệ đỡ bên dưới không
            boolean playable = board.canDropAt(emptyRow, emptyCol);
            // [UC9 - 9.3] 3 quân AI + 1 ô trống hợp lệ → +500 điểm
            if (aiCount == 3 && playable) return SCORE_THREE_IN_A_ROW;
            // [UC9 - 9.3] 3 quân đối thủ + 1 ô trống hợp lệ → -1.000 điểm
            if (opponentCount == 3 && playable) return -OPPONENT_THREE_THREAT;
        }

        // [UC9 - 9.3] 2 quân AI + 2 ô trống → +50 điểm
        if (aiCount == 2 && emptyCount == 2) return SCORE_TWO_IN_A_ROW;

        return 0;
    }
}
