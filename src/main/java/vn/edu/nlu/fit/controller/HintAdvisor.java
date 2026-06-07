/**
 * @file    HintAdvisor.java
 * @package vn.edu.nlu.fit.controller
 * @author  Trần Anh Tuấn (MSSV: 23130372)
 * @desc    UC12 – Gợi ý nước đi tốt nhất cho người chơi hiện tại.
 *          Tận dụng AIPlayer.chooseColumn() với depth=4 để gợi ý nhanh.
 *          View highlight cột được gợi ý bằng màu vàng trong 2 giây.
 */
package vn.edu.nlu.fit.controller;

import vn.edu.nlu.fit.heuristic.ConnectFourHeuristic;
import vn.edu.nlu.fit.model.AIPlayer;
import vn.edu.nlu.fit.model.Board;
import vn.edu.nlu.fit.model.ConnectFourGame;
import vn.edu.nlu.fit.model.Player;

import java.awt.Color;

public class HintAdvisor {

    /**
     * UC12 – Trả về cột mà người chơi hiện tại nên đi.
     * @author Trần Anh Tuấn (MSSV: 23130372)
     *
     * Tạo AIPlayer tạm thời với depth=4 để phân tích nhanh,
     * sử dụng bản sao bàn cờ để không ảnh hưởng game thực.
     *
     * @param game  Game hiện tại
     * @return chỉ số cột nên đi, -1 nếu không có cột hợp lệ
     */
    public int suggestColumn(ConnectFourGame game) {
        Player current = game.getCurrentPlayer();
        if (current == null) return -1;

        // Đối thủ là người chơi còn lại
        int opponentId = (current.getId() == 1) ? 2 : 1;

        // Tạo AI tạm thời để phân tích nước đi tốt nhất cho người chơi hiện tại
        AIPlayer advisor = new AIPlayer(
                current.getId(),
                "Advisor",
                Color.GRAY,
                opponentId,
                game.getWinChecker(),
                new ConnectFourHeuristic(current.getId(), opponentId)
        );

        // Dùng depth thấp (4) để gợi ý nhanh, không cần độ chính xác cao
        advisor.setSearchDepth(4);

        // Dùng bản sao bàn cờ để tránh ảnh hưởng game thực
        Board boardCopy = new Board(game.getBoard());
        return advisor.chooseColumn(boardCopy);
    }
}