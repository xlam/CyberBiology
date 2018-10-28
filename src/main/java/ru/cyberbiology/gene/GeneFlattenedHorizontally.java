package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Выравнится по горизонтали.
 * @author Nickolay
 */
public class GeneFlattenedHorizontally extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        if (Math.random() < 0.5) {  // кидаем монетку
            bot.setDirection(3);    // если ноль, то поворачиваем в одну сторону
        } else {
            bot.setDirection(7);    // если один, то поворачиваем в другую сторону
        }
        bot.incCommandAddress(1);   // увеличиваем указатель текущей команды на 1
        return false;
    }

    public String getDescription(IBot bot, int i) {
        return "выравнится по горизонтали";
    }
}
