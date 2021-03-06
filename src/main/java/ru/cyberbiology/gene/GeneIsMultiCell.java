package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Многоклеточный ли я?.
 *
 * @author Nickolay
 */
public class GeneIsMultiCell extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
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
    public String getDescription() {
        return "многоклеточный ли я ?";
    }
}
