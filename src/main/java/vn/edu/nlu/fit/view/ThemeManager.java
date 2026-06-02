/**
 * @file    ThemeManager.java
 * @package vn.edu.nlu.fit.view
 * @author  [Nguyễn Trọng Tín]
 * @date    2026-06-01
 * @version 1.0
 * @desc    Quản lý chủ đề màu sắc Light/Dark cho toàn bộ giao diện.
 *          Toggle giữa 2 chế độ và cung cấp màu sắc cho từng component.
 * @history v1.0 2026-06-01 - Tạo mới
 */
package vn.edu.nlu.fit.view;

import java.awt.Color;

public class ThemeManager {

    public enum Theme {LIGHT, DARK}

    // Light theme
    public static final Color LIGHT_BG     = Color.WHITE;
    public static final Color LIGHT_BOARD  = new Color(30, 144, 255);
    public static final Color LIGHT_TEXT   = Color.BLACK;
    public static final Color LIGHT_BUTTON = Color.WHITE;

    // Dark theme
    public static final Color DARK_BG      = new Color(30, 30, 40);
    public static final Color DARK_BOARD   = new Color(20, 60, 110);
    public static final Color DARK_TEXT    = new Color(230, 230, 230);
    public static final Color DARK_BUTTON  = new Color(60, 60, 80);

    private Theme currentTheme = Theme.LIGHT;

    public void toggle() {
        currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
    }

    public Theme getCurrentTheme()      { return currentTheme; }
    public boolean isDark()             { return currentTheme == Theme.DARK; }
    public Color getBackgroundColor()   { return isDark() ? DARK_BG     : LIGHT_BG; }
    public Color getBoardColor()        { return isDark() ? DARK_BOARD  : LIGHT_BOARD; }
    public Color getTextColor()         { return isDark() ? DARK_TEXT   : LIGHT_TEXT; }
    public Color getButtonColor()       { return isDark() ? DARK_BUTTON : LIGHT_BUTTON; }
}
