package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Cменить направление относительно.
 *
 * @author Nickolay
 *
 */
public class GeneChangeDirectionRelative extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        // вычисляем новое направление
        int param = Constant.DIRECTION[bot.getParam()];          // берём следующи байт за командой и вычисляем остаток от деления на 8
        int newdrct = bot.getDirection() + param;            // полученное число прибавляем к значению направления бота
        if (newdrct >= 8) {
            newdrct = newdrct - 8;
        } // результат должен быть в пределах от 0 до 8
        bot.setDirection(newdrct);
        bot.incCommandAddress(2);                              // адрес текущей команды увеличивается на 2,

        return false;
    }

    @Override
    public String getDescription() {
        return "сменить направление относительно";
    }
}
