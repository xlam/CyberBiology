package ru.cyberbiology;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Тесты для BasicWorld.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class BasicWorldTest {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 1000;
    private BasicWorld world;

    @Before
    public void setUp() {
        world = new BasicWorld(new PainterMock(), WIDTH, HEIGHT);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void worldMineralsAccumulationAtHeight() {
        world.setMineralsAccumulation("height");
        int sumMin = 0;
        int sumMax = 0;
        int mineralsMin;
        int mineralsMax;
        int steps = 10;
        for (int i = 0; i < steps; i++) {
            mineralsMin = world.getMineralsAt(1);
            mineralsMax = world.getMineralsAt(HEIGHT - 1);
            sumMin += mineralsMin;
            sumMax += mineralsMax;
        }
        assertTrue(sumMin < steps);
        assertTrue(sumMax > steps);
    }
}
