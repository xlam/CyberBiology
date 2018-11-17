package ru.cyberbiology.util;

/**
 * Измерение произволительности (пересчетов в секунду). Оригинальный алгоритм
 * взят отсюда http://tv-games.ru/forum/blog.php?b=2025
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class PerfMeter {

    private static double lastTime = System.nanoTime(); // предыдущее время
    private static double nowTime;      // текущее время
    private static double rate = 0;     // частота кадров
    private static double diff = 0;     // интервал между замерами

    /**
     * Замеряет время и считает частоту кадров.
     */
    public static void tick() {
        nowTime = System.nanoTime();
        diff = nowTime - lastTime;
        rate = 1000000000.0 / diff;
        lastTime = nowTime;
    }

    /**
     * Запоминает начальное время.
     */
    public static void start() {
        lastTime = System.nanoTime();
    }

    /*
     * Замеряет конечное время и считает частоту пересчетов.
     */
    public static void finish() {
        tick();
    }

    /**
     * Возвращает текущее измеренное значение в виде целого числа.
     *
     * @return
     */
    public static int getInt() {
        return (int) Math.round(rate);
    }

    /**
     * Возвращает интервал времени между замерами.
     *
     * @return
     */
    public static double getDiff() {
        return diff;
    }

    /*
     * Возвращает частоту пересчетов в виде строки.
     */
    public static String show() {
        return String.format("%3.1f", PerfMeter.rate);
    }

}
