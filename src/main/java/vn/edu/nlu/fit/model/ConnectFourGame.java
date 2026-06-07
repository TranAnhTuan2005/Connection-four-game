package vn.edu.nlu.fit.model;

import lombok.Getter;

@Getter
public class ConnectFourGame {
    private Board board;
    private PlayerManager playerManager;
    private GameState gameState;
    private WinChecker winChecker;
    private int rows, cols;
    // [update - Thanh Tú] Quản lý điểm số qua nhiều ván
    private ScoreManager scoreManager;
    // [UC17 - Trần Anh Tuấn] Lưu lịch sử nước đi để hỗ trợ chức năng Undo
    private MoveHistory moveHistory;

    public ConnectFourGame(int rows, int cols, WinChecker winChecker) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Board(rows, cols);
        this.playerManager = new PlayerManager();
        this.gameState = new GameState();
        this.winChecker = winChecker;
        this.scoreManager = new ScoreManager();// [update - Thanh Tú]
        this.moveHistory = new MoveHistory();// [UC17 - Trần Anh Tuấn]
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
        // [UC17 - Trần Anh Tuấn] Lưu nước đi vào lịch sử để hỗ trợ Undo
        moveHistory.push(move);

        if (winChecker.checkWin(board, row, col, currentPlayer.getId())) {
            gameState.setWinner(currentPlayer);
            // [update - Thanh Tú] Cộng điểm cho người thắng
            scoreManager.addWin(currentPlayer);

        } else if (board.isFull()) {
            gameState.setDraw();
            // [update - Thanh Tú] Cộng đếm ván hòa
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
        moveHistory.clear();// [UC17 - Trần Anh Tuấn]
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

    /**
     * UC17 – Hoàn tác nước đi gần nhất.
     * @author Trần Anh Tuấn (MSSV: 23130372)
     *
     * Xóa quân khỏi bàn cờ, hoàn tác điểm số (nếu game đã kết thúc),
     * và chuyển lại lượt cho người vừa đi.
     *
     * @return Move vừa hoàn tác, hoặc null nếu không có nước đi nào
     */
    public Move undoLastMove() {
        // UC17: Không có nước đi nào để hoàn tác
        if (moveHistory.isEmpty()) return null;

        // UC17: Lấy nước đi cuối từ stack và xóa quân khỏi board
        Move lastMove = moveHistory.pop();
        board.setCell(lastMove.getRow(), lastMove.getCol(), 0); // 0 = ô trống

        if (gameState.isGameOver()) {
            // UC17: Nếu game đã kết thúc → hoàn tác điểm số và reset gameState
            if (gameState.getWinner() != null) {
                scoreManager.undoWin(gameState.getWinner());
            } else if (gameState.isDraw()) {
                scoreManager.undoDraw();
            }
            gameState.reset();
        } else {
            // UC17: Game chưa over → player đã bị switch → switch ngược lại
            playerManager.switchToNextPlayer();
        }

        return lastMove;
    }

    /**
     * UC17 – Kiểm tra còn lịch sử nước đi để undo không.
     * @author Trần Anh Tuấn (MSSV: 23130372)
     *
     * Controller dùng để hiển thị thông báo khi stack rỗng.
     * @return true nếu còn nước đi trong lịch sử
     */
    public boolean hasMoveHistory() {
        return !moveHistory.isEmpty();
    }

}