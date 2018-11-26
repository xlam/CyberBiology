package ru.cyberbiology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.cyberbiology.gene.GeneMutate;
import ru.cyberbiology.prototype.IWindow;
import ru.cyberbiology.prototype.IWorld;
import ru.cyberbiology.util.ProjectProperties;
import ru.cyberbiology.util.PerfMeter;

public class World implements IWorld {

    public World world;
    public IWindow window;

    /**
     * Шаг отрисовки. Состояние мира отрисовывается на каждом PAINT_STEP
     * пересчете. Для комфортной визуализации рекомендуется зачение от 5 до 15.
     * Большие значения удобно использовать для проведения оптимизаций кода и
     * длительной работы мира. Можно изменять через меню `Шаг отрисовки`.
     */
    public static final int PAINT_STEP = 1000;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public int width;
    public int height;

    public Bot[] matrix; // Матрица мира
    public int generation;
    public int population;
    public int organic;
    public int pests;
    public int pestGenes;

    boolean started;
    private Worker thread;

    private ProjectProperties properties;
    private static String mineralsAccumulation;

    protected World(IWindow win) {
        world = this;
        window = win;
        population = 0;
        generation = 0;
        organic = 0;
        pests = 0;
        pestGenes = 0;
        properties = ProjectProperties.getInstance();
    }

    public World(IWindow win, int width, int height) {
        this(win);
        this.setSize(width, height);
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = new Bot[height * width];
    }

    @Override
    public void setBot(Bot bot) {
        matrix[width * bot.y + bot.x] = bot;
    }

    @Override
    public void clearBot(int x, int y) {
        matrix[width * y + x] = null;
    }

    @Override
    public void paint() {
        window.paint();
    }

    @Override
    public ProjectProperties getProperties() {
        return window.getProperties();
    }

    /**
     * Сбой в программе генома. Перемещает указатель текущей команды каждого
     * бота случайным образом.
     */
    void jumpBotsCmdAdress() {
        if (started()) {
            stop();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Bot bot = getBot(x, y);
                if (bot != null && bot.alive == Bot.LV_ALIVE) {
                    bot.adr = (byte) (Math.random() * Bot.MIND_SIZE);
                }
            }
        }
        start();
    }

    class Worker extends Thread {
        @Override
        public void run() {
            started = true;// Флаг работы потока, если установить в false поток
            // заканчивает работу
            while (started) {

                /**
                 * Пересчет мира.
                 * Параллельный стрим работает быстрее предыдущего многопоточного
                 * варианта? (см. коммит 0bc13cc)
                 * Быстрее, но не намного, процентов на 5. Зато реализация намного проще.
                 * Будем надеяться на отсутствие побочных эффектов.
                 */
                Arrays.stream(matrix)
                        .filter(b -> b != null)
                        .parallel()
                        .forEach(b -> b.step());

                generation = generation + 1;
                // отрисовка на экран через каждые "paintstep" шагов
                if (generation % Integer.parseInt(properties.getProperty("paintstep", "" + PAINT_STEP)) == 0) {
                    updateStats();
                    // замеряем время пересчета 10 итераций без учета отрисовки
                    PerfMeter.tick();
                    paint(); // отображаем текущее состояние симуляции на экран
                }
                // sleep(); // пауза между ходами, если надо уменьшить скорость
            }
            updateStats();
            paint();// если запаузили рисуем актуальную картинку
            started = false;// Закончили работу
        }
    }

    /**
    * Подсчет фактических значений населения и органики.
    */
    private void updateStats() {
        // возможно будет быстрее просто пройтись по массиву matrix?
        population = (int) Arrays.stream(matrix)
               .filter(b -> b != null && b.isAlive())
               .parallel()
               .count();
        organic = (int) Arrays.stream(matrix)
               .filter(b -> b != null && b.isOrganic())
               .parallel()
               .count();
        pests = (int) Arrays.stream(matrix)
               .filter(b -> b != null && b.pest > 0)
               .parallel()
               .count();
        pestGenes = (int) Arrays.stream(matrix)
               .filter(b -> b != null && b.pest > 0)
               .mapToInt(b -> b.pest)
               .parallel()
               .sum();
    }

    public final void generateAdam() {
        // ========== 1 ==============
        // бот номер 1 - это уже реальный бот
        Bot bot = new Bot(this);

        bot.adr = 0;
        bot.x = width / 2; // координаты бота
        bot.y = height / 2;
        bot.health = 990; // энергия
        bot.mineral = 0; // минералы
        bot.alive = 3; // отмечаем, что бот живой
        bot.c_red = 170; // задаем цвет бота
        bot.c_blue = 170;
        bot.c_green = 170;
        bot.direction = 5; // направление
        bot.mprev = null; // бот не входит в многоклеточные цепочки, поэтому
        // ссылки
        bot.mnext = null; // на предыдущего, следующего в многоклеточной цепочке
        // пусты
        for (int i = 0; i < 64; i++) { // заполняем геном командой 25 - фотосинтез
            bot.mind[i] = 25;
        }

        setBot(bot); // даём ссылку на бота в массиве world[]
    }

    @Override
    public void restoreLinks() {
        Bot bot;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bot = getBot(x, y);
                if (bot != null && bot.alive == bot.LV_ALIVE) {
                    if (bot.mprevX > -1 && bot.mprevY > -1) {
                        bot.mprev = getBot(bot.mprevX, bot.mprevY);
                    }
                    if (bot.mnextX > -1 && bot.mnextY > -1) {
                        bot.mnext = getBot(bot.mnextX, bot.mnextY);
                    }
                }
            }
        }
    }

    /**
     * Восстанавливает статистику мира при загрузке. Не восстанавливает
     * generation (на текущий момент generation не сохраняется в RecordManager)
     */
    @Override
    public final void restoreStats() {
        Bot bot;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bot = getBot(x, y);
                if (bot == null || bot.alive == bot.LV_FREE) {
                    continue;
                }
                if (bot.alive == bot.LV_ORGANIC_HOLD || bot.alive == bot.LV_ORGANIC_SINK) {
                    organic++;
                }
                population++;
                // TODO pestGenes всегда равно pest*2 ?!
                if (bot.pest > 0) {
                    pestGenes += bot.pest;
                    pests++;
                }
            }
        }
    }

    /**
     * Мутирует указанное количество живых ботов случайным образом.
     *
     * @param percent Количество ботов в процентах (1..100)
     * @param mutatesCount Количество мутаций на одного бота (1..64)
     */
    public void randomMutation(int percent, int mutatesCount) {
        // получить список живых ботов
        ArrayList<Bot> aliveBots = new ArrayList();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Bot bot = getBot(x, y);
                if (bot != null && bot.alive == bot.LV_ALIVE) {
                    aliveBots.add(bot);
                }
            }
        }

        // размер группы мутирующих ботов 10% от живых
        int aliveBotsCount = aliveBots.size();
        int mutantsCount = (int) Math.round(aliveBotsCount * percent / 100);
        System.out.println("Mutating " + percent + "% of alive bots (" + mutantsCount + " of " + aliveBotsCount + ")");

        // мутация
        Random rnd = new Random();
        GeneMutate mutagen = new GeneMutate();
        for (int i = 0; i < mutantsCount; i++) {
            Bot bot = aliveBots.get(rnd.nextInt(aliveBotsCount - 1));
            //System.out.println("Mutating " + bot);

            // Мутации одного гена оказалось недостаточно чтобы
            // встряхнуть заснувший мир
            for (int j = 0; j < mutatesCount; j++) {
                mutagen.onGene(bot);
            }
        }
    }

    public final boolean started() {
        return this.thread != null;
    }

    public final void start() {
        if (!this.started()) {
            mineralsAccumulation = properties.getProperty("MineralsAccumulation", "classic");
            this.thread = new Worker();
            // запуск таймера при запуске потока
            PerfMeter.start();
            this.thread.start();
        }
    }

    public final void stop() {
        if (thread == null) {
            return;
        }
        started = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread = null;
    }

    /**
     * Вычисляет количество минералов, которое может накопить бот на данной глубине.
     * @param y глубина
     * @return количество минералов
     */
    final int getMineralsAt(int y) {

        if (mineralsAccumulation.equals("height")) {
            return getMineralsAccHeight(y);
        }

        if (mineralsAccumulation.equals("classic")) {
            return getMineralsAccClassic(y);
        }

        return 0;
    }

    private int getMineralsAccClassic(int y) {
        // если бот находится на глубине ниже 48 уровня
        int minerals = 0;
        if (y > world.height / 2) {
            minerals++;
            if (y > world.height / 6 * 4) {
                minerals++;
            }
            if (y > world.height / 6 * 5) {
                minerals++;
            }
        }
        return minerals;
    }

    private int getMineralsAccHeight(int y) {
        if (RANDOM.nextInt(101) < (int) (y / (height * 0.01))) {
            return 0;
        }
        // количество получаемых минералов от 1 на самом верху до 5 в самом низу
        return RANDOM.nextInt(1, 2 + (int) (4 * y / height));
    }

    public final Bot getBot(int botX, int botY) {
        return this.matrix[width * botY + botX];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Bot[] getWorldArray() {
        return this.matrix;
    }
}
