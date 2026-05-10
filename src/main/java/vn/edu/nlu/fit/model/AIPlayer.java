package vn.edu.nlu.fit.model;

import vn.edu.nlu.fit.heuristic.Heuristic;

import java.awt.Color;

public class AIPlayer extends Player{
    private static final int MAX_DEPTH = 7;
    private static final int WIN_SCORE = 1000000;
    private static final int LOSE_SCORE = -1000000;

    private int humanPlayerId;
    private WinChecker winChecker;
    private Heuristic heuristic;
    private long nodesVisited = 0;
    private boolean useAlphaBeta = true;
    private int searchDepth = MAX_DEPTH;


    public AIPlayer(int id, String name, Color color, int humanPlayerId, WinChecker winChecker, Heuristic heuristic) {
        super(id, name, color);
        this.humanPlayerId = humanPlayerId;
        this.winChecker = winChecker;
        this.heuristic = heuristic;
    }


    @Override
    public boolean isAI() {
        return true;
    }

    public int chooseColumn(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;

        for (int col = 0; col < board.getCols(); col++) {
            if (!board.isValidColumn(col)) continue;

            Board newBoard = board.copyWithMove(col, this.id);

            int score;
            if (useAlphaBeta) {
                score = minimaxAlphaBeta(false, newBoard,
                        searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                score = minimax(newBoard, searchDepth - 1, false);
            }

            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }
        return bestCol;
    }



    private int minimaxAlphaBeta(boolean isMax, Board board, int depth, int alpha, int beta) {
        nodesVisited++;

        if (winChecker.checkWin(board, this.id))
            return WIN_SCORE + depth; // ưu tiên node thắng nhanh hơn

        if (winChecker.checkWin(board, humanPlayerId))
            return LOSE_SCORE - depth; // ưu tiên node thua muộn hơn

        if (depth == 0 || board.isFull())
            return this.heuristic.evaluate(board);

        if (isMax) {
            int maxValue = Integer.MIN_VALUE;

            int[] colOrder = {3, 2, 4, 1, 5, 0, 6};
//           duyệt theo thứ tự ưu tiên cột giữa trước để tìm thấy node đi tốt sớm
//           -> alpha beta cắt được nhiều nhánh thừa hơn
            for (int col : colOrder) {
                if (!board.isValidColumn(col)) continue;

                Board newBoard = board.copyWithMove(col, this.id);
                int eval = minimaxAlphaBeta(false, newBoard, depth - 1,  alpha, beta);

                maxValue = Math.max(maxValue, eval);
                alpha = Math.max(alpha, eval);

                if (alpha >= beta)
                    break;
            }
            return maxValue;

        } else {
            int minValue = Integer.MAX_VALUE;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.isValidColumn(col)) continue;

                Board newBoard = board.copyWithMove(col, humanPlayerId);
                int eval = minimaxAlphaBeta(true, newBoard, depth - 1,  alpha, beta);

                minValue = Math.min(minValue, eval);
                beta = Math.min(beta, eval);

                if (alpha >= beta)
                    break;
            }
            return minValue;
        }
    }


    private int minimax(Board board, int depth, boolean isMax) {
        nodesVisited++;
        if (winChecker.checkWin(board, this.id))
            return WIN_SCORE;

        if (winChecker.checkWin(board, humanPlayerId))
            return LOSE_SCORE;

        if (depth == 0 || board.isFull())
            return this.heuristic.evaluate(board);

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.isValidColumn(col)) continue;

                Board newBoard = board.copyWithMove(col, this.id);

                maxEval = Math.max(maxEval, minimax(newBoard, depth - 1, false));
            }
            return maxEval;

        } else {
            int minEval = Integer.MAX_VALUE;

            for (int col = 0; col < board.getCols(); col++) {
                if (!board.isValidColumn(col)) continue;

                Board newBoard = board.copyWithMove(col, humanPlayerId);

                minEval = Math.min(minEval, minimax(newBoard, depth - 1, true));
            }
            return minEval;
        }
    }






}
