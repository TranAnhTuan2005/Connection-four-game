package vn.edu.nlu.fit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
class ConnectFourGameTest {

    private ConnectFourGame game;

    @BeforeEach
    void setUp() {
        ConnectFourWinChecker winChecker = new ConnectFourWinChecker(4);
        game = new ConnectFourGame(6, 7, winChecker);
        game.setPlayers(
                new HumanPlayer(1, "Player1", Color.RED),
                new HumanPlayer(2, "Player2", Color.YELLOW)
        );
    }

    // ─── UC-03: makeMove – Happy Path ─────────────────────────────────────────

    @Test
    void makeMove_validColumn_returnsMoveNotNull() {
        // TC-03-01: Thả quân cột hợp lệ -> Move khác null
        assertNotNull(game.makeMove(3));
    }

    @Test
    void makeMove_validColumn_correctRowAndCol() {
        // TC-03-01: Move chứa đúng row=5, col=3, Player1
        Move move = game.makeMove(3);
        assertEquals(5, move.getRow());
        assertEquals(3, move.getCol());
        assertEquals(1, move.getPlayer().getId());
    }

    @Test
    void makeMove_secondMove_stacksOnTop() {
        // TC-03-02: Quân chồng đúng vị trí
        game.makeMove(0); // P1 -> hàng 5
        game.makeMove(0); // P2 -> hàng 4
        assertEquals(5, game.getBoard().getCell(5, 0) == 1 ? 5 : -1);
        assertEquals(2, game.getBoard().getCell(4, 0));
    }

    @Test
    void makeMove_switchesPlayer() {
        // UC-08: Sau nước đi hợp lệ -> chuyển lượt
        assertEquals(1, game.getCurrentPlayer().getId());
        game.makeMove(0);
        assertEquals(2, game.getCurrentPlayer().getId());
    }

    // ─── UC-03: makeMove – Negative ───────────────────────────────────────────

    @Test
    void makeMove_fullColumn_returnsNull() {
        // TC-03-03: Cột đầy -> null
        for (int i = 0; i < 6; i++) game.makeMove(0);
        assertNull(game.makeMove(0));
    }

    @Test
    void makeMove_gameOver_returnsNull() {
        // TC-03-05: Game kết thúc -> không nhận nước đi
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); // P1 thắng dọc
        assertNull(game.makeMove(2));
    }

    // ─── UC-03: makeMove – Win/Draw ───────────────────────────────────────────

    @Test
    void makeMove_fourInRow_gameOver() {
        // TC-03-04: 4 quân ngang -> thắng
        game.makeMove(0); game.makeMove(0);
        game.makeMove(1); game.makeMove(1);
        game.makeMove(2); game.makeMove(2);
        game.makeMove(3); // P1 thắng ngang
        assertTrue(game.isGameOver());
    }

    @Test
    void makeMove_fourInColumn_gameOver() {
        // 4 quân dọc -> thắng
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); game.makeMove(1);
        game.makeMove(0); // P1 thắng dọc
        assertTrue(game.isGameOver());
    }
}