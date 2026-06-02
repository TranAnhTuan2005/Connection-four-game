package vn.edu.nlu.fit.model;
import lombok.Getter;

@Getter
public class ScoreManager {
    private int player1Score;
    private int player2Score;
    private int drawCount;

    public ScoreManager() {
        reset();
    }

    /** Cộng điểm cho người thắng */
    public void addWin(Player winner) {
        if (winner == null) return;
        if (winner.getId() == 1) player1Score++;
        else if (winner.getId() == 2) player2Score++;
    }

    /** Tăng đếm số ván hòa */
    public void addDraw() {
        drawCount++;
    }

    /** Reset toàn bộ điểm (khi đổi chế độ chơi) */
    public void reset() {
        player1Score = 0;
        player2Score = 0;
        drawCount = 0;
    }

    /** Chuỗi hiển thị: "Đỏ 2 - 1 Vàng | Hòa: 0" */
    public String getDisplayText(String name1, String name2) {
        return String.format("%s %d - %d %s  |  Hòa: %d",
                name1, player1Score, player2Score, name2, drawCount);
    }
}