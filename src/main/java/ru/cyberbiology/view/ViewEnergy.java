package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.Bot;
import ru.cyberbiology.prototype.view.View;

public class ViewEnergy extends View {

    public ViewEnergy() {
    }

    @Override
    public String getName() {
        return "Энергия";
    }

    @Override
    public Color getBotColor(Bot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            // Color.WHITE
        } else if ((bot.alive == bot.LV_ORGANIC_SINK) || (bot.alive == bot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == bot.LV_ALIVE) {
            // черный цвет для умерших в данном пересчете ботов
            int colorRed = 0;
            int colorGreen = 0;
            int colorBlue = 0;

            // цвет живых ботов в зависимости от энергии
            if (bot.health > 0) {
                colorRed = 255;
                colorGreen = (int) Math.round(Math.abs(bot.health * 0.255 - 255));
            }

            color = new Color(colorRed, colorGreen, colorBlue);
        }
        return color;
    }
}
