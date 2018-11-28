package ru.cyberbiology;

import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Sergey Sokolov <xlamserg@gmail.com>
 */
public class DirectionArraySpeedTest {

    /**
     * Количество команд в геноме.
     */
    private final int SIZE = 64;

    /**
     * Количество операций вычисления направления.
     */
    private final int RUNS = 40000;

    /**
     * Ожидаемый прирост скорости вычисления направления.
     * 2 означает увеличение скорости в 2 раза или на 100%.
     */
    private final int ASSUME_SPEEDUP = 2;

    @Test
    public void checkDirectionArrayAccessSpeed() {
        Random r = new Random();
        long start;
        int dir;

        // замер скорости операции %
        start = System.nanoTime();
        for (int i=0; i<RUNS; i++) {
            dir = r.nextInt(SIZE) % 8;
        }
        double t1 = (System.nanoTime() - start) / 1000000f;

        // замер скорости доступа к предварительно расчитанным значениям
        start = System.nanoTime();
        for (int i=0; i<RUNS; i++) {
            dir = Constant.DIRECTION[r.nextInt(SIZE)];
        }
        double t2 = (System.nanoTime() - start) / 1000000f;

        double speedUp = t1 / t2;

        // Вместо assert выведем предупреждение и продолжим тесты
        if (speedUp < 1) {
            System.out.printf("WARNING!: very low array access speed: t1: %.3fms; t2: %.3fms; speedup: %.2f\n", t1, t2, speedUp);
        }
        else if (speedUp < ASSUME_SPEEDUP) {
            System.out.printf("WARNING: array access speed: t1: %.3fms; t2: %.3fms; speedup: %.2f\n", t1, t2, speedUp);
        }
     }
}
