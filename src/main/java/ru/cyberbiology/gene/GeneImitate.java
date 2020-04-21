package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Имитирование, подражание в поведении. Команда копирует случайный участок
 * генома друго бота в свой геном.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class GeneImitate extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        // направление действия
        int dir = Constant.DIRECTION[bot.getParam()];
        // адрес начала копирования в геноме другого бота
        bot.incCommandAddress(bot.imitate(dir));
        // команда завершающая
        return true;
    }

    @Override
    public String getDescription() {
        return "имитирование";
    }

}
