package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.BasicBot;

public class ViewPest extends AbstractView {

    @Override
    public String getName() {
        return "Паразиты";
    }

    @Override
    public Color getBotColor(BasicBot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            return color;
        } else if ((bot.alive == BasicBot.LV_ORGANIC_HOLD) || (bot.alive == BasicBot.LV_ORGANIC_SINK)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == BasicBot.LV_ALIVE) {
            // цвет бота без генов паразитизма
            int colorRed = 170;
            int colorGreen = 170;
            int colorBlue = 170;

            // бот-паразит, добавляем красноты
            if (bot.pest > 0) {
                colorRed = 255;
                colorGreen = 255 - bot.pest * 4;
                if (colorGreen < 0) {
                    colorGreen = 0;
                }
                colorBlue = 0;
            }

            color = new Color(colorRed, colorGreen, colorBlue);
        }
        return color;
    }
}
