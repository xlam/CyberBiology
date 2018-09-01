package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.Const;
import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;

/**
//.............   посмотреть в абсолютном направлении ...................................
            if (command == 31)// пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
            {
                int drct = botGetParam(this) % 8;
                botIndirectIncCmdAddress(this, botSeeBots(this, drct, 1));
            }
 * @author Nickolay
 *
 */
public class GeneLookAbsoluteDirection extends ABotGeneController
{

	@Override
	public boolean onGene(IBot bot)
	{
        int drct = Const.DIRECTION[bot.getParam()];       // вычисляем направление из следующего за командой байта
        bot.indirectIncCmdAddress(bot.seeBots(drct, 1)); // меняем адрес текущей команды
        // в зависимости от того, что было в этом направлении
        // пусто - 2 стена - 3 органик - 4 бот -5 родня -  6
        return false;
	}
	@Override
	public String getDescription(IBot bot, int i)
	{
		return "посмотреть в абсолютном направлении";
	}
}
