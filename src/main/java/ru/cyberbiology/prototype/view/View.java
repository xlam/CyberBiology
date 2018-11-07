package ru.cyberbiology.prototype.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import ru.cyberbiology.Bot;
import ru.cyberbiology.World;

/**
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public abstract class View implements IView {

    public Image buf;
    public Graphics g;
    public World world;

    public void init(World world, JPanel canvas) {
        this.world = world;
        //Создаем временный буфер для рисования
        buf = canvas.createImage(canvas.getWidth(), canvas.getHeight());
        //подеменяем графику на временный буфер
        g = buf.getGraphics();
    }

    @Override
    public Image paint(World world, JPanel canvas) {

        init(world, canvas);

        g.drawRect(0, 0, world.width * World.SIZE + 1, world.height * World.SIZE + 1);

        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                Bot bot = world.getBot(x, y);
                Color color = getBotColor(bot);
                g.setColor(color);
                g.fillRect(x * World.SIZE, y * World.SIZE, World.SIZE, World.SIZE);
                if (bot != null && bot.alive == bot.LV_ALIVE) {
                    g.setColor(Color.BLACK);
                    g.drawRect(x * World.SIZE, y * World.SIZE, World.SIZE, World.SIZE);
                }
            }
        }
        return buf;
    }

    public abstract Color getBotColor(Bot bot);
}
