package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Шаг в относительном напралении.
 *
 * @author Nickolay
  */
public class GeneStepInRelativeDirection extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        if (bot.isMulti() == 0) {   // бот многоклеточный? перемещаются только одноклеточные
            int drct = Const.DIRECTION[bot.getParam()];   // вычисляем направление из следующего за командой байта
            bot.indirectIncCmdAddress(bot.move(drct, 0)); // меняем адрес текущей команды
            // в зависимости от того, что было в этом направлении
            // смещение условного перехода 2-пусто  3-стена  4-органика 5-бот 6-родня
        } else {
            bot.incCommandAddress(2);
        }
        return true;    // выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "шаг   в относительном направлении ";
    }
}
