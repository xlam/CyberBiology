package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Шаг в относительном напралении.
 *
 * @author Nickolay
 */
public class GeneStepInRelativeDirection extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        if (bot.isMulti() == 0) {   // бот многоклеточный? перемещаются только одноклеточные
            int drct = Constant.DIRECTION[bot.getParam()];   // вычисляем направление из следующего за командой байта
            bot.indirectIncCmdAddress(bot.move(drct, 0)); // меняем адрес текущей команды
            // в зависимости от того, что было в этом направлении
            // смещение условного перехода 2-пусто  3-стена  4-органика 5-бот 6-родня
        } else {
            bot.incCommandAddress(2);
        }
        return true;    // выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription() {
        return "шаг   в относительном направлении ";
    }
}
