package ru.cyberbiology.gene;

import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.gene.ABotGeneController;

/**
 * Какой мой уровень (на какой высоте бот)?.
 *
 * @author Nickolay
 */
public class GeneMyLevel extends ABotGeneController {

    @Override
    public boolean onGene(IBot bot) {
        // байт в геноме может иметь значение от 0 до 63
        // умножая значение байта на 1,5 получаем значение от 0 до 95
        int param = bot.getParam() * bot.getWorld().getHeight() / IBot.MIND_SIZE;   // берем следующий за командой байт и умножаем на 1,5
        // если уровень бота ниже, чем полученное значение,
        // то прибавляем к указатели текущей команды значение 2-го байта, после выполняемой команды
        if (bot.getY() < param) {
            bot.indirectIncCmdAddress(2);
        } else { // иначе прибавляем к указатели текущей команды значение 3-го байта, после выполняемой команды
            bot.indirectIncCmdAddress(3);
        }
        return false;
    }

    @Override
    public String getDescription(IBot bot, int i) {
        return "какой мой уровень (на какой высоте бот)";
    }
}
