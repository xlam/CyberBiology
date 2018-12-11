package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Съесть в относительном напралении.
 *
 * @author Nickolay
 */
public class GeneEatRelativeDirection extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.eat(drct, 0));    // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        //смещение условного перехода  стена - 2 пусто - 3 органика - 4 живой - 5
        return true;    // выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription() {
        return "съесть в относительном напралении";
    }
}
