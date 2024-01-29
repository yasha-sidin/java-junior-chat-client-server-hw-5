package ru.gb.chat.client.frames.factories;

import java.awt.*;

/**
 * All fonts which are in used in this project
 */
public class FontFactory {
    /**
     * Main program font
     * @return main program font ("Times New Roman", BOLD, size: 14)
     */
    public static Font getMain() {
        return new Font("Times New Roman", Font.BOLD, 16);
    }

    public static Font getHeaderFont() {
        return new Font("Times New Roman", Font.BOLD, 21);
    }
}
