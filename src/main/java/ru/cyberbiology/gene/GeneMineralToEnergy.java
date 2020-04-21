package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Преобразовать минералы в энерию.
 *
 * @author Nickolay
 */
public class GeneMineralToEnergy extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        bot.mineral2Energy();
        bot.incCommandAddress(1);
        return true;
    }

    @Override
    public String getDescription() {
        return "преобразовать минералы в энерию";
    }
}
