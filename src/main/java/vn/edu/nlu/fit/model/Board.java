package vn.edu.nlu.fit.model;
import lombok.Getter;
@Getter
public class Board {
    private int rows;
    private int cols;
    private int[][] cells;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new int[rows][cols];
    }

    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.cells = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            System.arraycopy(other.cells[row], 0, this.cells[row], 0, cols);
        }
    }
    public boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    public boolean isValidColumn(int col) {
        return isInsideBoard(0, col) && cells[0][col] == 0;
    }
    public boolean canDropAt(int row, int col) {
        if (!isInsideBoard(row, col)) return false;

        if (cells[row][col] != 0) return false;

        return findLowestEmptyRow(col) == row;
    }


    public int getCell(int row, int col) {
        if (!isInsideBoard(row, col)) {
            throw new IllegalArgumentException("Invalid position: " + row + ", " + col);
        }
        return cells[row][col];
    }
    public void setCell(int row, int col, int player) {
        if (!isInsideBoard(row, col)) {
            throw new IllegalArgumentException("Invalid position: " + row + ", " + col);
        }
        this.cells[row][col] = player;
    }

    public boolean isFull() {
        for (int col = 0; col < cols; col++) {
            if (cells[0][col] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tìm hàng trống thấp nhất trong cột
     * @return row index, hoặc -1 nếu cột đầy
     */
    public int findLowestEmptyRow(int col) {
        if (!isInsideBoard(0, col)) {
            return -1;
        }

        for (int row = rows - 1; row >= 0; row--) {
            if (cells[row][col] == 0) {
                return row;
            }
        }
        return -1;
    }
    public Board copyWithMove(int col, int playerId) {
        int row = findLowestEmptyRow(col);
        if (row == -1) return null;

        Board newBoard = new Board(this);

        newBoard.setCell(row, col, playerId);

        return newBoard;
    }
}
