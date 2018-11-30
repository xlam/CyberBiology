package ru.cyberbiology;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Тесты для BasicBot.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class BasicBotTest {

    private BasicBot bot;
    private BasicWorld world;

    @Before
    public void setUp() {
        world = new BasicWorld(new PainterMock(), 100, 1000);
        bot = new BasicBot(world);
        bot.adr = 0;
        bot.posX = 50;
        bot.posY = 500;
        bot.health = 500;
        bot.mineral = 0;
        bot.alive = 3;
        bot.colorRed = 170;
        bot.colorBlue = 170;
        bot.colorGreen = 170;
        bot.direction = 3;
        bot.mprev = null;
        bot.mnext = null;
        for (int i = 0; i < 64; i++) {
            bot.mind[i] = 25;
        }
    }

    @After
    public void tearDown() {
        bot = null;
        world = null;
    }

    @Ignore("skip")
    @Test
    public void botTest() {
    }
}
