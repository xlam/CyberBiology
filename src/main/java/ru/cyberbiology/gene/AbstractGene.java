package ru.cyberbiology.gene;

/**
 * Абстрактный класс обработчика гена. Введен, если какие либо методы
 * обработчиков будут общими, тогда их можно реализовать здесь
 *
 * @author Nickolay
 *
 */
public abstract class AbstractGene implements Gene {

    /**
     * Возвращает тип направления, вычисляемый из переданного значения.
     * @param value значение для вычисления
     * @return  0 - относительное направление
     *          1 - абсолютное направление
     */
    public int getDirectionType(int value) {
        return value % 2;
    }
}
