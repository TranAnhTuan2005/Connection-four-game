/**
 * @file    Main.java
 * @package vn.edu.nlu.fit
 * @author  Trần Anh Tuấn (MSSV: 23130372)
 * @date    2026-05-01
 * @version 1.0
 * @desc    Điểm khởi động ứng dụng — thực hiện UC2 bước 2.1.0.
 *          Khởi tạo Model, View, Controller theo mô hình MVC.
 * @history v1.0 2026-05-01 – Tạo mới
 */
package vn.edu.nlu.fit;
import vn.edu.nlu.fit.controller.ConnectFourController;
import vn.edu.nlu.fit.model.ConnectFourGame;

import vn.edu.nlu.fit.model.ConnectFourWinChecker;
import vn.edu.nlu.fit.view.ConnectFourView;

public class Main {

    /**
     * UC2 – Bước 2.1.0: Hệ thống khởi động, tạo Model – View – Controller.
     * Đây là trạng thái ban đầu trước khi ConnectFourView được hiển thị.
     */
    public static void main(String[] args) {

        // UC2 – Bước 2.1.0: Tạo Model với bảng 6×7, điều kiện thắng 4 ô liên tiếp
        ConnectFourGame model = new ConnectFourGame(6, 7, new ConnectFourWinChecker(4));

        // UC2 – Bước 2.1.0: Tạo View với kích thước tương ứng
        ConnectFourView view = new ConnectFourView(model.getRows(), model.getCols());

        // UC2 – Bước 2.1.0: Tạo Controller kết nối Model và View,
        //                    gọi startPvP() + resetRound() ngay khi khởi động
        ConnectFourController controller = new ConnectFourController(model, view);
    }
}
