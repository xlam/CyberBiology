package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * Cменить направление относительно.
 *
 * @author Nickolay
 *
 */
public class GeneChangeDirection extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        // вычисляем новое направление
        int param = Constant.DIRECTION[bot.getParam()];
        int directionType = getDirectionType(bot.getParamByIndex(2));
        if (directionType == 0) {
            int newdrct = bot.getDirection() + param;   // полученное число прибавляем к значению направления бота
            bot.setDirection(newdrct % 8);  // результат должен быть в пределах от 0 до 8
        } else {
            bot.setDirection(param);
        }
        bot.incCommandAddress(2);       // адрес текущей команды увеличивается на 2,
        return false;
    }

    @Override
    public String getDescription() {
        return "сменить направление";
    }
}
