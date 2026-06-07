package vn.edu.nlu.fit.model;

/**
 * @file    MoveHistory.java
 * @package vn.edu.nlu.fit.model
 * @author  [Nhã Trân - 23130343]
 * @date    2026-06-06
 * @version 1.0
 * @desc    Lưu lịch sử các nước đi bằng stack (LIFO).
 *          push()  — ghi nhận nước đi mới vào stack.
 *          pop()   — lấy ra và xóa nước đi cuối (dùng cho Undo).
 *          peek()  — xem nước đi cuối mà không xóa.
 *          Được ConnectFourGame.makeMove() gọi push() mỗi lượt đi.
 *          Được ConnectFourGame.undoLastMove() gọi pop() khi hoàn tác.
 * @history v1.0 2026-06-06 - Tạo mới
 */

import java.util.ArrayDeque;
import java.util.Deque;

public class MoveHistory {

    private final Deque<Move> stack = new ArrayDeque<>();

    /**
     * Đẩy nước đi mới vào đỉnh stack.
     * Gọi từ ConnectFourGame.makeMove() sau khi đặt quân thành công.
     *
     * @param move Nước đi vừa thực hiện (không được null)
     */
    public void push(Move move) {
        if (move != null) stack.push(move);
    }

    /**
     * Lấy và xóa nước đi ở đỉnh stack (nước đi gần nhất).
     * Dùng cho Undo — gọi từ ConnectFourGame.undoLastMove().
     *
     * @return nước đi cuối cùng, hoặc null nếu stack rỗng
     */
    public Move pop() {
        return stack.isEmpty() ? null : stack.pop();
    }

    /**
     * Kiểm tra stack có rỗng không.
     * Dùng trong Controller để disable nút Undo khi không còn nước đi.
     *
     * @return true nếu chưa có nước đi nào được lưu
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }


    /**
     * Xem nước đi ở đỉnh stack mà không xóa.
     * Hữu ích để kiểm tra trước khi quyết định undo.
     *
     * @return nước đi cuối cùng, hoặc null nếu stack rỗng
     */
    public Move peek() {
        return stack.peek();
    }

    /**
     * Trả về số lượng nước đi đang lưu trong stack.
     *
     * @return số nước đi trong lịch sử
     */
    public int size() {
        return stack.size();
    }

    /**
     * Xóa toàn bộ lịch sử nước đi.
     * Gọi từ ConnectFourGame.reset() khi bắt đầu ván mới.
     */
    public void clear() {
        stack.clear();
    }
}

