package vn.edu.nlu.fit.view;

import javax.swing.*;
import java.awt.*;

import static vn.edu.nlu.fit.view.ConnectFourView.themeColor;

public class CellPanel extends JPanel {
    private Player player;

    public CellPanel() {
        this.setPreferredSize(new Dimension(90, 90));
        this.setBackground(themeColor);
        this.setOpaque(true);
    }

    public void setPlayer(Player player) {
        this.player = player;
        repaint();
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

        }else{
            fillColor = player.getColor();
        }

        g2.setColor(fillColor);
        g2.fillOval(x, y, size, size);


        // Border
        g2.setColor(new Color(20, 20, 20, 200));
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(x, y, size, size);

        g2.dispose();
    }
}