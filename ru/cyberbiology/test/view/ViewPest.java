package ru.cyberbiology.test.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import ru.cyberbiology.test.Bot;

import ru.cyberbiology.test.World;
import ru.cyberbiology.test.prototype.view.IView;

public class ViewPest implements IView
{

	public ViewPest()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName()
	{
		return "Паразиты";
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

        world.population = 0;
        world.organic = 0;
        world.pests = 0;
        world.pestGenes = 0;
        for (int y = 0; y < world.height; y++) {
            for (int x = 0; x < world.width; x++) {
                if (world.matrix[x][y] == null) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x * World.BOTW,y * World.BOTH, World.BOTW, World.BOTH);
                } else if ((world.matrix[x][y].alive == 1) || (world.matrix[x][y].alive == 2)) {
                    g.setColor(new Color(200, 200, 200));
                    g.fillRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);
                    world.organic = world.organic + 1;
                } else if (world.matrix[x][y].alive == 3) {
                    g.setColor(Color.BLACK);
                    g.drawRect(x * World.BOTW, y * World.BOTH, World.BOTW, World.BOTH);

                    int pestGenes = 0;
                    for (int i=0; i<Bot.MIND_SIZE; i++)
                        if (world.matrix[x][y].mind[i] == 49)
                            pestGenes++;

                    int colorRed = 200;
                    int colorGreen = 200;
                    int colorBlue = 200;

                    if (pestGenes > 0) {
                        world.pests++;
                        colorRed = 127 + pestGenes * 2;
                        colorGreen = 0;
                        colorBlue = 0;
                    }

                    world.pestGenes += pestGenes;

                    g.setColor(new Color(colorRed, colorGreen, colorBlue));
                    g.fillRect(x * World.BOTW + 1, y * World.BOTH + 1,World.BOTW-1, World.BOTH-1);
                    world.population = world.population + 1;
                }
            }
        }
        return buf;
    }
}
