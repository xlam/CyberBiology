package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Сколько минералов.
 *
 * @author Nickolay
 */
public class GeneMyMineral extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int param = bot.getParam() * 1000 / Bot.MIND_SIZE;
        if (bot.getMineral() < param) {
            bot.indirectIncCmdAddress(2);
        } else {
            bot.indirectIncCmdAddress(3);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "сколько  минералов";
    }
}
