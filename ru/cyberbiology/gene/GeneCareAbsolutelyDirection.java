package ru.cyberbiology.gene;

import ru.cyberbiology.Const;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
//******************************************************************************
// делиться - если у бота больше энергии или минералов, чем у соседа, то они распределяются поровну
            //.............   делится  в абсолютном направлении ........................
            if ((command == 33) || (command == 50)) {    // здесь я увеличил шансы появления этой команды
                int drct = botGetParam(this) % 8;
                botIndirectIncCmdAddress(this, botCare(this, drct, 1));  // стена - 2 пусто - 3 органика - 4 удачно - 5
            }
 * @author Nickolay
 *
 */
public class GeneCareAbsolutelyDirection extends ABotGeneController
{

	@Override
	public boolean onGene(IBot bot)
	{
        int drct = Const.DIRECTION[bot.getParam()];       // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.care(drct, 1)); // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // стена - 2 пусто - 3 органика - 4 удачно - 5
        return false;
	}
	public String getDescription(IBot bot, int i)
	{
		return "поделится  в абсолютном направлении";
	}
}
