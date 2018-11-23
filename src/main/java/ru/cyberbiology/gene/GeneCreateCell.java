package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Многоклеточность ( создание потомка, приклееного к боту).
 *
 * @author Nickolay
 */
public class GeneCreateCell extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // 0 - если бот не входит в многоклеточную цепочку
        // 1 или 2 - если бот является крайним в цепочке
        // 3 - если бот внутри цепочки
        int a = bot.isMulti();  // 0 - нету, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
        if (a == 3) {
            bot.Double();
        } else {            // если бот уже находится внутри цепочки, то новый бот рождается свободным
            bot.multi();    // в другом случае, новый бот рождается приклеенным к боту-предку
        }
        bot.incCommandAddress(1);   // увеличиваем адрес текущей команды на 1
        return true;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "многоклеточность ( создание потомка, приклееного к боту )";
    }
}