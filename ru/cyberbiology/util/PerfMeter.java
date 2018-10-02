package ru.cyberbiology.util;

/**
 * Измерение произволительности (пересчетов в секунду). Оригинальный алгоритм
 * взят отсюда http://tv-games.ru/forum/blog.php?b=2025
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class PerfMeter {

    static private double lastTime = System.nanoTime(); // предыдущее время
    static private double nowTime;      // текущее время
    static private double rate = 0;     // частота кадров
    static private double diff = 0;     // интервал между замерами

    /**
     * Замеряет время и считает частоту кадров.
     */
    static public void tick() {
        nowTime = System.nanoTime();
        diff = nowTime - lastTime;
        rate = 1000000000.0 / diff;
        lastTime = nowTime;
    }

    /**
     * Запоминает начальное время.
     */
    static public void start() {
        lastTime = System.nanoTime();
    }

    /*
     * Замеряет конечное время и считает частоту пересчетов.
     */
    static public void finish() {
        tick();
    }

    /**
     * Возвращает текущее измеренное значение в виде целого числа.
     *
     * @return
     */
    static public int getInt() {
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
