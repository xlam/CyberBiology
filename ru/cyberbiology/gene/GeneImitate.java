package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Имитирование, подражание в поведении. Команда копирует случайный участок
 * генома друго бота в свой геном.
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class GeneImitate extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // направление действия
        int dir = Const.DIRECTION[bot.getParam()];
        // адрес начала копирования в геноме другого бота
        bot.incCommandAddress(bot.imitate(dir));
        // команда завершающая
        return true;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "имитирование";
    }

}