package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Съесть.
 *
 * @author Nickolay
 */
public class GeneEat extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        int directionType = getDirectionType(bot.getParamByIndex(2));
        bot.indirectIncCmdAddress(bot.eat(drct, directionType));    // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        //смещение условного перехода  стена - 2 пусто - 3 органика - 4 живой - 5
        return true;    // выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription() {
        return "съесть";
    }
}
