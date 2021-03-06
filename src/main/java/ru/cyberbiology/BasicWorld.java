package ru.cyberbiology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.cyberbiology.gene.Gene;
import ru.cyberbiology.gene.GeneCare;
import ru.cyberbiology.gene.GeneChangeDirection;
import ru.cyberbiology.gene.GeneCreateBot;
import ru.cyberbiology.gene.GeneCreateCell;
import ru.cyberbiology.gene.GeneEat;
import ru.cyberbiology.gene.GeneFlattenedHorizontally;
import ru.cyberbiology.gene.GeneFullAroud;
import ru.cyberbiology.gene.GeneGive;
import ru.cyberbiology.gene.GeneImitate;
import ru.cyberbiology.gene.GeneIsHealthGrow;
import ru.cyberbiology.gene.GeneIsMineralGrow;
import ru.cyberbiology.gene.GeneIsMultiCell;
import ru.cyberbiology.gene.GeneLook;
import ru.cyberbiology.gene.GeneMineralToEnergy;
import ru.cyberbiology.gene.GeneMutate;
import ru.cyberbiology.gene.GeneMyHealth;
import ru.cyberbiology.gene.GeneMyLevel;
import ru.cyberbiology.gene.GeneMyMineral;
import ru.cyberbiology.gene.GenePest;
import ru.cyberbiology.gene.GenePhotosynthesis;
import ru.cyberbiology.gene.GeneStep;
import ru.cyberbiology.ui.Painter;
import ru.cyberbiology.util.PerfMeter;
import ru.cyberbiology.util.ProjectProperties;

public class BasicWorld implements World {

    /**
     * Шаг отрисовки. Состояние мира отрисовывается на каждом PAINT_STEP
     * пересчете. Для комфортной визуализации рекомендуется зачение от 5 до 15.
     * Большие значения удобно использовать для проведения оптимизаций кода и
     * длительной работы мира. Можно изменять через меню `Шаг отрисовки`.
     */
    public static final int PAINT_STEP = 1000;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static String mineralsAccumulation;
    public Painter painter;

    public int width;
    public int height;

    public BasicBot[] matrix; // Матрица мира
    public int generation;
    public int population;
    public int organic;
    public int pests;
    public int pestGenes;

    private boolean started;
    private Worker thread;

    private ProjectProperties properties;
    private final Gene[] genes = new Gene[Bot.MIND_SIZE];

    protected BasicWorld(Painter painter) {
        this.painter = painter;
        population = 0;
        generation = 0;
        organic = 0;
        pests = 0;
        pestGenes = 0;
        properties = ProjectProperties.getInstance();
        setupGeneControllers();
        painter.setWorld(this);
    }

    public BasicWorld(Painter painter, int width, int height) {
        this(painter);
        setSize(width, height);
    }

    @Override
    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        matrix = new BasicBot[height * width];
    }

    @Override
    public void setBot(BasicBot bot) {
        matrix[width * bot.posY + bot.posX] = bot;
    }

    @Override
    public void clearBot(int x, int y) {
        matrix[width * y + x] = null;
    }

    @Override
    public void paint() {
        painter.paint();
    }

    @Override
    public Gene[] getGenes() {
        return genes;
    }

    private void setupGeneControllers() {
        genes[23] = new GeneChangeDirection();              // 23 сменить направление
        genes[25] = new GenePhotosynthesis();               // 25 фотосинтез
        genes[27] = new GeneStep();                         // 27 шаг
        genes[28] = new GeneEat();                          // 28 съесть
        genes[30] = new GeneLook();                         // 30 посмотреть
        genes[32] = new GeneCare();                         // 32 делится энергией/минералами поровну
        genes[33] = new GeneCare();
        genes[35] = new GeneGive();                         // 35 отдать
        genes[52] = new GeneGive();
        genes[36] = new GeneFlattenedHorizontally();        // 36 выравнится по горизонтали
        genes[37] = new GeneMyLevel();                      // 37 высота бота
        genes[38] = new GeneMyHealth();                     // 38 здоровье бота
        genes[39] = new GeneMyMineral();                    // 39 минералы бота
        if (properties.getBoolean("EnableMultiCell")) {
            genes[40] = new GeneCreateCell();               // 40 создать клетку многоклеточного
            genes[46] = new GeneIsMultiCell();              // 46 многоклеточный
        }
        genes[41] = new GeneCreateBot();                    // 40 создать клетку одноклеточного
        genes[43] = new GeneFullAroud();                    // 43 окружен ли бот
        genes[44] = new GeneIsHealthGrow();                 // 44 прибавляется ли энергия
        if (properties.getProperty("MineralsAccumulation", "").equals("classic")) {
            genes[45] = new GeneIsMineralGrow();            // 45 прибавляются ли минералы
        }
        // 46 занято
        genes[47] = new GeneMineralToEnergy();              // 47 преобразовать минералы в энерию
        genes[48] = new GeneMutate();                       // 48 мутировать
        genes[49] = new GenePest();                         // 49 паразитировать
        genes[53] = new GeneImitate();                      // 53 имитация, подражание
    }

    /**
     * Сбой в программе генома. Перемещает указатель текущей команды каждого
     * бота случайным образом.
     */
    public void setRandomCmdAdress() {
        if (started()) {
            stop();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BasicBot bot = getBot(x, y);
                if (bot != null && bot.alive == BasicBot.LV_ALIVE) {
                    bot.adr = (byte) (Math.random() * BasicBot.MIND_SIZE);
                }
            }
        }
        start();
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

    /**
     * Создает первого бота в новом мире.
     */
    public final void generateAdam() {
        // ========== 1 ==============
        // бот номер 1 - это уже реальный бот
        BasicBot bot = new BasicBot(this);

        bot.adr = 0;
        bot.posX = width / 2; // координаты бота
        bot.posY = height / 2;
        bot.health = 990; // энергия
        bot.mineral = 0; // минералы
        bot.alive = 3; // отмечаем, что бот живой
        bot.colorRed = 170; // задаем цвет бота
        bot.colorBlue = 170;
        bot.colorGreen = 170;
        bot.direction = RANDOM.nextInt(8); // направление
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
        BasicBot bot;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bot = getBot(x, y);
                if (bot != null && bot.alive == BasicBot.LV_ALIVE) {
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
        BasicBot bot;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bot = getBot(x, y);
                if (bot == null || bot.alive == BasicBot.LV_FREE) {
                    continue;
                }
                if (bot.alive == BasicBot.LV_ORGANIC_HOLD || bot.alive == BasicBot.LV_ORGANIC_SINK) {
                    organic++;
                }
                population++;
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
        boolean isStarted = started;
        if (isStarted) {
            stop();
        }
        // получить список живых ботов
        ArrayList<BasicBot> aliveBots = new ArrayList();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                BasicBot bot = getBot(x, y);
                if (bot != null && bot.alive == BasicBot.LV_ALIVE) {
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
            BasicBot bot = aliveBots.get(rnd.nextInt(aliveBotsCount - 1));

            // Мутации одного гена оказалось недостаточно чтобы
            // встряхнуть заснувший мир
            for (int j = 0; j < mutatesCount; j++) {
                mutagen.exec(bot);
            }
        }
        if (isStarted) {
            start();
        }
    }

    public final boolean started() {
        return thread != null;
    }

    /**
     * Запускает пересчет мира.
     */
    public final void start() {
        if (!this.started()) {
            mineralsAccumulation = properties.getProperty("MineralsAccumulation", "classic");
            thread = new Worker();
            // запуск таймера при запуске потока
            PerfMeter.start();
            thread.start();
        }
    }

    /**
     * Останавливает пересчет мира.
     */
    public final void stop() {
        if (thread == null) {
            return;
        }
        started = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(BasicWorld.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread = null;
    }

    /**
     * Устанавливает алгоритм накопления минералов.
     *
     * @param acc classic|height
     */
    public void setMineralsAccumulation(String acc) {
        if ("classic".equals(acc) || "height".equals(acc)) {
            mineralsAccumulation = acc;
        }
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
        if (y > height / 2) {
            minerals++;
            if (y > height / 6 * 4) {
                minerals++;
            }
            if (y > height / 6 * 5) {
                minerals++;
            }
        }
        return minerals;
    }

    private int getMineralsAccHeight(int y) {
        // сначала определяется вероятность самого факта получения минералов
        if (RANDOM.nextInt(101) <= (int) (y / ((height - 1) * 0.01))) {
            // количество получаемых минералов от 1 на самом верху до 5 в самом низу
            return RANDOM.nextInt(1, 2 + (int) (5 * y / height));
        }
        return 0;
    }

    public final BasicBot getBot(int botX, int botY) {
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
    public BasicBot[] getWorldArray() {
        return matrix;
    }

    /**
     * Выполнить один пересчет мира.
     */
    public void doIteration() {
        doMatrixIteration();
        updateStats();
        paint();
    }

    /**
     * Один пересчет матрицы без обновления статистики и отрисовки.
     */
    private void doMatrixIteration() {
        /**
         * Параллельный стрим работает быстрее предыдущего многопоточного
         * варианта? (см. коммит 0bc13cc)
         * Быстрее, но не намного, процентов на 5. Зато реализация намного проще.
         * Будем надеяться на отсутствие побочных эффектов.
         */
        Arrays.stream(matrix)
                .filter(b -> b != null)
                .parallel()
                .forEach(b -> b.step());

        generation++;
    }

    private class Worker extends Thread {
        @Override
        public void run() {

            started = true; // Флаг работы потока, если установить в false поток заканчивает работу
            
            while (started) {

                doMatrixIteration();

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
}
