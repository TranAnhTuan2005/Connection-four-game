/**
 * @file    AIDifficulty.java
 * @package vn.edu.nlu.fit.enums
 * @author  [Người 4]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Enum cấp độ AI - quy định depth tìm kiếm Minimax.
 *          Easy   = depth 2 (yếu, phản ứng nhanh)
 *          Medium = depth 4 (vừa phải)
 *          Hard   = depth 7 (mạnh, mặc định)
 * @history v1.0 2026-06-01 - Tạo mới
 */
package vn.edu.nlu.fit.enums;

public enum AIDifficulty {
    EASY("Dễ", 2),
    MEDIUM("Trung bình", 4),
    HARD("Khó", 7);

    private final String label;
    private final int depth;

    AIDifficulty(String label, int depth) {
        this.label = label;
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return label;
    }
}
