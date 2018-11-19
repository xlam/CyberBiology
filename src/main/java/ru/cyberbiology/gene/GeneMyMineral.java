package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Сколько минералов.
 *
 * @author Nickolay
 */
public class GeneMyMineral extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        int param = bot.getParam() * 1000 / IBot.MIND_SIZE;
        if (bot.getMineral() < param) {
            bot.indirectIncCmdAddress(2);
        } else {
            bot.indirectIncCmdAddress(3);
        }
        return false;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "сколько  минералов";
    }
}
