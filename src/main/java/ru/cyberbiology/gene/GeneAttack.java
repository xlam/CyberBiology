package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * //****************************************************************************
 * //................ генная атака ................................... if
 * (command == 49) { // бот атакует геном соседа, на которого он повернут
 * botGenAttack(this); // случайным образом меняет один байт
 * botIncCommandAddress(this, 1); break; } // после её выполнения, управление
 * передаётся следующему боту
 *
 * @author Nickolay
 *
 */
public class GeneAttack extends AbstractBotGeneController {

    @Override
    public boolean onGene(Bot bot) {
        bot.genAttack(); // случайным образом меняет один байт
        bot.incCommandAddress(1);
        return true;//// выходим, так как команда мутировать - завершающая
    }

    @Override
    public String getDescription() {
        return "генная атака";
    }
}
