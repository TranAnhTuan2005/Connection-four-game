/**
 * @file    HumanPlayer.java
 * @package vn.edu.nlu.fit.model
 * @author  Trần Anh Tuấn (MSSV: 23130372)
 * @date    2026-05-01
 * @version 1.0
 * @desc    Người chơi thật — kế thừa Player, isAI() luôn trả về false.
 *          UC2a – Bước 2.2.2: startPvP() tạo 2 HumanPlayer (Đỏ + Vàng).
 *          UC2b – Bước 2.2.2: startPvE() tạo 1 HumanPlayer (Đỏ).
 * @history v1.0 2026-05-01 – Tạo mới
 */
package vn.edu.nlu.fit.model;

import java.awt.*;

public class HumanPlayer extends Player {

    /**
     * UC2 – Bước 2.2.2: Khởi tạo người chơi thật với id, tên và màu quân.
     * Gọi super(id, name, color) để lưu vào các trường của Player.
     */
    public HumanPlayer(int id, String name, Color color) {
        super(id, name, color);
    }

    /**
     * UC2  – Bước 2.2.2: isAI() = false → đây là người chơi thật.
     * UC4  – Bước 4.2.1: Controller kiểm tra isAI() để KHÔNG kích hoạt AI
     *                     sau khi HumanPlayer đi xong.
     *
     * @return false — HumanPlayer không phải AI
     */
    @Override
    public boolean isAI() {
        return false;
    }
}
