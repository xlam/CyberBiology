package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Паразитирование. Забирает жизненную силу сругого бота.
 * 
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class GenePest extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        bot.pestAttack();   // забирает жизненную энергию другого бота
        bot.incCommandAddress(1);
        return false;       // паразит может "ходить" несколько раз
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "паразитирование";
    }
}
