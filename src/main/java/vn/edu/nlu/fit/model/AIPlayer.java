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

    /** [v2.0] Đặt độ sâu tìm kiếm — dùng cho AIDifficulty và HintAdvisor */
    public void setSearchDepth(int depth) {
        this.searchDepth = Math.max(1, Math.min(depth, MAX_DEPTH));
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    @Override
    public boolean isAI() {
        return true;
    }
    // [UC8 - 8.0] Được Controller kích hoạt khi đến lượt AI
    public int chooseColumn(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int bestCol = -1;
        // [UC8 - 8.2] Duyệt từng cột, lọc các cột hợp lệ (chưa đầy)
        for (int col = 0; col < board.getCols(); col++) {
            if (!board.isValidColumn(col)) continue;
            // [UC8 - 8.3] Tạo bản sao bàn cờ, giả lập đặt thử quân AI
            Board newBoard = board.copyWithMove(col, this.id);

            int score;
            if (useAlphaBeta) {
                // [UC8 - 8.5] Gọi Minimax Alpha-Beta để tính điểm nước đi
                score = minimaxAlphaBeta(false, newBoard,
                        searchDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                score = minimax(newBoard, searchDepth - 1, false);
            }
            // [UC8 - 8.6] Cập nhật cột tốt nhất
            if (score > bestScore) {
                bestScore = score;
                bestCol = col;
            }
        }
        // [UC8 - 8.6] Trả về cột tốt nhất
        // [UC8 - 8-E1] Nếu tất cả cột đầy, bestCol = -1, Controller sẽ xử lý hòa
        return bestCol;
    }



    private int minimaxAlphaBeta(boolean isMax, Board board, int depth, int alpha, int beta) {
        nodesVisited++;
        // [UC8 - Alt 8.1a] Phát hiện thắng ngay → trả về WIN_SCORE, ưu tiên thắng nhanh
        if (winChecker.checkWin(board, this.id))
            return WIN_SCORE + depth; // ưu tiên node thắng nhanh hơn
        // [UC8 - Alt 8.1a] Phát hiện thua → trả về LOSE_SCORE, ưu tiên thua muộn
        if (winChecker.checkWin(board, humanPlayerId))
            return LOSE_SCORE - depth; // ưu tiên node thua muộn hơn
        // [UC8 - 8.4] Node lá: gọi UC9 (ConnectFourHeuristic) để lấy điểm đánh giá
        if (depth == 0 || board.isFull())
            return this.heuristic.evaluate(board);

        if (isMax) {
            int maxValue = Integer.MIN_VALUE;
            // [UC8 - 8.5] Ưu tiên cột giữa trước để Alpha-Beta cắt được nhiều nhánh hơn
            int[] colOrder = {3, 2, 4, 1, 5, 0, 6};
//           duyệt theo thứ tự ưu tiên cột giữa trước để tìm thấy node đi tốt sớm
//           -> alpha beta cắt được nhiều nhánh thừa hơn
            for (int col : colOrder) {
                if (!board.isValidColumn(col)) continue;

                Board newBoard = board.copyWithMove(col, this.id);
                int eval = minimaxAlphaBeta(false, newBoard, depth - 1,  alpha, beta);

                maxValue = Math.max(maxValue, eval);
                alpha = Math.max(alpha, eval);
                // [UC8 - 8.5] Cắt nhánh Alpha-Beta: bỏ qua nhánh không cần duyệt
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
                // [UC8 - 8.5] Cắt nhánh Alpha-Beta
                if (alpha >= beta)
                    break;
            }
            return minValue;
        }
    }

    // Chỉ chạy khi useAlphaBeta = false
// Khác minimaxAlphaBeta: không cắt nhánh, không ưu tiên cột giữa
// → chậm hơn nhưng logic đơn giản hơn, dùng để so sánh/kiểm thử
    private int minimax(Board board, int depth, boolean isMax) {
        nodesVisited++;
        // [UC8 - Alt 8.1a] Phát hiện thắng/thua
        // Lưu ý: không cộng/trừ depth như minimaxAlphaBeta
        // → không ưu tiên thắng nhanh hay thua muộn
        if (winChecker.checkWin(board, this.id))
            return WIN_SCORE;

        if (winChecker.checkWin(board, humanPlayerId))
            return LOSE_SCORE;
        // [UC8 - 8.4] Node lá: gọi UC9 để lấy điểm đánh giá
        if (depth == 0 || board.isFull())
            return this.heuristic.evaluate(board);

        if (isMax) {
            int maxEval = Integer.MIN_VALUE;
            // [UC8 - 8.2-8.5] Duyệt tuần tự 0→6, không ưu tiên cột giữa
            // → Alpha-Beta sẽ cắt được ít nhánh hơn nếu áp dụng
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
