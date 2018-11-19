package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Съесть в относительном напралении.
 *
 * @author Nickolay
 */
public class GeneEatRelativeDirection extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        int drct = Const.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.eat(drct, 0));    // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        //смещение условного перехода  стена - 2 пусто - 3 органика - 4 живой - 5
        return true;    // выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "съесть в относительном напралении";
    }
}
