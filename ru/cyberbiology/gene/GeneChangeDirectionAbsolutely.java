package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * //............... сменить направление абсолютно .... if (command == 24) { //
 * записываем новое значение направления direction = botGetParam(this) % 8; //
 * берем следующий байт и вычисляем остаток от деления на 8
 * botIncCommandAddress(this, 2); // адрес текущей команды увеличивается на 2, }
 *
 * @author Nickolay
 *
 */
public class GeneChangeDirectionAbsolutely extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // записываем новое значение направления
        bot.setDirection(Const.DIRECTION[bot.getParam()]);  // берем следующий байт и вычисляем остаток от деления на 8
        bot.incCommandAddress(2);                  // адрес текущей команды увеличивается на 2,

        return false;
    }

    public String getDescription(IBot bot, int i) {
        return "сменить направление абсолютно";
    }
}
