package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Приход энергии есть?.
 *
 * @author Nickolay
 */
public class GeneIsHealthGrow extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // функция full_aroud() возвращает  1, если бот окружен и 2, если нет
        // увеличиваем значение указателя текущей команды
        // на значение следующего байта после команды или 2-го байта после команды
        // в зависимости от того, окружен бот или нет
        bot.indirectIncCmdAddress(bot.isHealthGrow());
        return false;
    }

    public String getDescription(IBot bot, int i) {
        return "приход энергии есть?";
    }
}
