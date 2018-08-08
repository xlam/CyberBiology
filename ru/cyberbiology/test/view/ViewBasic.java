package ru.cyberbiology.test.view;

import java.awt.Color;

import ru.cyberbiology.test.Bot;
import ru.cyberbiology.test.prototype.view.View;

public class ViewBasic extends View
{
	public ViewBasic() {}

    @Override
	public String getName()
	{
		return "Базовое";
	}

    @Override
    public Color getBotColor(Bot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            // Color.WHITE
        } else if ((bot.alive == bot.LV_ORGANIC_SINK) || (bot.alive == bot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == bot.LV_ALIVE) {
            int green = (int) (bot.c_green - ((bot.c_green * bot.health) / 2000));
            if (green < 0) green = 0;
            if (green > 255) green = 255;
            int blue = (int) (bot.c_blue * 0.8 - ((bot.c_blue * bot.mineral) / 2000));
            color = new Color(bot.c_red, green, blue);
        }
        return color;
    }
}
