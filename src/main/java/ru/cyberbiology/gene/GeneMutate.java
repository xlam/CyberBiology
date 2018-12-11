package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Мутировать. Спорная команда, во время её выполнения меняются случайным
 * образом две случайные команды. Читал, что микроорганизмы могут усилить
 * вероятность мутации своего генома в неблагоприятных условиях.
 *
 * @author Nickolay
 */
public class GeneMutate extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        bot.modifyMind();
        bot.modifyMind();
        bot.incCommandAddress(1);
        return true; // выходим, так как команда мутировать - завершающая
    }

    @Override
    public String getDescription() {
        return "мутировать";
    }
}
