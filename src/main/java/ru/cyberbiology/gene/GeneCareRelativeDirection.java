package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;
import ru.cyberbiology.Constant;

/**
 * //******************************************************************************
 * // делиться - если у бота больше энергии или минералов, чем у соседа, то они
 * распределяются поровну //............. делится в относительном напралении
 * ........................ if ((command == 32) || (command == 42)) { // здесь я
 * увеличил шансы появления этой команды int drct = botGetParam(this) % 8; //
 * вычисляем направление из следующего за командой байта
 * botIndirectIncCmdAddress(this, botCare(this, drct, 0)); // меняем адрес
 * текущей команды // в зависимости от того, что было в этом направлении //
 * стена - 2 пусто - 3 органика - 4 удачно - 5 }
 *
 * @author Nickolay
 *
 */
public class GeneCareRelativeDirection extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        int drct = Constant.DIRECTION[bot.getParam()];       // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.care(drct, 0)); // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
        return false;
    }

    @Override
    public String getDescription() {
        return "поделится в относительном напралении";
    }
}
