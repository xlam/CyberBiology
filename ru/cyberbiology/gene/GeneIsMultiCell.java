package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Многоклеточный ли я?.
 *
 * @author Nickolay
 */
public class GeneIsMultiCell extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        int mu = bot.isMulti();
        if (mu == 0) {
            bot.indirectIncCmdAddress(1); // бот свободно живущий
        } else if (mu == 3) {
            bot.indirectIncCmdAddress(3); // бот внутри цепочки
        } else {
            bot.indirectIncCmdAddress(2); // бот скраю цепочки
        }
        return false;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "многоклеточный ли я ?";
    }
}
