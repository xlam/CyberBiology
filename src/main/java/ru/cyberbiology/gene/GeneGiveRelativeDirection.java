package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Отдать - безвозмездно отдать часть энергии и минералов соседу.
 *
 * @author Nickolay
 */
public class GeneGiveRelativeDirection extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];     // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.give(drct, 0));   // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // стена - 2 пусто - 3 органика - 4 удачно - 5
        return false;
    }

    @Override
    public String getDescription() {
        return "отдать - безвозмездно отдать часть энергии и минералов соседу";
    }
}
