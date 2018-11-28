package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Минералы прибавляются?.
 *
 * @author Nickolay
 */
public class GeneIsMineralGrow extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        if (bot.getY() > (bot.getWorld().getHeight() / 2)) {
            bot.indirectIncCmdAddress(1);
        } else {
            bot.indirectIncCmdAddress(2);
        }
        return false;
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "минералы прибавляются?";
    }
}
