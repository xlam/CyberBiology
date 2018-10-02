package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Мутировать. Спорная команда, во время её выполнения меняются случайным
 * образом две случайные команды. Читал, что микроорганизмы могут усилить
 * вероятность мутации своего генома в неблагоприятных условиях.
 *
 * @author Nickolay
 */
public class GeneMutate extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        bot.modifyMind();
        bot.modifyMind();
        bot.incCommandAddress(1);
        return true; // выходим, так как команда мутировать - завершающая
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "мутировать";
    }
}
