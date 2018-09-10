package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * //............... сменить направление относительно .... 
 * @author Nickolay
 *
 */
public class GeneChangeDirectionRelative extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // вычисляем новое направление
        int param = Const.DIRECTION[bot.getParam()];          // берём следующи байт за командой и вычисляем остаток от деления на 8
        int newdrct = bot.getDirection() + param;            // полученное число прибавляем к значению направления бота
        if (newdrct >= 8) {
            newdrct = newdrct - 8;
        }// результат должен быть в пределах от 0 до 8
        bot.setDirection(newdrct);
        bot.incCommandAddress(2);                              // адрес текущей команды увеличивается на 2,

        return false;
    }

    public String getDescription(IBot bot, int i) {
        return "сменить направление относительно";
    }
}
