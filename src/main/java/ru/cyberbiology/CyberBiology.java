package ru.cyberbiology;

import javax.swing.SwingUtilities;
import ru.cyberbiology.ui.MainWindow;

/**
 * Точка входа приложения.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class CyberBiology {

    /**
     * Точка входа.
     * @param args параметры командной строки
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
