package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Съесть в абсолютном направлении.
 *
 * @author Nickolay
 */
public class GeneEatAbsoluteDirection extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];       // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.eat(drct, 1)); // меняем адрес текущей команды
        return true;// выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription() {
        return "съесть  в абсолютном направлении";
    }
}
