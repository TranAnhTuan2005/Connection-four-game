package vn.edu.nlu.fit.view;

import vn.edu.nlu.fit.model.Player;

import javax.swing.*;
import java.awt.*;

import vn.edu.nlu.fit.model.Player;

import static vn.edu.nlu.fit.view.ConnectFourView.themeColor;

/**
 * Lớp CellPanel đại diện cho một ô cờ trên bàn chơi Connect Four.
 *
 * Chức năng:
 * - Hiển thị quân cờ của người chơi
 * - Vẽ giao diện hình tròn cho từng ô
 * - Cập nhật màu quân cờ theo Player
 */
public class CellPanel extends JPanel {

    // Người chơi hiện tại sở hữu ô cờ
    private Player player;
    // [v2.0 - Thanh Tú] Đánh dấu nếu ô này thuộc đường thắng
    private boolean isWinning = false;

    // [v2.0 - Thanh Tú] Trạng thái nhấp nháy (toggle bởi Timer ngoài)
    private boolean blinkOn = true;
    /**
     * Constructor khởi tạo ô cờ
     */
    public CellPanel() {

        // Thiết lập kích thước mặc định của ô cờ
        this.setPreferredSize(new Dimension(90, 90));

        // Thiết lập màu nền theo theme của game
        this.setBackground(themeColor);

        // Cho phép hiển thị nền
        this.setOpaque(true);
    }

    /**
     * Cập nhật người chơi cho ô cờ
     * Sau khi cập nhật sẽ vẽ lại giao diện
     * @param player người chơi sở hữu quân cờ
     */
    public void setPlayer(Player player) {

        this.player = player;

        // Yêu cầu Swing vẽ lại component
        repaint();
    }

    /**
     * Phương thức dùng để vẽ giao diện ô cờ
     * @param g đối tượng Graphics dùng để vẽ
     */
    /** [v2.0 - Thanh Tú] Đặt cờ này là ô thắng (để highlight) */
    public void setWinning(boolean winning) {
        this.isWinning = winning;
        repaint();
    }
    /** [v2.0 - Thanh Tú] Toggle nhấp nháy - gọi từ Timer trong View */
    public void toggleBlink() {
        if (isWinning) {
            blinkOn = !blinkOn;
            repaint();
        }
    }
    public boolean isWinning() {
        return isWinning;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        int pad = Math.max(10, getWidth() / 12);
        int size = Math.min(getWidth(), getHeight()) - pad * 2;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        Color fillColor;

        if (player == null) {
            fillColor = new Color(230, 230, 230);
        } else {
            fillColor = player.getColor();
        }

        g2.setColor(fillColor);
        g2.fillOval(x, y, size, size);

        // [v2.0 - Người 1] Vẽ viền vàng nhấp nháy nếu là ô thắng
        if (isWinning && blinkOn) {
            g2.setColor(new Color(255, 215, 0));  // gold
            g2.setStroke(new BasicStroke(5f));
            g2.drawOval(x - 2, y - 2, size + 4, size + 4);
        } else {
            g2.setColor(new Color(20, 20, 20, 200));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(x, y, size, size);
        }

        g2.dispose();
    }
}