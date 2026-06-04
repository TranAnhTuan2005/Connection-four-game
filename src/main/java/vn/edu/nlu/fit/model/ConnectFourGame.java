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

    public ConnectFourGame(int rows, int cols, WinChecker winChecker) {
        this.rows = rows;
        this.cols = cols;
        this.board = new Board(rows, cols);
        this.playerManager = new PlayerManager();
        this.gameState = new GameState();
        this.winChecker = winChecker;
        this.scoreManager = new ScoreManager();// [update - Thanh Tú]
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
