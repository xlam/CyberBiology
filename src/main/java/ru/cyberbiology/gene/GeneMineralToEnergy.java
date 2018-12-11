package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Преобразовать минералы в энерию.
 *
 * @author Nickolay
 */
public class GeneMineralToEnergy extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        bot.mineral2Energy();
        bot.incCommandAddress(1);
        return true;
    }

    @Override
    public String getDescription() {
        return "преобразовать минералы в энерию";
    }
}
