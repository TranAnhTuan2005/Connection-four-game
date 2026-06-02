/**
 * @file    CellPanel.java
 * @package vn.edu.nlu.fit.view
 * @desc    Ô cờ trên bàn chơi Connect Four.
 *          THAY ĐỔI v2.0: Highlight nhấp nháy khi là ô thắng (Người 1).
 * @history v1.0 - Tạo mới
 *          v2.0 2026-06-01 - Thêm highlight ô thắng + nhấp nháy
 */
package vn.edu.nlu.fit.view;

import vn.edu.nlu.fit.model.Player;

import javax.swing.*;
import java.awt.*;

import static vn.edu.nlu.fit.view.ConnectFourView.themeColor;

public class CellPanel extends JPanel {

    private Player player;

    // [v2.0 - Người 1] Đánh dấu nếu ô này thuộc đường thắng
    private boolean isWinning = false;

    // [v2.0 - Người 1] Trạng thái nhấp nháy (toggle bởi Timer ngoài)
    private boolean blinkOn = true;

    public CellPanel() {
        this.setPreferredSize(new Dimension(90, 90));
        this.setBackground(themeColor);
        this.setOpaque(true);
    }

    public void setPlayer(Player player) {
        this.player = player;
        repaint();
    }

    /** [v2.0 - Người 1] Đặt cờ này là ô thắng (để highlight) */
    public void setWinning(boolean winning) {
        this.isWinning = winning;
        repaint();
    }

    /** [v2.0 - Người 1] Toggle nhấp nháy - gọi từ Timer trong View */
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
