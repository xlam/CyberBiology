package ru.cyberbiology.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Arrays;
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

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, world.width * botSize, world.height * botSize);
        graphics.setColor(Color.BLACK);
        graphics.drawRect(0, 0, world.width * botSize + 1, world.height * botSize + 1);

        Arrays.stream(world.matrix)
                .filter(bot -> bot != null)
                .forEach(bot -> {
                    graphics.setColor(getBotColor(bot));
                    graphics.fillRect(bot.posX * botSize, bot.posY * botSize, botSize, botSize);
                    if (bot.alive == BasicBot.LV_ALIVE) {
                        graphics.setColor(Color.BLACK);
                        graphics.drawRect(bot.posX * botSize, bot.posY * botSize, botSize, botSize);
                    }
                });

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
