package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Съесть в абсолютном направлении.
 *
 * @author Nickolay
 */
public class GeneEatAbsoluteDirection extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        int drct = Const.DIRECTION[bot.getParam()];       // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.eat(drct, 1)); // меняем адрес текущей команды
        return true;// выходим, так как команда шагнуть - завершающая
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "съесть  в абсолютном направлении";
    }
}
