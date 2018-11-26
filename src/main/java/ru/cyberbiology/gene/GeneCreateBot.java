package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Деление (создание свободноживущего потомка).
 *
 * @author Nickolay
 */
public class GeneCreateBot extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        // 0 - если бот не входит в многоклеточную цепочку
        // 1 или 2 - если бот является крайним в цепочке
        // 3 - если бот внутри цепочки
        int a = bot.isMulti();
        if ((a == 0) || (a == 3)) {
            bot.Double();   // если бот свободный или внутри цепочки, , то новый бот рождается свободным
        } else {
            bot.multi();    // если бот крайний в цепочке, новый бот рождается приклеенным к боту-предку
        }
        bot.incCommandAddress(1);
        return true;
    }

    @Override
    public String getDescription(Bot bot, int i) {
        return "деление (создание свободноживущего потомка)";
    }
}
