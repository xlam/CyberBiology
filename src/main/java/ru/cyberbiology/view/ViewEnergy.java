package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.BasicBot;

public class ViewEnergy extends AbstractView {

    @Override
    public String getName() {
        return "Энергия";
    }

    @Override
    public Color getBotColor(BasicBot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            return color;
        } else if ((bot.alive == BasicBot.LV_ORGANIC_SINK) || (bot.alive == BasicBot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == BasicBot.LV_ALIVE) {
            // черный цвет для умерших в данном пересчете ботов
            int colorRed = 0;
            int colorGreen = 0;
            int colorBlue = 0;

            // цвет живых ботов в зависимости от энергии
            if (bot.health > 0) {
                colorRed = 255;
                colorGreen = (int) Math.round(Math.abs(bot.health * 0.255 - 255));
            }

            color = new Color(validColor(colorRed), validColor(colorGreen), validColor(colorBlue));
        }
        return color;
    }
}
