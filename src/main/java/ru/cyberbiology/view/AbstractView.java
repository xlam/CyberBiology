package ru.cyberbiology.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import ru.cyberbiology.BasicBot;
import ru.cyberbiology.BasicWorld;
import ru.cyberbiology.util.ProjectProperties;

/**
 * Реализует общий функционал видов.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public abstract class AbstractView implements View {

    public Image buf;
    public Graphics graphics;
    public BasicWorld world;
    private int botSize;

    private void init(BasicWorld world, JPanel canvas) {
        this.world = world;
        //Создаем временный буфер для рисования
        buf = canvas.createImage(canvas.getWidth(), canvas.getHeight());
        //подеменяем графику на временный буфер
        graphics = buf.getGraphics();
        botSize = ProjectProperties.getInstance().botSize();
    }

    @Override
    public Image paint(BasicWorld world, JPanel canvas) {

        init(world, canvas);

        graphics.drawRect(0, 0, world.width * botSize + 1, world.height * botSize + 1);

        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                BasicBot bot = world.getBot(x, y);
                Color color = getBotColor(bot);
                graphics.setColor(color);
                graphics.fillRect(x * botSize, y * botSize, botSize, botSize);
                if (bot != null && bot.alive == BasicBot.LV_ALIVE) {
                    graphics.setColor(Color.BLACK);
                    graphics.drawRect(x * botSize, y * botSize, botSize, botSize);
                }
            }
        }
        return buf;
    }

    public abstract Color getBotColor(BasicBot bot);

    /**
     * Проверка и корректировка значения цвета.
     * @param color проверяемое значение.
     * @return значение цвета в диапазоне от 0 до 255.
     */
    public int validColor(int color) {
        return color > 255 ? 255 : color < 0 ? 0 : color;
    }
}
