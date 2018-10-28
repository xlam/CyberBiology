package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Паразитирование. Забирает жизненную силу сругого бота.
 * 
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class GenePest extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        bot.pestAttack();   // забирает жизненную энергию другого бота
        bot.incCommandAddress(1);
        return false;       // паразит может "ходить" несколько раз
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "паразитирование";
    }
}
