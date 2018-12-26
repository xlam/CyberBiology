package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Паразитирование. Забирает жизненную силу сругого бота.
 * 
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class GenePest extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        bot.pestAttack();   // забирает жизненную энергию другого бота
        bot.incCommandAddress(1);
        return false;       // паразит может "ходить" несколько раз
    }

    @Override
    public String getDescription() {
        return "паразитирование";
    }
}
