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
    // [update - Nhã Trân] Lưu lịch sử nước đi để hỗ trợ chức năng Undo
    private MoveHistory moveHistory;

    public ConnectFourGame(int rows, int cols, WinChecker winChecker) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Board(rows, cols);
        this.playerManager = new PlayerManager();
        this.gameState = new GameState();
        this.winChecker = winChecker;
        this.scoreManager = new ScoreManager();// [update - Thanh Tú]
        this.moveHistory = new MoveHistory();//[update - Nhã Trân]
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
        moveHistory.push(move);//[update - Nhã Trân]

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
        moveHistory.clear();//[update - Nhã Trân]
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

    // [update - Nhã Trân] Hoàn tác nước đi gần nhất
    public Move undoLastMove() {
        // Không có nước đi nào để hoàn tác
        if (moveHistory.isEmpty()) return null;

        // Lấy nước đi cuối từ stack và xóa quân khỏi board
        Move lastMove = moveHistory.pop();
        board.setCell(lastMove.getRow(), lastMove.getCol(), 0); // 0 = ô trống

        if (gameState.isGameOver()) {
            // Nếu vừa có người thắng → reset gameState về đang chơi
            gameState.reset();
        } else {
            // Undo xong → trả lượt về người đã đi nước đó
            playerManager.switchToNextPlayer();
        }

        return lastMove;
    }

}