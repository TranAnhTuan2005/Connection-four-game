/**
 * @file    ConnectFourController.java
 * @package vn.edu.nlu.fit.controller

 * @desc    Controller trong mô hình MVC — xử lý toàn bộ sự kiện người dùng,
 *          điều phối Model và View.
 *          resetRound()        — UC2c  bước 2.1.2 → 2.1.5
 *          startPvP()          — UC2a  bước 2.2.2
 *          startPvE()          — UC2b  bước 2.2.2
 *          handleComboBoxAction()— UC2  bước 2.2.1 → 2.2.3
 *          handleColumnClick() — UC4   bước 4.1.2 → 4.1.7
 *                                UC4.1 bước 4.1.1.1 → 4.1.1.4
 * @history v1.0 2026-05-01 – Tạo mới
 */
package vn.edu.nlu.fit.controller;

import vn.edu.nlu.fit.enums.GameMode;
import vn.edu.nlu.fit.heuristic.ConnectFourHeuristic;
import vn.edu.nlu.fit.model.*;
import vn.edu.nlu.fit.view.ConnectFourView;
import vn.edu.nlu.fit.controller.HintAdvisor;
import vn.edu.nlu.fit.model.Board;
import vn.edu.nlu.fit.model.HumanPlayer;
import vn.edu.nlu.fit.persistence.GameSerializer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.*;

public class ConnectFourController {

    private final ConnectFourGame model;
    private final ConnectFourView view;
    // [v2.0 - Người 5] Service gợi ý nước đi
    private final HintAdvisor hintAdvisor;

    // UC2 – Bước 2.1.0: Lưu chế độ chơi hiện tại (PVP hoặc PVE)
    private GameMode currentMode;

    /**
     * UC2  – Bước 2.1.0: Constructor khởi tạo Controller, kết nối Model và View.
     *         Mặc định chế độ PVP, gọi startPvP() và resetRound() ngay sau khi tạo.
     *
     * @param model  ConnectFourGame — Model quản lý logic game
     * @param view   ConnectFourView — View hiển thị giao diện
     */
    public ConnectFourController(ConnectFourGame model, ConnectFourView view) {
        this.model = model;
        this.view = view;
        // [v2.0 - Người 5] Khởi tạo HintAdvisor
        this.hintAdvisor = new HintAdvisor();
        // UC2a – Bước 2.1.0: Chế độ mặc định là PVP
        this.currentMode = GameMode.PVP;
        startPvP();
        updateStatusLabel();

        // UC2  – Bước 2.1.1: Đăng ký ActionListener cho nút Reset, ComboBox và các nút cột
        this.addButtonAction();
    }

    /**
     * UC2  – Bước 2.1.1: Đăng ký ActionListener cho tất cả các nút tương tác.
     *         - 7 nút cột (DropPanel) → handleColumnClick(col)  [UC4 bước 4.1.1]
     *         - ComboBox chế độ chơi  → handleComboBoxAction()  [UC2 bước 2.2.1]
     *         - Nút Reset             → resetRound()            [UC2c bước 2.1.2]
     */
    public void addButtonAction() {
        // UC4 – Bước 4.1.1: Mỗi nút cột khi nhấn sẽ gọi handleColumnClick(col)
        for (int col = 0; col < model.getCols(); col++) {
            int column = col;
            view.getColButton(col).addActionListener(e -> handleColumnClick(column));
        }

        // UC2 – Bước 2.2.1: ComboBox đổi chế độ sẽ gọi handleComboBoxAction()
        view.getModeComboBox().addActionListener(e -> handleComboBoxAction(view.getSelectedGameMode()));

        // UC2c – Bước 2.1.2: Nút Reset sẽ gọi resetRound()
        view.getResetButton().addActionListener(e -> resetRound());

        // [v2.0 - Người 5] Đăng ký listener cho 3 nút mới
        view.getHintButton().addActionListener(e -> handleHint());
        view.getSaveButton().addActionListener(e -> handleSave());
        view.getLoadButton().addActionListener(e -> handleLoad());
    }

    /**
     * UC2  – Bước 2.2.1 → 2.2.3: Xử lý khi người chơi thay đổi chế độ trên ComboBox.
     *         Gọi startPvP() hoặc startPvE() tương ứng, sau đó tự động resetRound().
     *
     * @param mode  GameMode.PVP hoặc GameMode.PVE
     */
    public void handleComboBoxAction(GameMode mode) {
        switch (mode) {
            case PVP:
                // UC2a – Bước 2.2.2: Khởi tạo 2 HumanPlayer
                currentMode = GameMode.PVP;
                startPvP();
                view.setGameModeTitleText(GameMode.PVP.toString());
                break;

            case PVE:
                // UC2b – Bước 2.2.2: Khởi tạo 1 HumanPlayer + 1 AIPlayer
                currentMode = GameMode.PVE;
                startPvE();
                view.setGameModeTitleText(GameMode.PVE.toString());
                break;
        }
        // UC2 – Bước 2.2.3: Sau khi đổi chế độ, tự động reset bàn cờ
        resetRound();
    }

    /**
     * UC4   – Bước 4.1.2: Entry point khi người chơi nhấn nút cột.
     * UC4   – Bước 4.1.3: Kiểm tra isGameOver() trước khi xử lý.
     * UC4   – Bước 4.1.4: Gọi model.makeMove(col) → Board.isValidColumn(col).
     * UC4   – Bước 4.1.5: Board.findLowestEmptyRow() + Board.setCell().
     * UC4   – Bước 4.1.6: Cập nhật view.updateCell() sau khi đặt quân.
     * UC4   – Bước 4.1.7: Gọi checkGameState() kiểm tra thắng/hòa.
     * UC4.1 – Bước 4.1.1.1 → 4.1.1.2: Nếu move==null → showMessage cảnh báo cột đầy.
     * UC4   – Bước 4.2.1: Nếu PvE và đến lượt AI → AIPlayer.chooseColumn().
     *
     * @param col  Chỉ số cột được nhấn (0–6)
     */
    public void handleColumnClick(int col) {

        // UC4 – Bước 4.1.3: Nếu game đã kết thúc → return ngay, không xử lý
        if (model.isGameOver()) {
            return;
        }

        // UC4 – Bước 4.1.4: makeMove gọi isValidColumn(col) → findLowestEmptyRow() → setCell()
        Move move = model.makeMove(col);

        if (move == null) {
            // UC4.1 – Bước 4.1.1.1 → 4.1.1.2: move==null → cột đầy → hiển thị JOptionPane
            view.showMessage("Cột này đã đầy!");
            return;
            // UC4.1 – Bước 4.1.1.3 → 4.1.1.4: Người chơi đóng hộp thoại,
            //         trạng thái bàn cờ và lượt chơi không thay đổi
        }

        // UC4 – Bước 4.1.6: Cột hợp lệ → view.updateCell() vẽ lại ô với màu quân
        view.updateCell(move.getRow(), move.getCol(), move.getPlayer());

        // UC4 – Bước 4.1.7: Kiểm tra kết quả ván (thắng/hòa)
        checkGameState();

        // UC4 – Bước 4.2.1: Chế độ PvE → kích hoạt AIPlayer đi nước tiếp theo
        if (!model.isGameOver() && currentMode == GameMode.PVE && model.getCurrentPlayer().isAI()) {

            // UC4 – 4.2.1: AI tính nước đi tối ưu bằng Minimax Alpha-Beta (UC8)
            int aiCol = ((AIPlayer) model.getCurrentPlayer()).chooseColumn(model.getBoard());

            // UC4 – 4.2.1: AI gọi makeMove, nếu trả về null → tất cả cột đầy → hòa
            Move aiMove = model.makeMove(aiCol);

            if (aiMove == null) return;  // Bàn cờ đầy → kết thúc hòa

            // UC4 – 4.2.1: Cập nhật ô AI vừa đi lên view
            view.updateCell(aiMove.getRow(), aiMove.getCol(), aiMove.getPlayer());

            // UC4 – 4.2.1: Kiểm tra kết quả sau nước đi của AI
            checkGameState();
        }
    }

    /**
     * UC4 – Bước 4.1.7: Kiểm tra trạng thái game sau mỗi nước đi.
     *        Nếu có người thắng → handleWin(). Nếu hòa → handleDraw().
     *        Ngược lại → cập nhật lượt chơi tiếp theo lên statusLabel.
     */
    public void checkGameState() {
        GameState state = model.getGameState();
        if (state.isGameOver()) {
            if (state.getWinner() != null) {
                handleWin(state.getWinner());       // Có người thắng
            } else if (state.isDraw()) {
                handleDraw();                       // Hòa — bàn cờ đầy
            }
        } else {
            // UC4 – Chưa kết thúc → cập nhật lượt chơi tiếp theo
            updateStatusLabel();
        }
    }

    /**
     * UC4 – Bước 4.1.7: Hiển thị kết quả thắng và thông báo tên người thắng.
     * @param winner  Player giành chiến thắng
     */
    public void handleWin(Player winner) {
        String message = winner.getName() + " thắng!";
        view.updateStatus("Game kết thúc: " + message);
        view.showMessage(message);
    }

    /**
     * UC4 – Bước 4.1.7: Hiển thị kết quả hòa khi bàn cờ đầy mà không có người thắng.
     */
    public void handleDraw() {
        String message = "Hòa! Bàn cờ đầy.";
        view.updateStatus(message);
        view.showMessage(message);
    }

    /**
     * UC2c – Bước 2.1.2 → 2.1.5: Reset toàn bộ ván chơi về trạng thái ban đầu.
     *         Được gọi khi người dùng nhấn nút Reset (addButtonAction)
     *         hoặc sau khi đổi chế độ (handleComboBoxAction → resetRound).
     */
    public void resetRound() {
        // UC2c – Bước 2.1.3: ConnectFourGame.reset() xóa Board, đặt lại GameState
        model.reset();

        // UC2c – Bước 2.1.4: Duyệt 42 ô (6×7), gọi view.updateCell(r,c,null) từng ô
        updateAllCells();

        // UC2c – Bước 2.1.5: view.updateStatus("Lượt: " + player.getName())
        updateStatusLabel();
    }

    /**
     * UC2a – Bước 2.2.2: Khởi tạo 2 HumanPlayer cho chế độ PvP.
     *         player1 = HumanPlayer(id=1, "Người chơi Đỏ",  Color(220,40,40))
     *         player2 = HumanPlayer(id=2, "Người chơi Vàng", Color(255,210,25))
     *         Gọi từ: handleComboBoxAction(PVP) → startPvP() → resetRound()
     */
    public void startPvP() {
        // UC2a – Bước 2.2.2: Tạo player1 = HumanPlayer (màu đỏ)
        Player player1 = new HumanPlayer(1, "Người chơi Đỏ", new Color(220, 40, 40));

        // UC2a – Bước 2.2.2: Tạo player2 = HumanPlayer (màu vàng)
        Player player2 = new HumanPlayer(2, "Người chơi Vàng", new Color(255, 210, 25));

        // UC2a – Bước 2.2.2: Đăng ký 2 player vào model
        model.setPlayers(player1, player2);
    }

    /**
     * UC2b – Bước 2.2.2: Khởi tạo 1 HumanPlayer + 1 AIPlayer cho chế độ PvE.
     *         human = HumanPlayer(id=1, "Bạn", màu đỏ)
     *         ai    = AIPlayer(id=2, "Máy", màu vàng, depth=7, ConnectFourHeuristic)
     *         Gọi từ: handleComboBoxAction(PVE) → startPvE() → resetRound()
     */
    public void startPvE() {
        // UC2b – Bước 2.2.2: Tạo người chơi thật (Human)
        Player human = new HumanPlayer(1, "Bạn", new Color(220, 40, 40));

        // UC2b – Bước 2.2.2: Tạo AI (Minimax depth=7, heuristic ưu tiên cột giữa)
        //         Tham số: AIPlayer(id, name, color, humanId, winChecker, heuristic)
        Player ai = new AIPlayer(2, "Máy", new Color(255, 210, 25),
                                                        human.getId(),
                                                        model.getWinChecker(),
                                                        new ConnectFourHeuristic(human.getId(), 2));

        // UC2b – Bước 2.2.2: Đăng ký human + AI vào model
        model.setPlayers(human, ai);
    }

    /**
     * UC2c – Bước 2.1.5: Lấy người chơi hiện tại và cập nhật nhãn lượt chơi.
     *         Gọi view.updateStatus("Lượt: " + currentPlayer.getName()).
     */
    public void updateStatusLabel() {
        Player currentPlayer = model.getCurrentPlayer();
        // UC2c – Bước 2.1.5: Hiển thị "Lượt: Người chơi Đỏ" hoặc "Lượt: Bạn"...
        view.updateStatus("Lượt: " + currentPlayer.getName());
    }

    /**
     * UC2c – Bước 2.1.4: Duyệt toàn bộ 42 ô (6 hàng × 7 cột),
     *         gọi view.updateCell(r, c, null) để xóa trắng từng ô trên giao diện.
     *         Sau cùng gọi view.repaint() để vẽ lại toàn bộ bàn cờ.
     */
    public void updateAllCells() {
        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                // UC2c – Bước 2.1.4: Đặt ô (r,c) về trạng thái trống (null)
                view.updateCell(r, c, null);
            }
        }
        // UC2c – Bước 2.1.4: Repaint để hiển thị toàn bộ thay đổi cùng lúc
        view.repaint();
    }



    // ========================================================================
    // [v2.0 - Người 5] HINT + SAVE + LOAD
    // ========================================================================

    /**
     * Xử lý khi người dùng nhấn nút Hint.
     * Tìm cột tốt nhất theo phân tích AI và highlight cột đó.
     */
    private void handleHint() {
        if (model.isGameOver()) {
            view.showMessage("Game đã kết thúc!");
            return;
        }

        int suggestedCol = hintAdvisor.suggestColumn(model);
        if (suggestedCol < 0) {
            view.showMessage("Không tìm được nước đi gợi ý!");
            return;
        }

        view.highlightColumn(suggestedCol);
    }

    /**
     * Xử lý khi người dùng nhấn nút Save.
     * Mở JFileChooser cho người dùng chọn vị trí lưu file .cf4.
     */
    private void handleSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Connect Four Save (*.cf4)", "cf4"));
        chooser.setSelectedFile(new File("connectfour_save.cf4"));

        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            // Đảm bảo file có extension .cf4
            if (!path.endsWith(".cf4")) {
                path += ".cf4";
            }

            try {
                GameSerializer.save(model, path);
                view.showMessage("Đã lưu ván chơi vào:\n" + path);
            } catch (IOException ex) {
                view.showMessage("Lỗi khi lưu: " + ex.getMessage());
            }
        }
    }

    /**
     * Xử lý khi người dùng nhấn nút Load.
     * Mở JFileChooser cho người dùng chọn file .cf4 để khôi phục ván chơi.
     */
    private void handleLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Connect Four Save (*.cf4)", "cf4"));

        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();

            try {
                // Reset model trước khi load
                model.reset();
                GameSerializer.load(model, path);

                // Vẽ lại toàn bộ bàn cờ từ trạng thái model
                refreshBoardFromModel();

                updateStatusLabel();
                view.showMessage("Đã tải ván chơi từ:\n" + path);
            } catch (IOException ex) {
                view.showMessage("Lỗi khi tải: " + ex.getMessage());
            }
        }
    }

    /**
     * Vẽ lại toàn bộ bàn cờ từ trạng thái model (dùng sau khi load).
     * Tạo các HumanPlayer tạm thời chỉ để có màu hiển thị đúng.
     */
    private void refreshBoardFromModel() {
        Board board = model.getBoard();

        for (int r = 0; r < model.getRows(); r++) {
            for (int c = 0; c < model.getCols(); c++) {
                int v = board.getCell(r, c);
                Player owner = null;
                if (v == 1) {
                    owner = new HumanPlayer(1, "P1", new Color(220, 40, 40));   // đỏ
                } else if (v == 2) {
                    owner = new HumanPlayer(2, "P2", new Color(255, 210, 25));  // vàng
                }
                view.updateCell(r, c, owner);
            }
        }
    }
}
