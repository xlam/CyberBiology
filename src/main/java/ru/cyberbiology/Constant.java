package ru.cyberbiology;

/**
 * Константы приложения.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public interface Constant {

    public static final int DEFAULT_BOT_SIZE = 4;

    public static final int[] DIRECTION = {
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,
        0, 1, 2, 3, 4, 5, 6, 7,};

    /**
     * Приращение координаты X в зависимости от заданного направления.
     * В индексах массива содержится результирующее направление, которое равно
     * сумме текущего направления бота и параметра относительного направления
     * (всего 15 вариантов от 0 до 14, если параметр относительности не более 7).
     * Значения массива это соответствующие результирующему направлению
     * приращения координаты.
     * Приращение по абсолютному направлению укладывается в индексы от 0 до 7.
     */
    public static final int[] INCREMENT_X = {
        -1, 0, 1, 1, 1, 0, -1, -1, -1, 0, 1, 1, 1, 0, -1,};

    /**
     * Приращение координаты Y в зависимости заданного направления.
     * В индексах массива содержится результирующее направление, которое равно
     * сумме текущего направления бота и параметра относительного направления
     * (всего 15 вариантов от 0 до 14, если параметр относительности не более 7).
     * Значения массива это соответствующие результирующему направлению
     * приращения координаты.
     * Приращение по абсолютному направлению укладывается в индексы от 0 до 7.
     */
    public static final int[] INCREMENT_Y = {
        -1, -1, -1, 0, 1, 1, 1, 0, -1, -1, -1, 0, 1, 1, 1,};
}
