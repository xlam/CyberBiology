package ru.cyberbiology.view;

import java.awt.Color;
import ru.cyberbiology.BasicBot;

public class ViewMultiCell extends AbstractView {

    public ViewMultiCell() {
    }

    @Override
    public String getName() {
        return "Многоклеточные";
    }

    @Override
    public Color getBotColor(BasicBot bot) {

        Color color = Color.WHITE;

        if (bot == null) {
            return color;
        } else if ((bot.alive == BasicBot.LV_ORGANIC_SINK) || (bot.alive == BasicBot.LV_ORGANIC_HOLD)) {
            color = new Color(200, 200, 200);
        } else if (bot.alive == BasicBot.LV_ALIVE) {
            switch (bot.isMulti()) {
                case 1:     // - есть MPREV,
                    color = Color.MAGENTA;
                    break;
                case 2:     // - есть MNEXT,
                    color = Color.BLACK;
                    break;
                case 3:     // есть MPREV и MNEXT
                    color = Color.MAGENTA;
                    break;
                default:
                    int green = (int) (bot.c_green - ((bot.c_green * bot.health) / 2000));
                    if (green < 0) {
                        green = 0;
                    }
                    if (green > 255) {
                        green = 255;
                    }
                    int blue = (int) (bot.c_blue * 0.8 - ((bot.c_blue * bot.mineral) / 2000));
                    color = new Color(bot.c_red, green, blue);
                    break;
            }
        }
        return color;
    }
}
