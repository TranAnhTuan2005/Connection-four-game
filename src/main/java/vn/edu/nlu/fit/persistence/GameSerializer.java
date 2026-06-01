package vn.edu.nlu.fit.persistence;

/*
 * @desc    Lưu/khôi phục trạng thái ván chơi ra file .cf4.
 *          Format text đơn giản, không cần thư viện ngoài.
 *
 *          Định dạng file:
 *          ROWS=6
 *          COLS=7
 *          CURRENT_PLAYER=1
 *          CELLS=0,0,1,0,...,0  (42 số theo row-major)
 *
 */

import vn.edu.nlu.fit.model.Board;
import vn.edu.nlu.fit.model.ConnectFourGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameSerializer {

    /**
     * Lưu trạng thái bàn cờ ra file.
     *
     * @param game      ConnectFourGame hiện tại
     * @param filePath  Đường dẫn file output (vd: "saved/game1.cf4")
     * @throws IOException nếu không ghi được file
     */
    public static void save(ConnectFourGame game, String filePath) throws IOException {
        Board board = game.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        StringBuilder sb = new StringBuilder();
        sb.append("ROWS=").append(rows).append('\n');
        sb.append("COLS=").append(cols).append('\n');
        sb.append("CURRENT_PLAYER=").append(game.getCurrentPlayer().getId()).append('\n');
        sb.append("CELLS=");

        // Ghi tất cả 42 ô theo thứ tự row-major
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(board.getCell(r, c));
                // Thêm dấu phẩy giữa các giá trị (trừ giá trị cuối)
                if (!(r == rows - 1 && c == cols - 1)) {
                    sb.append(',');
                }
            }
        }

        // Ghi ra file (try-with-resources tự động đóng writer)
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(sb.toString());
        }
    }

    /**
     * Đọc file save và áp dụng vào game.
     * Lưu ý: cần gọi game.reset() trước khi gọi method này.
     *
     * @param game      Game đã được reset
     * @param filePath  Đường dẫn file save
     * @throws IOException nếu không đọc được hoặc file sai format
     */
    public static void load(ConnectFourGame game, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int rows = 0, cols = 0;
            int[] cellValues = null;

            // Đọc từng dòng và parse
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ROWS=")) {
                    rows = Integer.parseInt(line.substring(5).trim());
                } else if (line.startsWith("COLS=")) {
                    cols = Integer.parseInt(line.substring(5).trim());
                } else if (line.startsWith("CURRENT_PLAYER=")) {
                    // Có thể dùng để khôi phục lượt chơi
                    // (cần thêm setter trong PlayerManager nếu muốn dùng)
                } else if (line.startsWith("CELLS=")) {
                    String[] parts = line.substring(6).split(",");
                    cellValues = new int[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        cellValues[i] = Integer.parseInt(parts[i].trim());
                    }
                }
            }

            // Validate
            if (cellValues == null || cellValues.length != rows * cols) {
                throw new IOException("File save bị hỏng hoặc không đúng định dạng");
            }

            // Áp dụng các giá trị lên board
            Board board = game.getBoard();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int v = cellValues[r * cols + c];
                    if (v != 0) {
                        board.setCell(r, c, v);
                    }
                }
            }
        }
    }
}