package vn.edu.nlu.fit.view;
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
import vn.edu.nlu.fit.model.Player;
import javax.swing.Timer;

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

        // Tính delay mỗi bước:
        //   TOTAL_FRAMES * FRAME_DELAY = 18 * 16 = 288ms ≈ tổng thời gian mục tiêu
        //   Chia đều cho targetRow bước → ms/bước
        //   Giới hạn dưới: FRAME_DELAY (~60fps)
        //   Giới hạn trên: 80ms (tránh quá chậm khi targetRow = 1)
        int stepDelay = Math.max(FRAME_DELAY,
                Math.min(80, (TOTAL_FRAMES * FRAME_DELAY) / targetRow));

        // int[] để gán được bên trong lambda (effectively final)
        int[] currentRow = {0};

        // Timer[] thay cho instance field: gọi được timerRef[0].stop() trong lambda
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
