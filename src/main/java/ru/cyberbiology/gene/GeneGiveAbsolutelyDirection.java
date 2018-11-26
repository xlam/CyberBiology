package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Отдать в абсолютном направлении.
 *
 * @author Nickolay
 */
public class GeneGiveAbsolutelyDirection extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.give(drct, 1));   // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // стена - 2 пусто - 3 органика - 4 удачно - 5
        return false;
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "отдать  в абсолютном направлении";
    }
}
