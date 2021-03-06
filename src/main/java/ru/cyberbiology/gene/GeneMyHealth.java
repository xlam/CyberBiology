package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Какое моё здоровье?.
 *
 * @author Nickolay
 */
public class GeneMyHealth extends AbstractGene {

    @Override
    public boolean exec(Bot bot) {
        // байт в геноме может иметь значение от 0 до 63
        // умножая значение байта на 15 получаем значение от 0 до 945
        int param = bot.getParam() * 1000 / Bot.MIND_SIZE;   // берем следующий за командой байт и умножаем на 15
        // если здоровье бота ниже, чем полученное значение,
        // то прибавляем к указатели текущей команды значение 2-го байта, после выполняемой команды
        if (bot.getHealth() < param) {
            bot.indirectIncCmdAddress(2);
        } else { // иначе прибавляем к указатели текущей команды значение 3-го байта, после выполняемой команды
            bot.indirectIncCmdAddress(3);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "какое моё здоровье";
    }
}
