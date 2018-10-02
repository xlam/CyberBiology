package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Фотосинтез.
 *
 * @author Nickolay
 */
public class GenePhotosynthesis extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        bot.eatSun();               // выполняем команду фотосинтеза
        bot.incCommandAddress(1);   // адрес текущей команды увеличивается на 1,

        return true;// выходим, так как команда фотосинтез - завершающая
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "фотосинтез";
    }
}
