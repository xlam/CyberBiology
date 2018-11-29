package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.BasicBot;

public class ViewBasic extends AbstractView {

    @Override
    public String getName() {
        return "Базовое";
    }

    @Override
    public Color getBotColor(BasicBot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            return color;
        } else if ((bot.alive == BasicBot.LV_ORGANIC_SINK) || (bot.alive == BasicBot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == BasicBot.LV_ALIVE) {
            int green = (int) (bot.c_green - ((bot.c_green * bot.health) / 2000));
            int blue = (int) (bot.c_blue * 0.8 - ((bot.c_blue * bot.mineral) / 2000));
            color = new Color(validColor(bot.c_red), validColor(green), validColor(blue));
        }
        return color;
    }
}
