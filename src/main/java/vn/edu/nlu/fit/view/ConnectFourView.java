package vn.edu.nlu.fit.view;

import lombok.Getter;
import vn.edu.nlu.fit.enums.GameMode;
import vn.edu.nlu.fit.model.Player;
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

    public ConnectFourView(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cellPanels = new CellPanel[rows][cols];
        this.colButtons = new JButton[cols];
        this.statusLabel = new JLabel("Lượt: Người chơi 1 (Đỏ)");
        this.resetButton = new JButton("Reset");
        this.setLayout(new BorderLayout(8, 8));

        // [v2.0 - Người 5] Khởi tạo các nút mới
        this.hintButton = new JButton("💡 Hint");
        this.saveButton = new JButton("💾 Save");
        this.loadButton = new JButton("📂 Load");

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new TitlePanel(), BorderLayout.NORTH);
        wrapper.add(new DropPanel(cols, colButtons), BorderLayout.SOUTH);

        this.add(wrapper, BorderLayout.NORTH);
        this.add(new GameBoardPanel(rows, cols, cellPanels), BorderLayout.CENTER);
        this.add(new StatusPanel(statusLabel, resetButton), BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 800);
        this.setTitle("Connect Four");
        Image icon = new ImageIcon(getClass().getResource("/logo.png")).getImage();
        this.setIconImage(icon);
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    class TitlePanel extends JPanel {

        TitlePanel() {
            super(new BorderLayout());
            this.setBorder(new EmptyBorder(12, 12, 8, 12));

            GameModeTitle = new JLabel("Người vs Người", SwingConstants.CENTER);
            GameModeTitle.setFont(new Font("Arial",Font.BOLD, 24));
            add(GameModeTitle, BorderLayout.CENTER);

            modeComboBox = new JComboBox<>(GameMode.values());
            modeComboBox.setFont(new Font("Arial",Font.BOLD, 12));
            modeComboBox.setFocusable(false);
            modeComboBox.setBackground(Color.WHITE);
            add(modeComboBox, BorderLayout.EAST);

            JPanel leftSpace = new JPanel();
            leftSpace.setPreferredSize(modeComboBox.getPreferredSize());
            add(leftSpace, BorderLayout.WEST);
        }
    }

    class DropPanel extends JPanel {
        JButton[] colButtons;

        DropPanel(int cols, JButton[] colButtons) {
            this.colButtons = colButtons;

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
        CellPanel[][] cellPanels;

        GameBoardPanel(int rows, int cols, CellPanel[][] cellPanels) {
            this.cellPanels = cellPanels;

            setLayout(new GridLayout(rows, cols, 8,8));
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
        private JLabel statusLabel;
        private JButton resetButton;

        StatusPanel(JLabel statusLabel, JButton resetButton) {
            this.statusLabel = statusLabel;
            this.resetButton = resetButton;

            setLayout(new BorderLayout(8, 8));
            setBorder(new EmptyBorder(6, 8, 8, 8));

            statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(statusLabel, BorderLayout.CENTER);

            JPanel control = new JPanel();

            resetButton.setFocusable(false);
            resetButton.setBackground(Color.WHITE);

            control.add(resetButton);
            add(control, BorderLayout.EAST);
        }
    }

    public GameMode getSelectedGameMode(){
        return (GameMode) this.modeComboBox.getSelectedItem();
    }

    public void setGameModeTitleText(String text){
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

}