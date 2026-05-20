package vn.edu.nlu.fit.enums;

/**
 * Enum GameMode dùng để định nghĩa
 * các chế độ chơi của game Connect Four.
 *
 * Bao gồm:
 * - PVP: Người chơi với Người chơi
 * - PVE: Người chơi với Máy
 */
public enum GameMode {

    // Chế độ Người vs Người
    PVP("Người vs Người"),

    // Chế độ Người vs Máy
    PVE("Người vs Máy");

    // Tên hiển thị của chế độ chơi
    private final String label;

    /**
     * Constructor khởi tạo tên hiển thị
     * cho từng chế độ chơi
     * @param label tên hiển thị
     */
    GameMode(String label) {
        this.label = label;
    }

    /**
     * Ghi đè phương thức toString()
     * để hiển thị tên chế độ chơi trên giao diện.
     * Ví dụ:
     * - Người vs Người
     * - Người vs Máy
     * @return tên hiển thị của chế độ chơi
     */
    @Override
    public String toString() {
        return label;
    }
}