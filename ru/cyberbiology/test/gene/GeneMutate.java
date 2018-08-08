package ru.cyberbiology.test.gene;

import ru.cyberbiology.test.World;
import ru.cyberbiology.test.prototype.IBot;
import ru.cyberbiology.test.prototype.gene.ABotGeneController;

/**
//*********************************************************************
//................      мутировать   ...................................
// спорная команда, во время её выполнения меняются случайным образом две случайные команды
// читал, что микроорганизмы могут усилить вероятность мутации своего генома в неблагоприятных условиях
            if (command == 48) {
            	byte ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
                byte mc = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mind[ma] = mc;

                ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mc = (byte) (Math.random() * MIND_SIZE);  // 0..63
                mind[ma] = mc;
                botIncCommandAddress(this, 1);
                break;     // выходим, так как команда мутировать - завершающая
            }
 * @author Nickolay
 *
 */
public class GeneMutate extends ABotGeneController
{

	@Override
	public boolean onGene(IBot bot)
	{
        bot.modifyMind();
        bot.modifyMind();
        bot.incCommandAddress(1);
        return true; // выходим, так как команда мутировать - завершающая
	}
	@Override
	public String getDescription(IBot bot, int i)
	{
		return "мутировать";
	}
}
