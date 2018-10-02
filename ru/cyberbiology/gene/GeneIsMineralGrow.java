package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Минералы прибавляются?.
 *
 * @author Nickolay
 */
public class GeneIsMineralGrow extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        if (bot.getY() > (bot.getWorld().getHeight() / 2)) {
            bot.indirectIncCmdAddress(1);
        } else {
            bot.indirectIncCmdAddress(2);
        }
        return false;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "минералы прибавляются?";
    }
}
