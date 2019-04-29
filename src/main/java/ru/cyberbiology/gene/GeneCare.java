package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Делиться - если у бота больше энергии или минералов, чем у соседа, то они распределяются поровну.
 */
public class GeneCare extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];
        int directionType = getDirectionType(bot.getParamByIndex(2));
        // меняем адрес текущей команды в зависимости от того, что было в этом направлении
        // пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
        bot.indirectIncCmdAddress(bot.care(drct, directionType));
        return false;
    }

    @Override
    public String getDescription() {
        return "поделиться ресурсами";
    }
}
