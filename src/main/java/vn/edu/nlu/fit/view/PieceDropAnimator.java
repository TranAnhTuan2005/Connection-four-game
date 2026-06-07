package vn.edu.nlu.fit.view;
/**
 * @file    PieceDropAnimator.java
 * @package vn.edu.nlu.fit.view
 * @author  [Người 2]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Tạo hiệu ứng quân cờ rơi từ trên xuống vị trí đích.
 *          Sử dụng Swing Timer 60fps, tổng thời gian ~300ms.
 * @history v1.0 2026-06-06 - Tạo mới
 */
import vn.edu.nlu.fit.model.Player;
import javax.swing.Timer;
import java.awt.event.ActionListener;

public class PieceDropAnimator {

    private static final int FRAME_DELAY  = 16;   // ~60fps — delay tối thiểu mỗi frame (ms)
    private static final int TOTAL_FRAMES = 18;   // tổng số frame mục tiêu → ≈300ms

    private final CellPanel[][] cellPanels;

    /**
     * @param cellPanels Ma trận CellPanel của ConnectFourView (rows × cols).
     *                   Giữ tham chiếu trực tiếp, không copy.
     */
    public PieceDropAnimator(CellPanel[][] cellPanels) {
        this.cellPanels = cellPanels;
    }

    /**
     * Chạy animation quân rơi từ hàng 0 xuống targetRow trong cột col.
     *
     * Luồng thực thi:
     *   Trước timer    : cellPanels[0][col].setPlayer(player)
     *   Tick 1         : clear row 0  → show row 1
     *   Tick 2         : clear row 1  → show row 2
     *   ...
     *   Tick targetRow : clear row (targetRow-1) → show targetRow → STOP → onComplete()
     *
     * Trường hợp đặc biệt:
     *   targetRow == 0 → đặt trực tiếp, gọi ngay onComplete (không cần animate).
     *
     * @param targetRow  Hàng đích — từ Board.findLowestEmptyRow(), đã hợp lệ.
     * @param col        Cột người chơi / AI vừa chọn (0-indexed).
     * @param player     Player sở hữu quân cờ.
     * @param onComplete Callback trên EDT sau khi animation kết thúc.
     */
    public void animateDrop(int targetRow, int col, Player player, Runnable onComplete) {

        // Trường hợp đặc biệt: quân rơi đúng vào hàng đầu → đặt trực tiếp
        if (targetRow == 0) {
            cellPanels[0][col].setPlayer(player);
            if (onComplete != null) onComplete.run();
            return;
        }
        // Số frame để di chuyển qua mỗi hàng
        // Ví dụ: targetRow=5 → framesPerCell = max(1, 18/6) = 3 tick/hàng
        //        targetRow=2 → framesPerCell = max(1, 18/3) = 6 tick/hàng (chậm hơn)
        final int framesPerCell = Math.max(1, TOTAL_FRAMES / (targetRow + 1));

        // Đếm số tick và vị trí hàng hiện tại — dùng int[] để gán được trong lambda
        final int[] currentRow = {0};
        final int[] frame      = {0};


        // Tạo Timer tick mỗi FRAME_DELAY ms, listener gán sau để dùng được trong lambda
        Timer timer = new Timer(FRAME_DELAY, null);

        ActionListener tick = e -> {
            frame[0]++; // đếm số tick đã qua
            // Mỗi framesPerCell tick → đủ thời gian để di chuyển xuống 1 hàng
            if (frame[0] % framesPerCell == 0 && currentRow[0] < targetRow) {
                // Xóa quân ở hàng trước (hàng 0 là lần đầu nên chưa có gì để xóa)
                if (currentRow[0] > 0) {
                    cellPanels[currentRow[0] - 1][col].setPlayer(null);
                }
            // Xóa quân khỏi hàng hiện tại
            cellPanels[currentRow[0]][col].setPlayer(null);

            // Di chuyển xuống một hàng
            currentRow[0]++;
        }
            // Hiện quân ở hàng mới
            cellPanels[currentRow[0]][col].setPlayer(player);

            // Đến đích → dừng timer, gọi callback
            if (currentRow[0] >= targetRow) {
                ((Timer) e.getSource()).stop();
                if (onComplete != null) onComplete.run();
            }
        };

        timer.start();
    }
}
