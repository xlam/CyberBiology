package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Посмотреть в относительном напралении.
 *
 * @author Nickolay
 */
public class GeneLookRelativeDirection extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];         // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.seeBots(drct, 0));    // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
        return false;
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "посмотреть  в относительном напралении";
    }
}
