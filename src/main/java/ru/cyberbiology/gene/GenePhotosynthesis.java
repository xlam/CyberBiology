package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Фотосинтез.
 *
 * @author Nickolay
 */
public class GenePhotosynthesis extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        bot.eatSun();               // выполняем команду фотосинтеза
        bot.incCommandAddress(1);   // адрес текущей команды увеличивается на 1,

        return true;// выходим, так как команда фотосинтез - завершающая
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "фотосинтез";
    }
}
