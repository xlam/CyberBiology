package ru.cyberbiology.test.view;

import java.awt.Color;

import ru.cyberbiology.test.Bot;
import ru.cyberbiology.test.prototype.view.View;

public class ViewPest extends View
{

	public ViewPest() {}

	@Override
	public String getName()
	{
		return "Паразиты";
	}

    @Override
    public Color getBotColor(Bot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            color = Color.WHITE;
        } else if ((bot.alive == bot.LV_ORGANIC_HOLD) || (bot.alive == bot.LV_ORGANIC_SINK)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == bot.LV_ALIVE) {
            // цвет бота без генов паразитизма
            int colorRed = 170;
            int colorGreen = 170;
            int colorBlue = 170;

            // бот-паразит, добавляем красноты
            if (bot.pest > 0) {
                world.pestGenes += bot.pest;
                world.pests++;
                colorRed = 127 + bot.pest * 2;
                colorGreen = 0;
                colorBlue = 0;
            }

            color = new Color(colorRed, colorGreen, colorBlue);
        }
        return color;
    }
}
