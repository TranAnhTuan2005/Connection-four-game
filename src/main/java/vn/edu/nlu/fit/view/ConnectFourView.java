package vn.edu.nlu.fit.view;

import vn.edu.nlu.fit.enums.GameMode;
import vn.edu.nlu.fit.model.Player;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Lớp ConnectFourView chịu trách nhiệm xây dựng và quản lý
 * toàn bộ giao diện của trò chơi Connect Four.
 * Bao gồm:
 * - Tiêu đề và chọn chế độ chơi
 * - Bàn cờ
 * - Nút thả quân
 * - Thanh trạng thái
 * - Hiển thị thông báo kết quả
 */
public class ConnectFourView extends JFrame {
    // Màu giao diện chính của trò chơi
    public static Color themeColor = new Color(30, 144, 255);
    // Số hàng và số cột của bàn cờ
    private final int rows;
    private final int cols;
    // Tiêu đề hiển thị chế độ chơi hiện tại
    private JLabel GameModeTitle;
    // ComboBox dùng để chọn chế độ chơi
    private JComboBox<GameMode> modeComboBox;
    // Ma trận các ô cờ giao diện
    private final CellPanel[][] cellPanels;
    // Danh sách nút thả quân theo cột
    private final JButton[] colButtons;
    // Label hiển thị trạng thái/lượt chơi
    private final JLabel statusLabel;
    // Nút reset trò chơi
    private final JButton resetButton;
    /**
     * Constructor khởi tạo giao diện chính của game
     * @param rows số hàng bàn cờ
     * @param cols số cột bàn cờ
     */
    public ConnectFourView(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        // Khởi tạo ma trận ô cờ
        this.cellPanels = new CellPanel[rows][cols];
        // Khởi tạo các nút thả quân
        this.colButtons = new JButton[cols];
        // Khởi tạo label trạng thái ban đầu
        this.statusLabel = new JLabel("Lượt: Người chơi 1 (Đỏ)");
        // Khởi tạo nút reset
        this.resetButton = new JButton("Reset");
        // Thiết lập layout tổng thể
        this.setLayout(new BorderLayout(8, 8));
        // Wrapper chứa phần tiêu đề và nút thả quân
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new TitlePanel(), BorderLayout.NORTH);
        wrapper.add(new DropPanel(cols, colButtons), BorderLayout.SOUTH);
        // Thêm các thành phần vào giao diện chính
        this.add(wrapper, BorderLayout.NORTH);
        this.add(new GameBoardPanel(rows, cols, cellPanels), BorderLayout.CENTER);
        this.add(new StatusPanel(statusLabel, resetButton), BorderLayout.SOUTH);
        // Cấu hình cửa sổ
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 800);
        this.setTitle("Connect Four");
        // Thiết lập icon ứng dụng
        Image icon = new ImageIcon(getClass().getResource("/logo.png")).getImage();
        this.setIconImage(icon);
        // Hiển thị cửa sổ giữa màn hình
        this.setLocationRelativeTo(null);
        // Hiển thị giao diện
        this.setVisible(true);
    }
    /**
     * Panel tiêu đề trò chơi
     * Bao gồm:
     * - Tên chế độ chơi
     * - ComboBox chọn mode
     */
    class TitlePanel extends JPanel {
        TitlePanel() {
            super(new BorderLayout());
            this.setBorder(new EmptyBorder(12, 12, 8, 12));
            // Label hiển thị chế độ chơi

            GameModeTitle = new JLabel("Người vs Người", SwingConstants.CENTER);
            GameModeTitle.setFont(new Font("Arial", Font.BOLD, 24));
            add(GameModeTitle, BorderLayout.CENTER);
            // ComboBox chọn chế độ chơi

            modeComboBox = new JComboBox<>(GameMode.values());
            modeComboBox.setFont(new Font("Arial", Font.BOLD, 12));
            modeComboBox.setFocusable(false);
            modeComboBox.setBackground(Color.WHITE);
            add(modeComboBox, BorderLayout.EAST);
            // Khoảng trắng căn chỉnh giao diện

            JPanel leftSpace = new JPanel();
            leftSpace.setPreferredSize(modeComboBox.getPreferredSize());
            add(leftSpace, BorderLayout.WEST);
        }
    }
    /**
     * Panel chứa các nút thả quân theo cột
     */
    class DropPanel extends JPanel {
        JButton[] colButtons;
        DropPanel(int cols, JButton[] colButtons) {
            this.colButtons = colButtons;
            JPanel topPanel = new JPanel(new GridLayout(1, cols, 8, 8));
            topPanel.setOpaque(false);
            // Tạo nút thả quân cho từng cột
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
    /**
     * Panel bàn cờ chính
     * Chứa ma trận các ô CellPanel
     */
    class GameBoardPanel extends JPanel {
        CellPanel[][] cellPanels;
        GameBoardPanel(int rows, int cols, CellPanel[][] cellPanels) {
            this.cellPanels = cellPanels;
            // Thiết lập lưới bàn cờ

            setLayout(new GridLayout(rows, cols, 8, 8));
            setBackground(themeColor);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            // Tạo từng ô cờ

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    CellPanel cell = new CellPanel();
                    cellPanels[r][c] = cell;
                    add(cell);
                }
            }
        }
    }
    /**
     * Panel trạng thái trò chơi
     * Hiển thị:
     * - Lượt chơi hiện tại
     * - Nút reset
     */
    class StatusPanel extends JPanel {
        private JLabel statusLabel;
        private JButton resetButton;
        StatusPanel(JLabel statusLabel, JButton resetButton) {
            this.statusLabel = statusLabel;
            this.resetButton = resetButton;
            setLayout(new BorderLayout(8, 8));
            setBorder(new EmptyBorder(6, 8, 8, 8));
            // Cấu hình label trạng thái

            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(statusLabel, BorderLayout.CENTER);
            JPanel control = new JPanel();
            // Cấu hình nút reset

            resetButton.setFocusable(false);
            resetButton.setBackground(Color.WHITE);
            control.add(resetButton);
            add(control, BorderLayout.EAST);
        }
    }
    /**
     * Lấy chế độ chơi hiện tại từ ComboBox
     * @return GameMode được chọn
     */
    public GameMode getSelectedGameMode() {
        return (GameMode) this.modeComboBox.getSelectedItem();
    }
    /**
     * Cập nhật tiêu đề chế độ chơi
     * @param text nội dung tiêu đề mới
     */
    public void setGameModeTitleText(String text) {
        this.GameModeTitle.setText(text);
    }
    /**
     * Lấy nút thả quân theo cột
     * @param col vị trí cột
     * @return JButton của cột tương ứng
     */
    public JButton getColButton(int col) {
        return colButtons[col];
    }
    /**
     * Cập nhật quân cờ trên bàn cờ
     *
     * @param row hàng
     * @param col cột
     * @param player người chơi sở hữu quân cờ
     */
    public void updateCell(int row, int col, Player player) {
        this.cellPanels[row][col].setPlayer(player);
    }
    /**
     * Cập nhật trạng thái trò chơi
     * @param text nội dung trạng thái mới
     */
    public void updateStatus(String text) {
        this.statusLabel.setText(text);
    }
    /**
     * Hiển thị hộp thoại thông báo
     * @param message nội dung thông báo
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}