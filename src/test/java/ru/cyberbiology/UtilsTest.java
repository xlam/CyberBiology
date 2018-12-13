package ru.cyberbiology;

import org.junit.Test;
import static org.junit.Assert.*;
import ru.cyberbiology.util.MiscUtils;

/**
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class UtilsTest {

    @Test
    public void findsRepeatingGenesSequence() {

        String[] history1 = {
            "23", "24", "1", "2", "36", "60",
            "23", "24", "1", "2", "36", "60",
            "23", "24", "1", "2", "36", "60",
            "23", "24", "1", "0", "36", "60",
        };

        String[] history2 = {
            "23", "24", "1", "23", "24", "1",
            "23", "24", "1", "23", "24", "1",
            "23", "24", "1", "23", "24", "1",
            "23", "24", "1", "23", "24", "1",
        };

        String[] history3 = {
            "23", "24", "1", "2", "36", "60",
            "41", "24", "24", "2", "36", "60",
        };

        String[] program1 = {
            "23", "24", "1", "2", "36", "60",
        };

        String[] program2 = {
            "23", "24", "1",
        };

        assertArrayEquals(program1, MiscUtils.getProgram(history1));
        assertArrayEquals(program2, MiscUtils.getProgram(history2));
        assertNull(MiscUtils.getProgram(history3));
    }
}
