package ru.cyberbiology.test.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import ru.cyberbiology.test.Bot;

import ru.cyberbiology.test.World;
import ru.cyberbiology.test.prototype.view.IView;

public class ViewEnergy implements IView
{

	public ViewEnergy()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName()
	{
		return "Энергия";
	}

    @Override
    public Image paint(World world,JPanel canvas) {
    	int w = canvas.getWidth();
    	int h = canvas.getHeight();
    	//Создаем временный буфер для рисования
    	Image buf = canvas.createImage(w, h);
    	//подеменяем графику на временный буфер
    	Graphics g = buf.getGraphics();

        g.drawRect(0, 0, world.width * World.BOTW + 1, world.height * World.BOTH + 1);

        world.pests = 0;
        world.pestGenes = 0;
        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                Bot bot = world.matrix[x][y];
                if (bot == null) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x * World.BOTW,y * World.BOTH, World.BOTW, World.BOTH);
                } else if ((bot.alive == 1) || (bot.alive == 2)) {
                    g.setColor(new Color(200, 200, 200));
                    g.fillRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);
                } else if (bot.alive == 3) {
                    g.setColor(Color.BLACK);
                    g.drawRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);

                    // черный цвет для умерших в данном пересчете ботов
                    int colorRed = 0;
                    int colorGreen = 0;
                    int colorBlue = 0;

                    // цвет выживших ботов в зависимости от энергии
                    if (bot.health > 0) {
                        colorRed = 255;
                        colorGreen = (int) Math.round(Math.abs(bot.health * 0.255 - 255));
                    }

                    // исключения по цвету помогают выявить ботов с аномальным
                    // количеством энергии (здоровья)
                    try {
                        g.setColor(new Color(colorRed, colorGreen, colorBlue));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("ViewEnergy.paint(): " +
                            "green=" + colorGreen + " " +
                            "health=" + bot.health
                        );
                    }

                    if (bot.pest > 0) {
                        world.pestGenes += world.matrix[x][y].pest;
                        world.pests++;
                    }

                    g.fillRect(x * World.BOTW + 1, y * World.BOTH + 1,World.BOTW-1, World.BOTH-1);
                }
            }
        }
        return buf;
    }
}
