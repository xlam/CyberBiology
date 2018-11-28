package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Приход энергии есть?.
 *
 * @author Nickolay
 */
public class GeneIsHealthGrow extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        // функция full_aroud() возвращает  1, если бот окружен и 2, если нет
        // увеличиваем значение указателя текущей команды
        // на значение следующего байта после команды или 2-го байта после команды
        // в зависимости от того, окружен бот или нет
        bot.indirectIncCmdAddress(bot.isHealthGrow());
        return false;
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "приход энергии есть?";
    }
}
