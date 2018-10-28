package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Преобразовать минералы в энерию.
 *
 * @author Nickolay
 */
public class GeneMineralToEnergy extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        bot.mineral2Energy();
        bot.incCommandAddress(1);
        return true;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "преобразовать минералы в энерию";
    }
}
