/**
 * @file    PieceDropAnimator.java
 * @package vn.edu.nlu.fit.view
 * @author  [Người 2]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Tạo hiệu ứng quân cờ rơi từ trên xuống vị trí đích.
 *          Sử dụng Swing Timer 60fps, tổng thời gian ~300ms.
 * @history v1.0 2026-06-01 - Tạo mới
 */
package vn.edu.nlu.fit.view;

import vn.edu.nlu.fit.model.Player;

import javax.swing.Timer;
import java.awt.event.ActionListener;

public class PieceDropAnimator {

    private static final int FRAME_DELAY = 16;        // ≈60fps
    private static final int TOTAL_FRAMES = 18;       // ≈300ms

    private final CellPanel[][] cellPanels;

    public PieceDropAnimator(CellPanel[][] cellPanels) {
        this.cellPanels = cellPanels;
    }

    /**
     * Animation quân rơi từ hàng 0 xuống targetRow ở cột col.
     *
     * @param targetRow  Hàng đích
     * @param col        Cột
     * @param player     Người chơi
     * @param onComplete Callback sau khi animation kết thúc
     */
    public void animateDrop(int targetRow, int col, Player player, Runnable onComplete) {
        // Trường hợp đặc biệt: thả vào hàng 0 → đặt luôn, không animation
        if (targetRow == 0) {
            cellPanels[0][col].setPlayer(player);
            if (onComplete != null) onComplete.run();
            return;
        }

        final int[] currentRow = {0};
        final int[] frame = {0};
        final int framesPerCell = Math.max(1, TOTAL_FRAMES / (targetRow + 1));

        Timer timer = new Timer(FRAME_DELAY, null);
        ActionListener tick = e -> {
            frame[0]++;
            // Mỗi framesPerCell tick → di chuyển xuống 1 hàng
            if (frame[0] % framesPerCell == 0 && currentRow[0] < targetRow) {
                // Xóa ô cũ
                if (currentRow[0] > 0) {
                    cellPanels[currentRow[0] - 1][col].setPlayer(null);
                }
                // Tô ô mới
                cellPanels[currentRow[0]][col].setPlayer(player);
                currentRow[0]++;
            }
            // Kết thúc animation
            if (currentRow[0] >= targetRow) {
                if (currentRow[0] > 0) {
                    cellPanels[currentRow[0] - 1][col].setPlayer(null);
                }
                cellPanels[targetRow][col].setPlayer(player);
                ((Timer) e.getSource()).stop();
                if (onComplete != null) onComplete.run();
            }
        };
        timer.addActionListener(tick);
        timer.start();
    }
}
