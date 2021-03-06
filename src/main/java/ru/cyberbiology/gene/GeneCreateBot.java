package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Деление (создание свободноживущего потомка).
 *
 * @author Nickolay
 */
public class GeneCreateBot extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        // 0 - если бот не входит в многоклеточную цепочку
        // 1 или 2 - если бот является крайним в цепочке
        // 3 - если бот внутри цепочки
        int a = bot.isMulti();
        if ((a == 0) || (a == 3)) {
            bot.doubleFree();   // если бот свободный или внутри цепочки, , то новый бот рождается свободным
        } else {
            bot.doubleMulti();    // если бот крайний в цепочке, новый бот рождается приклеенным к боту-предку
        }
        bot.incCommandAddress(1);
        return true;
    }

    @Override
    public String getDescription() {
        return "деление (создание свободноживущего потомка)";
    }
}
