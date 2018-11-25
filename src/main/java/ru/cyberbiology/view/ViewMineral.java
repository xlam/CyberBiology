package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.Bot;
import ru.cyberbiology.prototype.view.View;

public class ViewMineral extends View {

    @Override
    public String getName() {
        return "Минералы";
    }

    @Override
    public Color getBotColor(Bot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            // Color.WHITE
        } else if ((bot.alive == Bot.LV_ORGANIC_SINK) || (bot.alive == Bot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == Bot.LV_ALIVE) {
            // черный цвет для умерших в данном пересчете ботов
            int colorRed = 0;
            int colorGreen = 0;
            int colorBlue = 0;

            // цвет живых ботов в зависимости от энергии
            if (bot.mineral > 0) {
                colorBlue = 255;
                colorGreen = (int) Math.round(Math.abs(bot.mineral * 0.255 - 255));
            }

            color = new Color(colorRed, colorGreen, colorBlue);
        }
        return color;
    }
}
