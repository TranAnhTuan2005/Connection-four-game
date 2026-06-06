package vn.edu.nlu.fit.model;

import lombok.Getter;
import java.util.Stack;

@Getter
public class ConnectFourGame {
    private Board board;
    private PlayerManager playerManager;
    private GameState gameState;
    private WinChecker winChecker;
    private int rows, cols;
    // [update - Thanh Tú] Quản lý điểm số qua nhiều ván
    private ScoreManager scoreManager;
    // [v2.0 - Undo] Lưu lịch sử nước đi để hỗ trợ Undo
    private Stack<Move> moveHistory;

    public ConnectFourGame(int rows, int cols, WinChecker winChecker) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Board(rows, cols);
        this.playerManager = new PlayerManager();
        this.gameState = new GameState();
        this.winChecker = winChecker;
        this.scoreManager = new ScoreManager();// [update - Thanh Tú]
        this.moveHistory = new Stack<>();
    }

    public void setPlayers(Player player1, Player player2){
        playerManager.setPlayers(player1, player2);
    }

    /**
     * Thực hiện nước đi
     * @param col Cột muốn đánh
     * @return Move object nếu thành công, null nếu thất bại
     */
    public Move makeMove(int col) {
        if (gameState.isGameOver() || !board.isValidColumn(col)) {
            return null;
        }

        int row = board.findLowestEmptyRow(col);
        Player currentPlayer = playerManager.getCurrentPlayer();
        board.setCell(row, col, currentPlayer.getId());

        Move move = new Move(row, col, currentPlayer);
        // [v2.0 - Undo] Lưu nước đi vào lịch sử
        moveHistory.push(move);

        if (winChecker.checkWin(board, row, col, currentPlayer.getId())) {
            gameState.setWinner(currentPlayer);
            // [update_ Tú] Cộng điểm cho người thắng
            scoreManager.addWin(currentPlayer);

        } else if (board.isFull()) {
            gameState.setDraw();
            // [update_ Tú] Cộng đếm ván hòa
            scoreManager.addDraw();

        } else {
            playerManager.switchToNextPlayer();
        }

        return move;
    }

    public void reset() {
        this.board = new Board(rows, cols);
        playerManager.reset();
        gameState.reset();
        moveHistory.clear();
    }

    /**
     * [v2.0 - Undo] Hoàn tác nước đi cuối cùng.
     * Xóa quân khỏi bàn cờ, hoàn tác điểm số (nếu game đã kết thúc),
     * và chuyển lại lượt cho người vừa đi.
     *
     * @return Move vừa hoàn tác, hoặc null nếu không có nước đi nào
     */
    public Move undoLastMove() {
        if (moveHistory.isEmpty()) return null;

        Move lastMove = moveHistory.pop();

        // Xóa quân khỏi bàn cờ
        board.setCell(lastMove.getRow(), lastMove.getCol(), 0);

        if (gameState.isGameOver()) {
            // Hoàn tác điểm số nếu game đã kết thúc bởi nước này
            if (gameState.getWinner() != null) {
                scoreManager.undoWin(gameState.getWinner());
            } else if (gameState.isDraw()) {
                scoreManager.undoDraw();
            }
            // Reset trạng thái game về đang chơi
            gameState.reset();
            // Game over → player KHÔNG bị switch trong makeMove → không cần switch lại
        } else {
            // Game chưa over → player ĐÃ bị switch trong makeMove → switch ngược lại
            playerManager.switchToNextPlayer();
        }

        return lastMove;
    }

    /** [v2.0 - Undo] Kiểm tra có nước đi nào trong lịch sử không */
    public boolean hasMoveHistory() {
        return !moveHistory.isEmpty();
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    public Player getCurrentPlayer() {
        return playerManager.getCurrentPlayer();
    }

    public int getRows() {
        return board.getRows();
    }

    public int getCols() {
        return board.getCols();
    }

}
