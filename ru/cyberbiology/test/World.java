package ru.cyberbiology.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.cyberbiology.test.gene.GeneMutate;
import ru.cyberbiology.test.prototype.IWindow;
import ru.cyberbiology.test.prototype.IWorld;
import ru.cyberbiology.test.prototype.record.IRecordManager;
import ru.cyberbiology.test.record.v0.PlaybackManager;
import ru.cyberbiology.test.record.v0.RecordManager;
import ru.cyberbiology.test.util.ProjectProperties;
import ru.cyberbiology.test.util.PerfMeter;

public class World implements IWorld
{
	public World world;
	public IWindow window;

	PlaybackManager playback;
	IRecordManager recorder;

	public static final int BOTW = 4;
	public static final int BOTH = 4;

    /**
     * Шаг отрисовки.
     * Состояние мира отрисовывается на каждом PAINT_STEP пересчете.
     * Для комфортной визуализации рекомендуется зачение от 5 до 15.
     * Большие значения удобно использовать для проведения оптимизаций
     * кода и длительной работы мира.
     *
     * TODO сделать изменяемым через интерфейс программы
     */
    public static final int PAINT_STEP = 1000;

	public int width;
	public int height;

	public Bot[] matrix; // Матрица мира
	public int generation;
	public int population;
	public int organic;
	public int pests;
    public int pestGenes;

	boolean started;
	Worker thread;
	protected World(IWindow win)
	{
		world = this;
		window = win;
		population = 0;
		generation = 0;
		organic = 0;
		pests = 0;
        pestGenes = 0;
		recorder = new RecordManager(this);
	}
	public World(IWindow win, int width, int height)
	{
		this(win);
		this.setSize(width, height);
	}

	@Override
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.matrix = new Bot[height * width];
	}

	@Override
	public void setBot(Bot bot)
	{
		matrix[width * bot.y + bot.x] = bot;
	}

    @Override
    public void clearBot(int x, int y) {
        matrix[width * y + x] = null;
    }

	public void paint()
	{
		window.paint();
	}
	@Override
	public ProjectProperties getProperties()
	{
		return window.getProperties();
	}

    /**
     * Сбой в программе генома.
     * Перемещает указатель текущей команды каждого бота случайным образом.
     */
    void jumpBotsCmdAdress() {
        if (started()) stop();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Bot bot = getBot(x, y);
                if (bot != null) {
                    if (bot.alive == bot.LV_ALIVE) {
                        bot.adr = (byte)(Math.random() * Bot.MIND_SIZE);
                    }
                }
            }
        }
        start();
    }

    /**
     * Многопоточный класс цикла пересчета матрицы.
     * Каждый поток отвечает за свой участок матрицы.
     * @param from Индекс бота в матрице с которого поток начинает пересчет
     * @param to Индекс бота в матрице до которого поток производит пересчет
     */
    class WorldWorker extends Thread {
        int from;
        int to;

        public WorldWorker(int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            Bot bot;
            for (int i=from; i<to; i++) {
                bot = matrix[i];
                if (bot != null) {
                    bot.step(); // выполняем шаг бота
                    if (recorder.isRecording()) {
                        // вызываем обработчика записи бота
                        recorder.writeBot(bot, bot.x, bot.y);
                    }
                }
            }
        }
    }


	class Worker extends Thread
	{
        int part = matrix.length / 2;
        WorldWorker w1;
        WorldWorker w2;

        @Override
        public void run()
		{
			started = true;// Флаг работы потока, если установить в false поток
							// заканчивает работу
			while (started)
			{

				boolean rec = recorder.isRecording(); // запоминаем флаг
														// "записывать" на
														// полную итерацию кадра
				if (rec)// вызываем обработчика "старт кадра"
					recorder.startFrame();
				// обновляем матрицу

                // первая простая попытка реализовать многопоточную обработку матрицы.
                // Пока что лучший результат дают два потока. На моей машине
                // прибавка составляет 20-30 пересчетов мира в секунду (WIPS)
                // при размере окна 1024x768, полностью заселенной матрице и PAINT_STEP = 1000
                w1 = new WorldWorker(0, part);
                w2 = new WorldWorker(part, matrix.length);
                w1.start();
                w2.start();
                try {
                //w1.join();
                // вроде бы достаточно подождать только второй поток, но это не точно
                w2.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
                }

				if (rec)// вызываем обработчика "конец кадра"
					recorder.stopFrame();
				generation = generation + 1;
				if (generation % PAINT_STEP == 0)
				{ // отрисовка на экран через каждые ... шагов
                    // замеряем время пересчета 10 итераций без учета отрисовки
                    PerfMeter.tick();
					paint(); // отображаем текущее состояние симуляции на экран
				}
				// sleep(); // пауза между ходами, если надо уменьшить скорость
			}
			paint();// если запаузили рисуем актуальную картинку
			started = false;// Закончили работу
		}
	}

	public void generateAdam()
	{
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
		for (int i = 0; i < 64; i++)
		{ // заполняем геном командой 25 - фотосинтез
			bot.mind[i] = 25;
		}

		setBot(bot); // даём ссылку на бота в массиве world[]

		return;
	}
	public void restoreLinks()
	{
        Bot bot;
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
                bot = getBot(x, y);
				if (bot != null)
				{
					if (bot.alive == bot.LV_ALIVE)
					{
						if(bot.mprevX>-1 && bot.mprevY>-1)
						{
							bot.mprev = getBot(bot.mprevX, bot.mprevY);
						}
						if(bot.mnextX>-1 && bot.mnextY>-1)
						{
							bot.mnext = getBot(bot.mnextX, bot.mnextY);
						}
					}
				}
			}
		}
	}

    /**
     * Восстанавливает статистику мира при загрузке.
     * Не восстанавливает generation (на текущий момент generation
     * не сохраняется в RecordManager)
     */
    @Override
    public void restoreStats() {
        Bot bot;
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
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
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                Bot bot = getBot(x, y);
                if (bot != null && bot.alive == bot.LV_ALIVE)
                    aliveBots.add(bot);
            }
        }

        // размер группы мутирующих ботов 10% от живых
        int aliveBotsCount = aliveBots.size();
        int mutantsCount = (int) Math.round(aliveBotsCount * percent/100);
        System.out.println("Mutating " + percent + "% of alive bots (" + mutantsCount + " of " + aliveBotsCount + ")");

        // мутация
        Random rnd = new Random();
        GeneMutate mutagen = new GeneMutate();
        for (int i=0; i<mutantsCount; i++) {
            Bot bot = aliveBots.get(rnd.nextInt(aliveBotsCount-1));
            //System.out.println("Mutating " + bot);

            // Мутации одного гена оказалось недостаточно чтобы
            // встряхнуть заснувший мир
            for (int j=0; j<mutatesCount; j++) {
                mutagen.onGene(bot);
            }
        }
    }

    public boolean started()
	{
		return this.thread != null;
	}

	public void start()
	{
		if (!this.started())
		{
			this.thread = new Worker();
            // запуск таймера при запуске потока
            PerfMeter.start();
			this.thread.start();
		}
	}

	public void stop()
	{
		started = false;
		this.thread = null;
	}

	public boolean isRecording()
	{
		return this.recorder.isRecording();
	}

	public void startRecording()
	{
		this.recorder.startRecording();
	}

	public boolean stopRecording()
	{
		return this.recorder.stopRecording();
	}

	public Bot getBot(int botX, int botY)
	{
		return this.matrix[width * botY + botX];
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	public boolean haveRecord()
	{
		return this.recorder.haveRecord();
	}
	/*
	public void deleteRecord()
	{
		this.recorder.deleteRecord();
	}*/

	public void makeSnapShot()
	{
		this.recorder.makeSnapShot();
	}
	@Override
	public Bot[] getWorldArray()
	{
		return this.matrix;
	}
	public void openFile(File file)
	{
		playback = new PlaybackManager(this, file);
	}
}
