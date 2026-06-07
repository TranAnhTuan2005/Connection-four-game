package vn.edu.nlu.fit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(6, 7);
    }

    // ─── UC-05: setCell ───────────────────────────────────────────────────────

    @Test
    void setCell_player1_updatesCorrectly() {
        // TC-05-01: Ô cập nhật đúng màu Player1
        board.setCell(5, 3, 1);
        assertEquals(1, board.getCell(5, 3));
    }

    @Test
    void setCell_player2_updatesCorrectly() {
        // TC-05-04: Player2 màu khác Player1
        board.setCell(5, 3, 2);
        assertEquals(2, board.getCell(5, 3));
    }

    @Test
    void setCell_otherCellsUnchanged() {
        // TC-05-01: 41 ô còn lại vẫn = 0
        board.setCell(5, 3, 1);
        assertEquals(0, board.getCell(5, 0));
        assertEquals(0, board.getCell(0, 6));
    }

    @Test
    void setCell_stacksCorrectly() {
        // TC-05-02: Quân chồng đúng hàng
        board.setCell(5, 0, 1);
        board.setCell(4, 0, 2);
        assertEquals(1, board.getCell(5, 0));
        assertEquals(2, board.getCell(4, 0));
        assertEquals(0, board.getCell(3, 0));
    }

    @Test
    void setCell_invalidPosition_throwsException() {
        // setCell ngoài bàn cờ -> exception
        assertThrows(IllegalArgumentException.class,
                () -> board.setCell(-1, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> board.setCell(6, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> board.setCell(0, 7, 1));
    }

    // ─── UC-05: copyWithMove ──────────────────────────────────────────────────

    @Test
    void copyWithMove_placesTokenAtLowestRow() {
        // TC-05-01: Quân đặt đúng hàng thấp nhất
        Board newBoard = board.copyWithMove(3, 1);
        assertNotNull(newBoard);
        assertEquals(1, newBoard.getCell(5, 3));
    }

    @Test
    void copyWithMove_doesNotModifyOriginal() {
        // TC-05-03: Bàn gốc không thay đổi
        board.copyWithMove(3, 1);
        assertEquals(0, board.getCell(5, 3));
    }

    @Test
    void copyWithMove_stacksOnExistingToken() {
        // TC-05-02: Quân mới chồng lên đúng vị trí
        board.setCell(5, 2, 1);
        Board newBoard = board.copyWithMove(2, 2);
        assertEquals(2, newBoard.getCell(4, 2)); // quân mới hàng 4
        assertEquals(1, newBoard.getCell(5, 2)); // quân cũ hàng 5 giữ nguyên
    }

    @Test
    void copyWithMove_fullColumn_returnsNull() {
        // Cột đầy -> null
        for (int row = 5; row >= 0; row--) {
            board.setCell(row, 0, 1);
        }
        assertNull(board.copyWithMove(0, 2));
    }
}