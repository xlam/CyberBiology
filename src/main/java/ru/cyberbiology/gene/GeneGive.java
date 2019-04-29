package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Отдать.
 *
 * @author Nickolay
 */
public class GeneGive extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        int directionType = getDirectionType(bot.getParamByIndex(2));
        bot.indirectIncCmdAddress(bot.give(drct, directionType));   // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // стена - 2 пусто - 3 органика - 4 удачно - 5
        return false;
    }

    @Override
    public String getDescription() {
        return "отдать";
    }
}
