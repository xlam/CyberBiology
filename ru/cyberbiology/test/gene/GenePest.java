package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;

public class GenePest extends ABotGeneController
{
	@Override
	public boolean onGene(IBot bot)
	{
		bot.pestAttack(); // забирает жизненную энергию другого бота
        bot.incCommandAddress(1);
        return false; // паразит может "ходить" несколько раз
	}

    @Override
	public String getDescription(IBot bot, int i)
	{
		return "паразитирование";
	}
}
