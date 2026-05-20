/**
 * @file    Board.java
 * @package vn.edu.nlu.fit.model
 * @author  Trần Anh Tuấn (MSSV: 23130372)
 * @date    2026-05-01
 * @version 1.0
 * @desc    Quản lý trạng thái bàn cờ 6×7.
 *          isValidColumn() — thực hiện UC4 bước 4.1.4.
 *          findLowestEmptyRow() + setCell() — thực hiện UC4 bước 4.1.5.
 * @history v1.0 2026-05-01 – Tạo mới
 */
package vn.edu.nlu.fit.model;
import lombok.Getter;
@Getter
public class Board {

    // Số hàng và số cột của bàn cờ (mặc định 6×7)
    private int rows;
    private int cols;

    // UC4 – Bước 4.1.4: cells lưu trạng thái từng ô: 0=trống, 1=Player1, 2=Player2
    private int[][] cells;

    /**
     * UC2c – Bước 2.1.3: Khởi tạo bàn cờ mới với tất cả ô bằng 0 (trống).
     * Được gọi từ ConnectFourGame khi tạo model lần đầu.
     */
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new int[rows][cols];
    }

    /**
     * UC4  – Bước 4.2.1: Copy constructor — tạo bản sao bàn cờ cho AI
     *                     duyệt các nước đi mà không ảnh hưởng bàn cờ thực.
     */
    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.cells = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            System.arraycopy(other.cells[row], 0, this.cells[row], 0, cols);
        }
    }

    /**
     * UC4 – Bước 4.1.4 (Điều kiện 1): Kiểm tra tọa độ (row, col)
     *        có nằm trong phạm vi bàn cờ [0, rows-1] × [0, cols-1] không.
     *
     * @param row  Chỉ số hàng
     * @param col  Chỉ số cột
     * @return true nếu tọa độ hợp lệ
     */
    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * UC4  – Bước 4.1.4: Kiểm tra cột col có hợp lệ để thả quân không.
     *         Điều kiện 1: col nằm trong phạm vi [0, cols-1] (isInsideBoard).
     *         Điều kiện 2: ô trên cùng (row=0) của cột chưa có quân (cells[0][col]==0).
     * UC4.1– Bước 4.1.1.0: Nếu trả về false → cột đầy → kích hoạt UC4.1.
     *
     * Được gọi từ: ConnectFourGame.makeMove(col)
     *           ← ConnectFourController.handleColumnClick(col)
     *
     * @param col  Chỉ số cột người chơi chọn (0–6)
     * @return true nếu cột hợp lệ và còn chỗ trống; false nếu cột đầy hoặc ngoài phạm vi
     */
    public boolean isValidColumn(int col) {
        // UC4 – 4.1.4 Điều kiện 1: col nằm trong phạm vi [0, cols-1]
        // UC4 – 4.1.4 Điều kiện 2: ô trên cùng (row=0) của cột chưa có quân
        return isInsideBoard(0, col) && cells[0][col] == 0;
    }

    /**
     * UC4 – Bước 4.1.5: Kiểm tra có thể thả quân vào ô (row, col) không.
     *        Dùng cho AI khi duyệt cây Minimax.
     */
    public boolean canDropAt(int row, int col) {
        if (!isInsideBoard(row, col)) return false;

        if (cells[row][col] != 0) return false;

        return findLowestEmptyRow(col) == row;
    }


    /**
     * Lấy giá trị ô tại (row, col).
     * @return 0 = trống, 1 = Player1, 2 = Player2
     */
    public int getCell(int row, int col) {
        if (!isInsideBoard(row, col)) {
            throw new IllegalArgumentException("Invalid position: " + row + ", " + col);
        }
        return cells[row][col];
    }

    /**
     * UC4 – Bước 4.1.5: Ghi giá trị playerId vào ô (row, col).
     *        Được gọi sau findLowestEmptyRow() để đặt quân vào vị trí thấp nhất.
     *
     * @param row      Hàng thấp nhất còn trống (kết quả từ findLowestEmptyRow)
     * @param col      Cột người chơi đã chọn
     * @param player   Id của người chơi (1 hoặc 2)
     */
    public void setCell(int row, int col, int player) {
        if (!isInsideBoard(row, col)) {
            throw new IllegalArgumentException("Invalid position: " + row + ", " + col);
        }
        this.cells[row][col] = player;
    }

    /**
     * UC2c – Bước 2.1.3: Kiểm tra bàn cờ đã đầy (dùng để phát hiện hòa).
     * @return true nếu tất cả 7 cột đều có ô trên cùng khác 0
     */
    public boolean isFull() {
        for (int col = 0; col < cols; col++) {
            if (cells[0][col] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * UC4 – Bước 4.1.5: Tìm hàng trống thấp nhất trong cột col
     *        theo cơ chế trọng lực (quân rơi xuống đáy).
     *        Được gọi từ ConnectFourGame.makeMove() sau khi isValidColumn() = true.
     *
     * @param col  Cột cần tìm hàng trống
     * @return chỉ số hàng thấp nhất còn trống, hoặc -1 nếu cột đầy
     */
    public int findLowestEmptyRow(int col) {
        if (!isInsideBoard(0, col)) {
            return -1;
        }
        // Duyệt từ hàng cuối lên trên, trả về hàng đầu tiên còn trống
        for (int row = rows - 1; row >= 0; row--) {
            if (cells[row][col] == 0) {
                return row;
            }
        }
        return -1;  // Cột đầy
    }

    /**
     * UC4 – Bước 4.2.1: Tạo bản sao bàn cờ với nước đi col của playerId.
     *        Dùng trong AIPlayer.chooseColumn() để duyệt Minimax mà không
     *        thay đổi bàn cờ gốc.
     *
     * @param col       Cột AI muốn thả quân
     * @param playerId  Id của AI player
     * @return Board mới có nước đi đã áp dụng, hoặc null nếu cột đầy
     */
    public Board copyWithMove(int col, int playerId) {
        int row = findLowestEmptyRow(col);
        if (row == -1) return null;   // Cột đầy → không thể đặt quân

        Board newBoard = new Board(this);

        newBoard.setCell(row, col, playerId);

        return newBoard;
    }
}
