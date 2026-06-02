/**
 * @file    TurnTimer.java
 * @package vn.edu.nlu.fit.view
 * @author  [Người 4]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Đếm ngược 30 giây cho mỗi lượt đi.
 *          Hết giờ → gọi callback onTimeout.
 *          Hiển thị thời gian còn lại lên JLabel, đổi màu đỏ khi <= 5s.
 * @history v1.0 2026-06-01 - Tạo mới
 */
package vn.edu.nlu.fit.view;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;

public class TurnTimer {

    private static final int DEFAULT_SECONDS = 30;

    private final JLabel timerLabel;
    private final Runnable onTimeout;
    private Timer timer;
    private int secondsLeft;
    private int totalSeconds;

    public TurnTimer(JLabel timerLabel, Runnable onTimeout) {
        this.timerLabel = timerLabel;
        this.onTimeout = onTimeout;
        this.totalSeconds = DEFAULT_SECONDS;
    }

    public void setTotalSeconds(int seconds) {
        this.totalSeconds = seconds;
    }

    /** Khởi động/restart bộ đếm cho lượt mới */
    public void start() {
        stop();
        secondsLeft = totalSeconds;
        updateLabel();

        timer = new Timer(1000, e -> {
            secondsLeft--;
            updateLabel();
            if (secondsLeft <= 0) {
                stop();
                if (onTimeout != null) onTimeout.run();
            }
        });
        timer.start();
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private void updateLabel() {
        timerLabel.setText(String.format("⏱ %ds", secondsLeft));
        timerLabel.setForeground(secondsLeft <= 5 ? Color.RED : Color.BLACK);
    }
}
