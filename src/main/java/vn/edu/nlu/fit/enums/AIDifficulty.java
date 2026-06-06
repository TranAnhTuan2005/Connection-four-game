/**
 * @file    AIDifficulty.java
 * @package vn.edu.nlu.fit.enums
 * @author  [Người 4]
 * @date    2026-06-01
 * @version 2.0
 * @desc    Enum cấp độ AI - quy định depth tìm kiếm Minimax và tỉ lệ sai lầm.
 *          Easy   = depth 1, 40% đi ngẫu nhiên (rất yếu, dễ thắng)
 *          Medium = depth 5, 10% đi ngẫu nhiên (vừa phải, đôi khi sơ hở)
 *          Hard   = depth 10, 0% sai lầm (rất mạnh, gần như bất bại)
 * @history v1.0 2026-06-01 - Tạo mới
 *          v2.0 2026-06-06 - Thêm mistakeRate để phân biệt độ khó rõ ràng hơn
 */
package vn.edu.nlu.fit.enums;

public enum AIDifficulty {
    EASY("Dễ", 1, 0.40),
    MEDIUM("Trung bình", 5, 0.10),
    HARD("Khó", 10, 0.0);

    private final String label;
    private final int depth;
    private final double mistakeRate;

    AIDifficulty(String label, int depth, double mistakeRate) {
        this.label = label;
        this.depth = depth;
        this.mistakeRate = mistakeRate;
    }

    public int getDepth() {
        return depth;
    }

    public double getMistakeRate() {
        return mistakeRate;
    }

    @Override
    public String toString() {
        return label;
    }
}
