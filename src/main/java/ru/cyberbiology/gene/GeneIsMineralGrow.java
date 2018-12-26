package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Минералы прибавляются?.
 *
 * @author Nickolay
 */
public class GeneIsMineralGrow extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        if (bot.getY() > (bot.getWorld().getHeight() / 2)) {
            bot.indirectIncCmdAddress(1);
        } else {
            bot.indirectIncCmdAddress(2);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "минералы прибавляются?";
    }
}
