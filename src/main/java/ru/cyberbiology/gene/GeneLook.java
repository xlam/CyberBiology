package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Посмотреть.
 *
 * @author Nickolay
 */
public class GeneLook extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];         // вычисляем направление из следующего за командой байта
        int directionType = getDirectionType(bot.getParamByIndex(2));
        bot.indirectIncCmdAddress(bot.seeBots(drct, directionType));    // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
        return false;
    }

    @Override
    public String getDescription() {
        return "посмотреть";
    }
}
