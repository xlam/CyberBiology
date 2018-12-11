package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * //............... сменить направление абсолютно .... if (command == 24) { //
 * записываем новое значение направления direction = botGetParam(this) % 8; //
 * берем следующий байт и вычисляем остаток от деления на 8
 * botIncCommandAddress(this, 2); // адрес текущей команды увеличивается на 2, }
 *
 * @author Nickolay
 *
 */
public class GeneChangeDirectionAbsolutely extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        // записываем новое значение направления
        bot.setDirection(Constant.DIRECTION[bot.getParam()]);  // берем следующий байт и вычисляем остаток от деления на 8
        bot.incCommandAddress(2);                  // адрес текущей команды увеличивается на 2,

        return false;
    }

    @Override
    public String getDescription() {
        return "сменить направление абсолютно";
    }
}
