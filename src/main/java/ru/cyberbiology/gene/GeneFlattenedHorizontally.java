package ru.cyberbiology.gene;

import java.util.concurrent.ThreadLocalRandom;
import ru.cyberbiology.Bot;

/**
 * Выравнится по горизонтали.
 *
 * @author Nickolay
 */
public class GeneFlattenedHorizontally extends AbstractBotGeneController {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public boolean onGene(Bot bot) {
        if (RANDOM.nextInt(101) < 50) {  // кидаем монетку
            bot.setDirection(3);    // если ноль, то поворачиваем в одну сторону
        } else {
            bot.setDirection(7);    // если один, то поворачиваем в другую сторону
        }
        bot.incCommandAddress(1);   // увеличиваем указатель текущей команды на 1
        return false;
    }

    @Override
    public String getDescription() {
        return "выравнится по горизонтали";
    }
}
