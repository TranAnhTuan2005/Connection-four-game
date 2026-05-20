/**
 * @file    Player.java
 * @package vn.edu.nlu.fit.model
 * @author  Trần Anh Tuấn (MSSV: 23130372)
 * @date    2026-05-01
 * @version 1.0
 * @desc    Lớp trừu tượng định nghĩa người chơi.
 *          Dùng trong UC2a (startPvP) và UC2b (startPvE):
 *          HumanPlayer và AIPlayer đều kế thừa lớp này.
 * @history v1.0 2026-05-01 – Tạo mới
 */
package vn.edu.nlu.fit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.awt.*;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Player {

    // UC2 – Bước 2.2.2: id phân biệt Player 1 (Human) và Player 2 (Human/AI)
    protected final int id;

    // UC2 – Bước 2.1.5: name dùng để hiển thị trên updateStatus()
    protected final String name;

    // UC4  – Bước 4.1.6: color dùng để vẽ quân cờ trên CellPanel
    protected final Color color;

    /**
     * UC2 – Bước 2.2.2: Constructor khởi tạo người chơi với id, tên và màu quân cờ.
     * Được gọi từ startPvP() và startPvE() trong ConnectFourController.
     *
     * @param id    Định danh người chơi (1 hoặc 2)
     * @param name  Tên hiển thị (VD: "Người chơi Đỏ", "Bạn", "Máy")
     * @param color Màu quân cờ hiển thị trên bàn cờ
     */
    public Player(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    /**
     * UC2  – Bước 2.2.2: Phân biệt người chơi thật hay AI.
     * UC4  – Bước 4.2.1: Controller dùng isAI() để quyết định
     *                     có kích hoạt AIPlayer.chooseColumn() hay không.
     *
     * @return true nếu là AIPlayer, false nếu là HumanPlayer
     */
    public abstract boolean isAI();


}
