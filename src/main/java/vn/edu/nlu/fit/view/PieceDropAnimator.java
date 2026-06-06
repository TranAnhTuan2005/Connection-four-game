package vn.edu.nlu.fit.view;
/**
 * @file    PieceDropAnimator.java
 * @package vn.edu.nlu.fit.model
 * @author  [Nhã Trân - 23130343]
 * @date    2026-06-06
 * @version 1.0
 * @desc    Tạo hiệu ứng quân cờ rơi từ hàng 0 xuống hàng đích (targetRow).
 *          Dùng Swing Timer 60fps (~16ms/frame), tổng ~300ms (18 frame).
 *          Được ConnectFourController gọi sau mỗi makeMove() thành công,
 *          thay thế cho view.updateCell() trực tiếp.
 * @history v1.0 2026-06-06 - Tạo mới
 */

import vn.edu.nlu.fit.model.Player;
import javax.swing.Timer;
public class PieceDropAnimator {

    // --- Hằng số điều chỉnh tốc độ animation ---

    private static final int FRAME_DELAY = 16;   // ~60fps — delay tối thiểu mỗi frame (ms)
    private static final int TOTAL_FRAMES = 18;   // tổng số frame mục tiêu → ≈300ms

    // --- Trạng thái ---

    /**
     * Ma trận CellPanel của ConnectFourView — tham chiếu trực tiếp.
     */
    private final CellPanel[][] cellPanels;

    // =========================================================================
    // Constructor
    // =========================================================================

    /**
     * @param cellPanels Ma trận CellPanel của ConnectFourView (rows × cols).
     *                   Giữ tham chiếu trực tiếp, không copy.
     */
    public PieceDropAnimator(CellPanel[][] cellPanels) {
        this.cellPanels = cellPanels;
    }

    // =========================================================================
    // Public API
    // =========================================================================

    public void animateDrop(int targetRow, int col, Player player, Runnable onComplete) {

        // Trường hợp đặc biệt: quân rơi đúng vào hàng đầu → không cần animate
        if (targetRow == 0) {
            cellPanels[0][col].setPlayer(player);
            if (onComplete != null) onComplete.run();
            return;
        }

        // Tính delay mỗi bước:
        //   TOTAL_FRAMES * FRAME_DELAY = 18 * 16 = 288ms ≈ tổng thời gian mục tiêu
        //   Chia đều cho targetRow bước → ms/bước
        //   Giới hạn dưới: FRAME_DELAY (~60fps)
        //   Giới hạn trên: 80ms (tránh quá chậm khi targetRow = 1)
        //
        //   Ví dụ (board 6 hàng):
        //     targetRow=5 → max(16, min(80, 288/5)) = 57ms/bước → tổng 285ms
        //     targetRow=3 → max(16, min(80, 288/3)) = 80ms/bước → tổng 240ms
        //     targetRow=1 → max(16, min(80, 288/1)) = 80ms/bước → tổng  80ms
        int stepDelay = Math.max(FRAME_DELAY,
                Math.min(80, (TOTAL_FRAMES * FRAME_DELAY) / targetRow));

        // int[] để có thể gán bên trong lambda (effectively final)
        int[] currentRow = {0};

        // Timer[] thay cho instance field: giúp gọi timerRef[0].stop()
        // bên trong lambda mà không cần instance field (effectively final)
        Timer[] timerRef = {null};

        // Hiện quân ở hàng 0 ngay lập tức, trước khi timer bắt đầu
        cellPanels[0][col].setPlayer(player);

        timerRef[0] = new Timer(stepDelay, null);
        timerRef[0].addActionListener(e -> {
            // Xóa quân khỏi hàng hiện tại
            cellPanels[currentRow[0]][col].setPlayer(null);

            // Di chuyển xuống một hàng
            currentRow[0]++;

            // Hiện quân ở hàng mới
            cellPanels[currentRow[0]][col].setPlayer(player);

            // Đến đích → dừng timer, gọi callback
            if (currentRow[0] >= targetRow) {
                timerRef[0].stop();
                if (onComplete != null) onComplete.run();
            }
        });

        timerRef[0].start();
    }
}