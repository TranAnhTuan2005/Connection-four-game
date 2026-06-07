/**
 * @file    ConnectFourView.java
 * @package vn.edu.nlu.fit.view
 * @desc    JFrame chính của game.
 * THAY ĐỔI v2.0:
 * - Người 1: scoreLabel + highlightWinningCells()
 * - Người 2: undoButton + PieceDropAnimator
 * - Người 3: themeButton + soundCheckBox + applyTheme()
 * - Người 4: timerLabel + difficultyComboBox
 * - Người 5: hintButton + saveButton + loadButton + highlightColumn()
 * @history v1.0 - Tạo mới
 * v2.0 2026-06-01 - Tích hợp tất cả tính năng mở rộng
 */
package vn.edu.nlu.fit.view;

import lombok.Getter;
import vn.edu.nlu.fit.enums.AIDifficulty;
import vn.edu.nlu.fit.enums.GameMode;
import vn.edu.nlu.fit.model.Player;
import vn.edu.nlu.fit.model.WinningCells;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Getter
public class ConnectFourView extends JFrame {
    public static Color themeColor = new Color(30, 144, 255);

    private final int rows;
    private final int cols;

    private JLabel GameModeTitle;
    private JComboBox<GameMode> modeComboBox;
    private final CellPanel[][] cellPanels;
    private final JButton[] colButtons;
    private final JLabel statusLabel;
    private final JButton resetButton;

    // [v2.0 - Người 5] Nút Hint, Save, Load
    private JButton hintButton;
    private JButton saveButton;
    private JButton loadButton;

    // [v2.0 - Người 4] Timer + chọn độ khó AI
    private JLabel timerLabel;
    private JComboBox<AIDifficulty> difficultyComboBox;

    // [v2.0 - Thanh Tú] Hiển thị điểm thắng/thua
    private JLabel scoreLabel;

    // [v2.0 - Nhã Trân] Nút Undo + Animator
    private JButton undoButton;
    private PieceDropAnimator animator;

    // [v2.0 - Người 3] Đổi theme + bật/tắt âm thanh
    private JButton themeButton;
    private JCheckBox soundCheckBox;
    private ThemeManager themeManager;

    // [v2.0 - Thanh Tú] Timer nhấp nháy ô thắng
    private Timer blinkTimer;

    // [v2.0 - Người 5] Theo dõi cột đang highlight và timer để xóa khi nhấn Hint lại
    private int highlightedCol = -1;
    private Timer highlightTimer;

    public ConnectFourView(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cellPanels = new CellPanel[rows][cols];
        this.colButtons = new JButton[cols];
        this.statusLabel = new JLabel("Lượt: Người chơi 1 (Đỏ)");
        this.resetButton = new JButton("Reset");

        // [v2.0] Khởi tạo các thành phần mới từ các thành viên
        this.scoreLabel = new JLabel("Đỏ 0 - 0 Vàng  |  Hòa: 0");
        this.undoButton = new JButton("Undo");
        this.themeButton = new JButton("Dark");
        this.soundCheckBox = new JCheckBox("Âm thanh", true);
        this.timerLabel = new JLabel("30s");
        this.difficultyComboBox = new JComboBox<>(AIDifficulty.values());
        this.difficultyComboBox.setSelectedItem(AIDifficulty.HARD);
        this.hintButton = new JButton("Hint");
        this.saveButton = new JButton("Save");
        this.loadButton = new JButton("Load");
        this.themeManager = new ThemeManager();

        this.setLayout(new BorderLayout(8, 8));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new TitlePanel(), BorderLayout.NORTH);
        wrapper.add(new DropPanel(cols, colButtons), BorderLayout.SOUTH);

        this.add(wrapper, BorderLayout.NORTH);
        this.add(new GameBoardPanel(rows, cols, cellPanels), BorderLayout.CENTER);
        this.add(new StatusPanel(statusLabel, resetButton), BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 900);
        this.setTitle("Connect Four");
        try {
            Image icon = new ImageIcon(getClass().getResource("/logo.png")).getImage();
            this.setIconImage(icon);
        } catch (Exception ignored) {}
        this.setLocationRelativeTo(null);

        // [v2.0 - Nhã Trân] Khởi tạo animator sau khi cellPanels được tạo
        this.animator = new PieceDropAnimator(cellPanels);

        // [v2.0 - Thanh Tú] Timer nhấp nháy ô thắng (500ms toggle)
        this.blinkTimer = new Timer(500, e -> {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    cellPanels[r][c].toggleBlink();
                }
            }
        });

        this.setVisible(true);
    }

    // ========================================================================
    // Inner Panels
    // ========================================================================

    class TitlePanel extends JPanel {
        TitlePanel() {
            super(new BorderLayout());
            this.setBorder(new EmptyBorder(12, 12, 8, 12));

            GameModeTitle = new JLabel("Người vs Người", SwingConstants.CENTER);
            GameModeTitle.setFont(new Font("Arial", Font.BOLD, 24));
            add(GameModeTitle, BorderLayout.CENTER);

            // Phải: ComboBox chế độ chơi + cấp độ AI
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            modeComboBox = new JComboBox<>(GameMode.values());
            modeComboBox.setFont(new Font("Arial", Font.BOLD, 12));
            modeComboBox.setFocusable(false);
            modeComboBox.setBackground(Color.WHITE);
            rightPanel.add(modeComboBox);

            // [v2.0 - Người 4] ComboBox độ khó AI
            difficultyComboBox.setFont(new Font("Arial", Font.BOLD, 12));
            difficultyComboBox.setFocusable(false);
            difficultyComboBox.setBackground(Color.WHITE);
            rightPanel.add(difficultyComboBox);

            add(rightPanel, BorderLayout.EAST);

            // Trái: timer
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            leftPanel.add(timerLabel);
            add(leftPanel, BorderLayout.WEST);
        }
    }

    class DropPanel extends JPanel {
        DropPanel(int cols, JButton[] colButtons) {
            JPanel topPanel = new JPanel(new GridLayout(1, cols, 8, 8));
            topPanel.setOpaque(false);

            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton("\u2193");
                btn.setFont(new Font("Arial", Font.BOLD, 16));
                btn.setFocusable(false);
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);
                btn.setMargin(new Insets(8, 10, 8, 10));
                btn.setPreferredSize(new Dimension(0, 44));
                colButtons[c] = btn;
                topPanel.add(btn);
            }

            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(12, 12, 8, 12));
            add(topPanel, BorderLayout.CENTER);
        }
    }

    class GameBoardPanel extends JPanel {
        GameBoardPanel(int rows, int cols, CellPanel[][] cellPanels) {
            setLayout(new GridLayout(rows, cols, 8, 8));
            setBackground(themeColor);
            setBorder(new EmptyBorder(0, 8, 0, 8));

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    CellPanel cell = new CellPanel();
                    cellPanels[r][c] = cell;
                    add(cell);
                }
            }
        }
    }

    class StatusPanel extends JPanel {
        StatusPanel(JLabel statusLabel, JButton resetButton) {
            setLayout(new BorderLayout(8, 8));
            setBorder(new EmptyBorder(6, 8, 8, 8));

            // Trái: status + score (2 dòng)
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            leftPanel.add(statusLabel);

            // [v2.0 - Thanh Tú] Hiển thị điểm số dưới statusLabel
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            scoreLabel.setForeground(new Color(80, 80, 80));
            leftPanel.add(scoreLabel);

            add(leftPanel, BorderLayout.CENTER);

            // Phải: các nút điều khiển
            JPanel control = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));

            // [v2.0 - Người 5] Hint button
            styleButton(hintButton);
            control.add(hintButton);

            // [v2.0 - Nhã Trân] Undo button
            styleButton(undoButton);
            control.add(undoButton);

            // [v2.0 - Người 5] Save/Load buttons
            styleButton(saveButton);
            control.add(saveButton);
            styleButton(loadButton);
            control.add(loadButton);

            // [v2.0 - Người 3] Theme + Sound
            styleButton(themeButton);
            control.add(themeButton);
            soundCheckBox.setFocusable(false);
            soundCheckBox.setBackground(Color.WHITE);
            control.add(soundCheckBox);

            // Reset button (gốc)
            resetButton.setFocusable(false);
            resetButton.setBackground(Color.WHITE);
            control.add(resetButton);

            add(control, BorderLayout.EAST);
        }

        private void styleButton(JButton btn) {
            btn.setFocusable(false);
            btn.setBackground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
        }
    }

    // ========================================================================
    // Public API - Original
    // ========================================================================

    public GameMode getSelectedGameMode() {
        return (GameMode) this.modeComboBox.getSelectedItem();
    }

    public void setGameModeTitleText(String text) {
        this.GameModeTitle.setText(text);
    }

    public JButton getColButton(int col) {
        return colButtons[col];
    }

    public void updateCell(int row, int col, Player player) {
        this.cellPanels[row][col].setPlayer(player);
    }

    public void updateStatus(String text) {
        this.statusLabel.setText(text);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // ========================================================================
    // Public API - v2.0 (mở rộng)
    // ========================================================================

    /** [v2.0 - Thanh Tú] Cập nhật text điểm số */
    public void updateScore(String text) {
        this.scoreLabel.setText(text);
    }

    /** [v2.0 - Thanh Tú] Highlight các ô thắng và bắt đầu nhấp nháy */
    public void highlightWinningCells(WinningCells winningCells) {
        if (winningCells == null || winningCells.isEmpty()) return;
        for (int[] pos : winningCells.getCells()) {
            cellPanels[pos[0]][pos[1]].setWinning(true);
        }
        blinkTimer.start();
    }

    /** [v2.0 - ThanhTu] Xóa highlight (khi reset hoặc undo) */
    public void clearWinningHighlight() {
        blinkTimer.stop();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cellPanels[r][c].setWinning(false);
            }
        }
    }

    /** [v2.0 - Người 4] Get cấp độ AI đang chọn */
    public AIDifficulty getSelectedDifficulty() {
        return (AIDifficulty) this.difficultyComboBox.getSelectedItem();
    }

    /** [v2.0 - Người 5] Highlight cột được gợi ý trong 2 giây */
    public void highlightColumn(int col) {
        if (col < 0 || col >= cols) return;

        // Xóa highlight cũ trước khi highlight cột mới
        clearColumnHighlight();

        JButton btn = colButtons[col];
        final Color originalBg = btn.getBackground();
        btn.setBackground(new Color(255, 220, 0));  // vàng
        highlightedCol = col;

        highlightTimer = new Timer(2000, e -> {
            btn.setBackground(originalBg);
            highlightedCol = -1;
        });
        highlightTimer.setRepeats(false);
        highlightTimer.start();
    }

    /** Xóa highlight cột gợi ý hiện tại (nếu có) */
    private void clearColumnHighlight() {
        if (highlightTimer != null && highlightTimer.isRunning()) {
            highlightTimer.stop();
        }
        if (highlightedCol >= 0 && highlightedCol < cols) {
            // Khôi phục màu nút theo theme hiện tại
            colButtons[highlightedCol].setBackground(themeManager.getButtonColor());
            highlightedCol = -1;
        }
    }

    /** [v2.0 - Người 3] Áp dụng theme hiện tại lên toàn bộ giao diện */
    public void applyTheme() {
        Color bg = themeManager.getBackgroundColor();
        Color board = themeManager.getBoardColor();
        Color text = themeManager.getTextColor();
        Color btn = themeManager.getButtonColor();

        themeColor = board;

        // Đệ quy cập nhật nền cho tất cả panel con (trừ CellPanel)
        applyBgRecursive(getContentPane(), bg);

        // GameBoardPanel dùng màu bàn cờ riêng
        if (cellPanels[0][0].getParent() != null) {
            cellPanels[0][0].getParent().setBackground(board);
        }

        // Cập nhật CellPanel với màu bàn cờ
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cellPanels[r][c].setBackground(board);
            }
        }

        // Cập nhật màu chữ
        statusLabel.setForeground(text);
        scoreLabel.setForeground(text);
        timerLabel.setForeground(text);
        GameModeTitle.setForeground(text);

        // Cập nhật nút điều khiển
        for (JButton b : new JButton[]{resetButton, undoButton, hintButton,
                saveButton, loadButton, themeButton}) {
            b.setBackground(btn);
            b.setForeground(text);
        }

        // Cập nhật nút cột (DropPanel)
        for (JButton cb : colButtons) {
            cb.setBackground(btn);
            cb.setForeground(text);
        }

        // Cập nhật ComboBox
        modeComboBox.setBackground(btn);
        modeComboBox.setForeground(text);
        difficultyComboBox.setBackground(btn);
        difficultyComboBox.setForeground(text);

        // Cập nhật SoundCheckBox
        soundCheckBox.setBackground(bg);
        soundCheckBox.setForeground(text);

        themeButton.setText(themeManager.isDark() ? "Light" : "Dark");

        repaint();
    }

    /** Đệ quy cập nhật nền cho tất cả container con (bỏ qua CellPanel) */
    private void applyBgRecursive(Container container, Color bg) {
        container.setBackground(bg);
        for (Component comp : container.getComponents()) {
            if (comp instanceof CellPanel) continue;
            if (comp instanceof Container) {
                applyBgRecursive((Container) comp, bg);
            }
        }
    }
}