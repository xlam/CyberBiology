package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Отдать в абсолютном направлении.
 *
 * @author Nickolay
 */
public class GeneGiveAbsolutelyDirection extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        int drct = Const.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.give(drct, 1));   // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // стена - 2 пусто - 3 органика - 4 удачно - 5
        return false;
    }

    public String getDescription(IBot bot, int i) {
        return "отдать  в абсолютном направлении";
    }
}
