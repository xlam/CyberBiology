package ru.cyberbiology.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс вспомогательных методов.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class MiscUtils {

    /**
     * Ищет последовательность команд, выполненную два или более раза подряд.
     * <p>
     * Найденная последовательность представляет собой текущую программу бота.
     *
     * @param   history история выполненных генов
     * @return  найденная программа, или null
     */
    public static String[] getProgram(String[] history) {

        if (history == null) {
            return null;
        }
        String delimeter = "-";
        Pattern pattern = Pattern.compile("^([0-9" + delimeter + "]+)(\\1+)(.*)?$");
        Matcher matcher = pattern.matcher(String.join(delimeter, history));
        if (matcher.matches()) {
            return matcher.group(1).split(delimeter);
        }
        return null;
    }
}
