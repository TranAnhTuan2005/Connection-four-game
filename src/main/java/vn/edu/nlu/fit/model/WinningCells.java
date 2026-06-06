package vn.edu.nlu.fit.model;
/**
 * @file    WinningCells.java
 * @package vn.edu.nlu.fit.model
 * @author  [Thanh Tú - 23130365]
 * @date    2026-06-02
 * @version 2.0
 * @desc    Lưu danh sách các ô tạo nên đường thắng.
 *          ConnectFourWinChecker điền dữ liệu khi phát hiện thắng.
 *          CellPanel dùng để highlight nhấp nháy ô thắng.
 */
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WinningCells {
    private final List<int[]> cells = new ArrayList<>();

    public void add(int row, int col) {
        cells.add(new int[]{row, col});
    }

    public void clear() {
        cells.clear();
    }

    public boolean isEmpty() {
        return cells.isEmpty();
    }

    /** Kiểm tra ô (row, col) có thuộc đường thắng không */
    public boolean contains(int row, int col) {
        for (int[] c : cells) {
            if (c[0] == row && c[1] == col) return true;
        }
        return false;
    }
}
