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
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // Tạo đối tượng Graphics2D để hỗ trợ vẽ nâng cao
        Graphics2D g2 = (Graphics2D) g.create();

        // Bật anti-aliasing giúp hình tròn mượt hơn
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Tăng chất lượng render
        g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Khoảng cách padding của quân cờ
        int pad = Math.max(10, getWidth() / 12);

        // Kích thước hình tròn quân cờ
        int size = Math.min(getWidth(), getHeight()) - pad * 2;

        // Tọa độ X của quân cờ
        int x = (getWidth() - size) / 2;

        // Tọa độ Y của quân cờ
        int y = (getHeight() - size) / 2;

        // Biến lưu màu quân cờ
        Color fillColor;

        // Nếu ô chưa có quân cờ -> hiển thị màu xám
        if (player == null) {

            fillColor = new Color(230, 230, 230);

        } else {

            // Nếu có quân cờ -> lấy màu từ Player
            fillColor = player.getColor();
        }

        // Thiết lập màu quân cờ
        g2.setColor(fillColor);

        // Vẽ hình tròn quân cờ
        g2.fillOval(x, y, size, size);

        // Vẽ viền quân cờ

        g2.setColor(new Color(20, 20, 20, 200));

        // Độ dày viền
        g2.setStroke(new BasicStroke(2f));

        // Vẽ viền hình tròn
        g2.drawOval(x, y, size, size);

        // Giải phóng tài nguyên đồ họa
        g2.dispose();
    }
}