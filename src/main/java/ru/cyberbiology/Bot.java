package ru.cyberbiology;

import java.util.Random;
import ru.cyberbiology.gene.GeneCareAbsolutelyDirection;
import ru.cyberbiology.gene.GeneCareRelativeDirection;
import ru.cyberbiology.gene.GeneChangeDirectionAbsolutely;
import ru.cyberbiology.gene.GeneChangeDirectionRelative;
import ru.cyberbiology.gene.GeneEatAbsoluteDirection;
import ru.cyberbiology.gene.GeneEatRelativeDirection;
import ru.cyberbiology.gene.GeneFlattenedHorizontally;
import ru.cyberbiology.gene.GeneFullAroud;
import ru.cyberbiology.gene.GeneGiveAbsolutelyDirection;
import ru.cyberbiology.gene.GeneGiveRelativeDirection;
import ru.cyberbiology.gene.GeneImitate;
import ru.cyberbiology.gene.GeneIsHealthGrow;
import ru.cyberbiology.gene.GeneIsMineralGrow;
import ru.cyberbiology.gene.GeneIsMultiCell;
import ru.cyberbiology.gene.GeneLookRelativeDirection;
import ru.cyberbiology.gene.GeneMineralToEnergy;
import ru.cyberbiology.gene.GeneMutate;
import ru.cyberbiology.gene.GeneMyHealth;
import ru.cyberbiology.gene.GeneMyLevel;
import ru.cyberbiology.gene.GeneMyMineral;
import ru.cyberbiology.gene.GenePest;
import ru.cyberbiology.gene.GenePhotosynthesis;
import ru.cyberbiology.gene.GeneStepInAbsolutelyDirection;
import ru.cyberbiology.gene.GeneStepInRelativeDirection;
import ru.cyberbiology.gene.GeneСreateBot;
import ru.cyberbiology.gene.GeneСreateCell;
import ru.cyberbiology.prototype.IBot;
import ru.cyberbiology.prototype.IWorld;
import ru.cyberbiology.prototype.gene.IBotGeneController;

public class Bot implements IBot {

    public int adr;
    public int x;
    public int y;
    public int health;
    public int mineral;
    public int alive;
    public int c_red;
    public int c_green;
    public int c_blue;
    public int direction;
    public int pest;
    public Bot mprev;
    public Bot mnext;

    // максимальное количество генов паразитирования в геноме
    private final int MAX_PEST_GENES = 32;

    static IBotGeneController[] geneController = new IBotGeneController[64];

    static {
        geneController[23] = new GeneChangeDirectionRelative(); //23 сменить направление относительно
        geneController[24] = new GeneChangeDirectionAbsolutely(); //24 сменить направление абсолютно
        geneController[25] = new GenePhotosynthesis();//25 фотосинтез
        geneController[26] = new GeneStepInRelativeDirection();//26 шаг   в относительном направлении
        geneController[27] = new GeneStepInAbsolutelyDirection();//27 шаг   в абсолютном направлении
        geneController[28] = new GeneEatRelativeDirection();//28 шаг  съесть в относительном направлении
        geneController[29] = new GeneEatAbsoluteDirection();//29 шаг  съесть в абсолютном направлении
        geneController[30] = new GeneLookRelativeDirection();//30 шаг  посмотреть в относительном направлении

        geneController[32] = new GeneCareRelativeDirection();//32 шаг делится   в относительном напралении
        geneController[42] = geneController[32];

        geneController[33] = new GeneCareAbsolutelyDirection();//33 шаг делится   в абсолютном напралении
        geneController[50] = geneController[33];

        geneController[34] = new GeneGiveRelativeDirection();//34 шаг отдать   в относительном напралении
        geneController[51] = geneController[34];

        geneController[35] = new GeneGiveAbsolutelyDirection();//35 шаг отдать   в абсолютном напралении
        geneController[52] = geneController[35];

        geneController[36] = new GeneFlattenedHorizontally();//36 выравнится по горизонтали
        geneController[37] = new GeneMyLevel();//37 высота бота
        geneController[38] = new GeneMyHealth();//38 здоровье бота
        geneController[39] = new GeneMyMineral();//39 минералы бота
        geneController[40] = new GeneСreateCell();//40 создать клетку многоклеточного
        geneController[41] = new GeneСreateBot();//40 создать клетку одноклеточного
        //42 занято
        geneController[43] = new GeneFullAroud();//43  окружен ли бот
        geneController[44] = new GeneIsHealthGrow();//44  окружен ли бот
        geneController[45] = new GeneIsMineralGrow();//45  прибавляются ли минералы
        geneController[46] = new GeneIsMultiCell();//46  многоклеточный
        geneController[47] = new GeneMineralToEnergy();//47  преобразовать минералы в энерию
        geneController[48] = new GeneMutate();//48  мутировать
        geneController[49] = new GenePest();//49  паразитировать
        geneController[50] = new GeneImitate(); // 50 имитация, подражание
    }

    public static final int MIND_SIZE = 64;     // объем генома
    public byte[] mind = new byte[MIND_SIZE];   // геном бота содержит 64 команды

    // Далее следуют константы состояния бота, которое отмеченно для каждого бота в массиве bots[].
    /**
     * Место свободно, здесь может быть размещен новый бот.
     */
    public int LV_FREE = 0;

    /**
     * Бот погиб и представляет из себя органику в подвешенном состоянии.
     */
    public int LV_ORGANIC_HOLD = 1;

    /**
     * Тонущая ораника. Органика тонет, пока не встретит препятствие, после чего
     * остается в подвешенном состоянии (LV_ORGANIC_HOLD).
     */
    public int LV_ORGANIC_SINK = 2;

    /**
     * Живой бот.
     */
    public int LV_ALIVE = 3;

    /**
     * Поля нужны для сериализации ботов координаты соседних клеток
     * многоклеточного.
     */
    public int mprevX;
    public int mprevY;
    public int mnextX;
    public int mnextY;

    World world;

    public Bot(World world) {
        this.world = world;
        direction = 2;
        health = 5;
        alive = LV_ALIVE;
    }

    /**
     * Главная функция жизнедеятельности бота. Здесь выполняется код его
     * мозга-генома
     */
    public void step() {
        /*
    	if(alive == LV_ORGANIC_SINK || alive == LV_ORGANIC_HOLD)
    	{
    		botMove(this, 5, 1);
    	}*/
        if (alive == LV_FREE || alive == LV_ORGANIC_HOLD || alive == LV_ORGANIC_SINK) {
            botMove(this, 5, 1);
            return;     //Это труп - выходим!
        }

        IBotGeneController cont;

        for (int cyc = 0; cyc < MIND_SIZE / 4; cyc++) {//15
            int command = mind[adr];    // текущая команда

            // Получаем обработчика команды
            cont = geneController[command];
            if (cont != null) // если обработчик такой команды назначен
            {
                if (cont.onGene(this)) // передаем ему управление
                {
                    break; // если обрабочик говорит, что он последний - завершаем цикл?
                }
            } else {    //если ни с одной команд не совпало значит безусловный переход прибавляем к указателю текущей команды значение команды
                botIncCommandAddress(this, command);
                break;
            }
            if (alive != LV_ALIVE || health <= 0) {
                break;
            }
        }

        /**
         * Выход из функции и передача управления следующему боту. Но перед
         * выходом нужно проверить, входит ли бот в многоклеточную цепочку и
         * если да, то нужно распределить энергию и минералы с соседями, а также
         * проверить количество накопленой энергии - возможно пришло время
         * подохнуть или породить потомка.
         */
        if (alive == LV_ALIVE) {
            int a = isMulti(this);
            // распределяем энергию  минералы по многоклеточному организму
            // возможны три варианта, бот находится внутри цепочки
            // бот имеет предыдущего бота в цепочке и не имеет следующего
            // бот имеет следующего бота в цепочке и не имеет предыдущего
            if (a == 3) {   // бот находится внутри цепочки
                Bot pb = mprev; // ссылка на предыдущего бота в цепочке
                Bot nb = mnext; // ссылка на следующего бота в цепочке
                // делим минералы
                int m = mineral + nb.mineral + pb.mineral; // общая сумма минералов
                //распределяем минералы между всеми тремя ботами
                m = m / 3;
                mineral = m;
                nb.mineral = m;
                pb.mineral = m;
                // делим энергию
                // проверим, являются ли следующий и предыдущий боты в цепочке крайними
                // если они не являются крайними, то распределяем энергию поровну
                // связанно это с тем, что в крайних ботах в цепочке должно быть больше энергии
                // что бы они плодили новых ботов и удлиняли цепочку
                int apb = isMulti(pb);
                int anb = isMulti(nb);
                if ((anb == 3) && (apb == 3)) { // если следующий и предыдущий боты не являются крайними
                    // то распределяем энергию поровну
                    int h = health + nb.health + pb.health;
                    h = h / 3;
                    health = h;
                    nb.health = h;
                    pb.health = h;
                }
            }
            // бот является крайним в цепочке и имеет предыдкщего бота
            if (a == 1) {
                Bot pb = mprev; // ссылка на предыдущего бота
                int apb = isMulti(pb);  // проверим, является ли предыдущий бот крайним в цепочке
                if (apb == 3) { // если нет, то распределяем энергию в пользу текущего бота
                    // так как он крайний и ему нужна энергия для роста цепочки
                    int h = health + pb.health;
                    h = h / 4;
                    health = h * 3;
                    pb.health = h;
                }
            }
            // бот является крайним в цепочке и имеет следующего бота
            if (a == 2) {
                Bot nb = mnext; // ссылка на следующего бота
                int anb = isMulti(nb);  // проверим, является ли следующий бот крайним в цепочке
                if (anb == 3) {         // если нет, то распределяем энергию в пользу текущего бота
                    // так как он крайний и ему нужна энергия для роста цепочки
                    int h = health + nb.health;
                    h = h / 4;
                    health = h * 3;
                    nb.health = h;
                }
            }

            // Проверим уровень энергии у бота, возможно пришла пора помереть или родить.
            // Вопрос стоит ли так делать, родждение прописано в генных командах
            // Sergey Sokolov: ответ - стоит, ибо то, что прописано в генах это спонтанные роды,
            // а тут естественные.
            if (health > 999 || (health > 399 && pest > 0)) { // паразиту достаточно 400 энергии для размножения
                if ((a == 1) || (a == 2)) {
                    botMulti(this); // если бот был крайним в цепочке, то его потомок входит в состав цепочки
                } else {
                    botDouble(this); // если бот был свободным или находился внутри цепочки
                    // то его потомок рождается свободным
                }
            }

            // трата энергии за ход
            if (pest > 0) {
                health = health - 20; // паразит
            } else {
                health = health - 3; // здоровый бот
            }

            if (health < 1) {       // если энергии стало меньше 1
                bot2Organic(this);  // то время умирать, превращаясь в огранику
                return;             // и передаем управление к следующему боту
            }
            // если бот находится на глубине ниже 48 уровня
            // то он автоматом накапливает минералы, но не более 999
            if (y > world.height / 2) {
                mineral = mineral + 1;
                if (y > world.height / 6 * 4) {
                    mineral = mineral + 1;
                }
                if (y > world.height / 6 * 5) {
                    mineral = mineral + 1;
                }
                if (mineral > 999) {
                    mineral = 999;
                }
            }
        }
    }

    @Override
    public IWorld getWorld() {
        return this.world;
    }

    /**
     * Вычисляет Х-координату рядом с био по относительному направлению.
     *
     * @param bot
     * @param n направление
     * @return X - координата по абсолютному направлению
     */
    int xFromVektorR(Bot bot, int n) {
        int xt = x + Const.INCREMENT_X[direction + n];
        if (xt >= world.width) {
            xt = 0;
        }
        if (xt < 0) {
            xt = world.width - 1;
        }
        return xt;
    }

    /**
     * Вычисляет Х-координату рядом с био по абсолютному направлению.
     *
     * @param bot
     * @param n
     * @return X - координата по абсолютному направлению
     */
    int xFromVektorA(Bot bot, int n) {
        return xFromVektorR(bot, 0);
    }

    /**
     * Вычисляет Y-координату рядом с био по относительному направлению.
     *
     * @param bot
     * @param n направление
     * @return Y координата по относительному направлению
     */
    int yFromVektorR(Bot bot, int n) {
        int yt = y + Const.INCREMENT_Y[direction + n];
        return yt;
    }

    /**
     * Вычисляет Y-координату рядом с био по абсолютному направлению.
     *
     * @param bot
     * @param n направление
     * @return Y координата по абсолютному направлению
     */
    int yFromVektorA(Bot bot, int n) {
        return yFromVektorR(bot, 0);
    }

    /**
     * Окружен ли бот?.
     *
     * @param bot
     * @return 1-окружен 2-нет
     */
    int fullAroud(Bot bot) {
        for (int i = 0; i < 8; i++) {
            int xt = xFromVektorR(bot, i);
            int yt = yFromVektorR(bot, i);
            if ((yt >= 0) && (yt < world.height)) {
                if (world.getBot(xt, yt) == null) {
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * Ищет свободные ячейки вокруг бота кругу через низ.
     *
     * @param bot
     * @return номер направление или 8 , если свободных нет
     */
    int findEmptyDirection(Bot bot) {
        for (int i = 0; i < 8; i++) {
            int xt = xFromVektorR(bot, i);
            int yt = yFromVektorR(bot, i);
            if ((yt >= 0) && (yt < world.height)) {
                if (world.getBot(xt, yt) == null) {
                    return i;
                }
            }
        }
        // свободных направлений нет
        return 8;
    }

    /**
     * Получает параметр для текущей команды.
     *
     * @param bot
     * @return возвращает число из днк, следующее за выполняемой командой
     */
    int botGetParam(Bot bot) {
        int paramadr = bot.adr + 1;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        return bot.mind[paramadr]; // возвращает число, следующее за выполняемой командой
    }

    /**
     * Увеличивает адрес команды.
     *
     * @param bot
     * @param a насколько прибавить адрес
     */
    void botIncCommandAddress(Bot bot, int a) {
        int paramadr = bot.adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        bot.adr = paramadr;
    }

    /**
     * Косвенно увеличивает адрес команды.
     *
     * @param bot
     * @param a смещение до команды, которая станет смещением
     */
    void botIndirectIncCmdAddress(Bot bot, int a) {
        int paramadr = bot.adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        int bias = bot.mind[paramadr];
        botIncCommandAddress(bot, bias);
    }

    /**
     * Превращение бота в органику.
     *
     * @param bot
     */
    void bot2Organic(Bot bot) {
        bot.alive = LV_ORGANIC_SINK;    // отметим в массиве bots[], что бот органика
        Bot pbot = bot.mprev;
        Bot nbot = bot.mnext;
        if (pbot != null) {
            pbot.mnext = null;
        } // удаление бота из многоклеточной цепочки
        if (nbot != null) {
            nbot.mprev = null;
        }
        bot.mprev = null;
        bot.mnext = null;
// todo: https://github.com/xlam/CyberBiology/issues/2
//        this.world.organic++;
//        this.world.population--;
//        if (bot.pest > 0) {
//            this.world.pests--;
//            this.world.pestGenes -= bot.pest;
//        }
    }

    /**
     * Нахожусь ли я в многоклеточной цепочке?.
     *
     * @param bot
     * @return 0 - нет, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
     */
    int isMulti(Bot bot) {
        int a = 0;
        if (bot.mprev != null) {
            a = 1;
        }
        if (bot.mnext != null) {
            a = a + 2;
        }
        return a;
    }

    /**
     * Перемещает бота в нужную точку без проверок.
     *
     * @param bot
     * @param xt новые координаты x
     * @param yt новые координаты y
     */
    void moveBot(Bot bot, int xt, int yt) {
        world.matrix[world.width * yt + xt] = bot;
        world.clearBot(bot.x, bot.y);
        bot.x = xt;
        bot.y = yt;
    }

    /**
     * Удаление бота.
     *
     * @param bot
     */
    public void deleteBot(Bot bot) {
        Bot pbot = bot.mprev;
        Bot nbot = bot.mnext;
        if (pbot != null) {
            pbot.mnext = null;
        } // удаление бота из многоклеточной цепочки
        if (nbot != null) {
            nbot.mprev = null;
        }
        bot.mprev = null;
        bot.mnext = null;
// todo: https://github.com/xlam/CyberBiology/issues/2
//        if (bot.alive == bot.LV_ORGANIC_HOLD || bot.alive == bot.LV_ORGANIC_SINK) {
//            this.world.organic--;
//        }
//        if (bot.alive == bot.LV_ALIVE) {
//            this.world.population--;
//            if (bot.pest > 0) {
//                this.world.pests--;
//                this.world.pestGenes -= bot.pest;
//            }
//        }
        world.clearBot(bot.x, bot.y); // удаление бота с карты
    }

    /**
     * Фотосинтез. Этой командой забит геном первого бота бот получает энергию
     * солнца в зависимости от глубины и количества минералов, накопленных
     * ботом.
     *
     * @param bot
     */
    public void botEatSun(Bot bot) {
        int t;
        if (bot.mineral < 100) {
            t = 0;
        } else if (bot.mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int a = 0;
        if (bot.mprev != null) {
            a = a + 4;
        }
        if (bot.mnext != null) {
            a = a + 4;
        }
        int hlt = a + 1 * (11 - (15 * bot.y / world.height) + t); // формула вычисления энергии
        if (hlt > 0) {
            bot.health = bot.health + hlt;  // прибавляем полученную энергия к энергии бота
            goGreen(bot, hlt);              // бот от этого зеленеет
        }
    }

    /**
     * Преобразование минералов в энергию.
     *
     * @param bot
     */
    public void botMineral2Energy(Bot bot) {
        if (bot.mineral > 100) {   // максимальное количество минералов, которые можно преобразовать в энергию = 100
            bot.mineral = bot.mineral - 100;
            bot.health = bot.health + 400; // 1 минерал = 4 энергии
            goBlue(bot, 100);  // бот от этого синеет
        } else {  // если минералов меньше 100, то все минералы переходят в энергию
            goBlue(bot, bot.mineral);
            bot.health = bot.health + 4 * bot.mineral;
            bot.mineral = 0;
        }
    }

    /**
     * Перемещение бота.
     *
     * @param bot ссылка на бота,
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return
     */
    public int botMove(Bot bot, int direction, int ra) { // ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе   2-пусто  3-стена  4-органика 5-бот 6-родня
        int xt;
        int yt;
        if (ra == 0) {          // вычисляем координату клетки, куда перемещается бот (относительное направление)
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {                // вычисляем координату клетки, куда перемещается бот (абсолютное направление)
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там ... стена
            return 3;                       // то возвращаем 3
        }
        Bot bot2 = world.getBot(xt, yt);
        if (bot2 == null) {  // если клетка была пустая,
            moveBot(bot, xt, yt);    // то перемещаем бота
            return 2;                       // и функция возвращает 2
        }
        // осталось 2 варианта: ограника или бот
        if (bot2.alive <= LV_ORGANIC_SINK) { // если на клетке находится органика
            return 4;                       // то возвращаем 4
        }
        if (isRelative(bot, bot2) == 1) {  // если на клетке родня
            return 6;                      // то возвращаем 6
        }
        return 5;                         // остался только один вариант - на клетке какой-то бот возвращаем 5
    }

    /**
     * Скушать другого бота или органику.
     *
     * @param bot входе ссылка на бота
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return пусто - 2 стена - 3 органик - 4 бот - 5
     */
    int botEat(Bot bot, int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе пусто - 2  стена - 3  органик - 4  бот - 5
        bot.health = bot.health - 4; // бот теряет на этом 4 энергии в независимости от результата
        int xt;
        int yt;
        if (ra == 0) {  // вычисляем координату клетки, с которой хочет скушать бот (относительное направление)
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {        // вычисляем координату клетки, с которой хочет скушать бот (абсолютное направление)
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot2 = world.getBot(xt, yt);
        if (bot2 == null) {  // если клетка пустая возвращаем 2
            return 2;
        } // осталось 2 варианта: ограника или бот
        else if (bot2.alive <= LV_ORGANIC_SINK) {   // если там оказалась органика
            deleteBot(bot2);                        // то удаляем её из списков
            bot.health = bot.health + 100;  //здоровье увеличилось на 100
            goRed(this, 100);               // бот покраснел
            return 4;                       // возвращаем 4
        }
        // дошли до сюда, значит впереди живой бот
        int min0 = bot.mineral;     // определим количество минералов у бота
        int min1 = bot2.mineral;    // определим количество минералов у потенциального обеда
        int hl = bot2.health;       // определим энергию у потенциального обеда
        // если у бота минералов больше
        if (min0 >= min1) {
            // количество минералов у бота уменьшается на количество минералов у жертвы,
            // типа, стесал свои зубы о панцирь жертвы
            bot.mineral = min0 - min1;
            deleteBot(bot2);          // удаляем жертву из списков
            int cl = 100 + (hl / 2);           // количество энергии у бота прибавляется на 100+(половина от энергии жертвы)
            bot.health = bot.health + cl;
            goRed(this, cl);                    // бот краснеет
            return 5;                              // возвращаем 5
        }
        // если у жертвы минералов больше
        bot.mineral = 0; // то бот израсходовал все свои минералы на преодоление защиты
        min1 = min1 - min0;       // у жертвы количество минералов тоже уменьшилось
        bot2.mineral = min1 - min0;       // перезаписали минералы жертве (ЗАПЛАТКА!!!)
        // если здоровья в 2 раза больше, чем минералов у жертвы,
        // то здоровьем проламываем минералы
        if (bot.health >= 2 * min1) {
            deleteBot(bot2);         // удаляем жертву из списков
            int cl = 100 + (hl / 2) - 2 * min1; // вычисляем, сколько энергии смог получить бот
            bot.health = bot.health + cl;
            if (cl < 0) {
                cl = 0;
            } // ЗАПЛАТКА!!! - энергия не должна быть отрицательной

            goRed(this, cl);    // бот краснеет
            return 5;           // возвращаем 5
        }
        // если здоровья меньше, чем (минералов у жертвы)*2, то бот погибает от жертвы
        bot2.mineral = min1 - (bot.health / 2);  // у жертвы минералы истраченны
        bot.health = 0; // здоровье уходит в ноль
        return 5;       // возвращаем 5
    }

    /**
     * Посмотреть.
     *
     * @param bot ссылка на бота
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return пусто - 2 стена - 3 органик - 4 бот - 5 родня - 6
     */
    int botSeeBots(Bot bot, int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе  пусто - 2  стена - 3  органик - 4  бот - 5  родня - 6
        int xt;
        int yt;
        if (ra == 0) {  // выясняем, есть ли что в этом  направлении (относительном)
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {       // выясняем, есть ли что в этом  направлении (абсолютном)
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot2 = world.getBot(xt, yt);
        if (bot2 == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot2.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        } else if (isRelative(bot, bot2) == 1) {  // если родня, то возвращаем 6
            return 6;
        } else { // если какой-то бот, то возвращаем 5
            return 5;
        }
    }

    /**
     * Атака на геном соседа, меняем случайный ген случайным образом.
     *
     * @param bot
     */
    void botGenAttack(Bot bot) {   // вычисляем кто у нас перед ботом (используется только относительное направление вперед)
        int xt = xFromVektorR(bot, 0);
        int yt = yFromVektorR(bot, 0);
        Bot bot2 = world.getBot(xt, yt);
        if ((yt >= 0) && (yt < world.height) && (bot2 != null)) {
            if (bot2.alive == LV_ALIVE) { // если там живой бот
                bot.health = bot.health - 10; // то атакуюий бот теряет на атаку 10 энергии
                if (bot.health > 0) {
                    bot2.modifyMind();
                }
            }
        }
    }

    /**
     * Поделится. Если у бота больше энергии или минералов, чем у соседа в
     * заданном направлении то бот делится излишками.
     *
     * @param bot ссылка на бота
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return
     */
    int botCare(Bot bot, int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) {  // определяем координаты для относительного направления
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {        // определяем координаты для абсолютного направления
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot2 = world.getBot(xt, yt);
        if (bot2 == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot2.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        }
        // если мы здесь, то в данном направлении живой
        int hlt0 = bot.health;      // определим количество энергии и минералов
        int hlt1 = bot2.health;     // у бота и его соседа
        int min0 = bot.mineral;
        int min1 = bot2.mineral;
        if (hlt0 > hlt1) {                  // если у бота больше энергии, чем у соседа
            int hlt = (hlt0 - hlt1) / 2;    // то распределяем энергию поровну
            bot.health = bot.health - hlt;
            bot2.health = bot2.health + hlt;
        }
        if (min0 > min1) {                  // если у бота больше минералов, чем у соседа
            int min = (min0 - min1) / 2;    // то распределяем их поровну
            bot.mineral = bot.mineral - min;
            bot2.mineral = bot2.mineral + min;
        }
        return 5;
    }

    /**
     * Отдать безвозместно, то есть даром.
     *
     * @param bot ссылка на бота
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return стена - 2 пусто - 3 органика - 4 удачно - 5
     */
    int botGive(Bot bot, int direction, int ra) // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
    {   // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) {  // определяем координаты для относительного направления
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {        // определяем координаты для абсолютного направления
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot2 = world.getBot(xt, yt);
        if (bot2 == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot2.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        }
        // если мы здесь, то в данном направлении живой
        int hlt0 = bot.health;  // бот отдает четверть своей энергии
        int hlt = hlt0 / 4;
        bot.health = hlt0 - hlt;
        bot2.health = bot2.health + hlt;

        int min0 = bot.mineral; // бот отдает четверть своих минералов
        if (min0 > 3) {         // только если их у него не меньше 4
            int min = min0 / 4;
            bot.mineral = min0 - min;
            bot2.mineral = bot2.mineral + min;
            if (bot2.mineral > 999) {
                bot2.mineral = 999;
            }
        }
        return 5;
    }

    /**
     * Рождение нового бота делением.
     */
    private void botDouble(Bot bot) {
        bot.health = bot.health - 150;  // бот затрачивает 150 единиц энергии на создание копии
        if (bot.health <= 0) {
            return; // если у него было меньше 150, то пора помирать
        }
        int n = findEmptyDirection(bot);    // проверим, окружен ли бот
        if (n == 8) {                       // если бот окружен, то он в муках погибает
            bot.health = 0;
            return;
        }

        Bot newbot = new Bot(this.world);

        int xt = xFromVektorR(bot, n);   // координаты X и Y
        int yt = yFromVektorR(bot, n);

        System.arraycopy(bot.mind, 0, newbot.mind, 0, MIND_SIZE);

        // информация о паразитных генах сохраняется в новом боте
        newbot.pest = bot.pest;

        if (Math.random() < 0.25) { // в одном случае из четырех случайным образом меняем один случайный байт в геноме
            newbot.modifyMind();
        }

        // Полноценный потомок-паразит не может получать энергию от
        // фотосинтеза или переработки минералов. Эти команды заменяются
        // командой паразитирования. Тем не менее, в процессе эволюции, в
        // геноме снова могут появиться фотосинтез и переработка.
        // TODO это решение требует перепроектирования!
        if (newbot.pest > 0) {
            for (int i = 0; i < MIND_SIZE; i++) {
                if (newbot.mind[i] == 25 || newbot.mind[i] == 47) {
                    newbot.mind[i] = 49;
                    newbot.pest++;
                }
            }
        }

        newbot.adr = 0; // указатель текущей команды в новорожденном устанавливается в 0
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = bot.health / 2;   // забирается половина здоровья у предка
        bot.health = bot.health / 2;
        newbot.mineral = bot.mineral / 2; // забирается половина минералов у предка
        bot.mineral = bot.mineral / 2;

        newbot.alive = LV_ALIVE;        // отмечаем, что бот живой

        newbot.c_red = bot.c_red;       // цвет такой же, как у предка
        newbot.c_green = bot.c_green;   // цвет такой же, как у предка
        newbot.c_blue = bot.c_blue;     // цвет такой же, как у предка

        newbot.direction = (int) (Math.random() * 8);   // направление, куда повернут новорожденный, генерируется случайно

        world.setBot(newbot);    // отмечаем нового бота в массиве matrix
// todo: https://github.com/xlam/CyberBiology/issues/2
//        this.world.population++;
    }

    /**
     * Рождение новой клетки многоклеточного.
     */
    private void botMulti(Bot bot) {
        Bot pbot = bot.mprev;    // ссылки на предыдущего и следущего в многоклеточной цепочке
        Bot nbot = bot.mnext;
        // если обе ссылки больше 0, то бот уже внутри цепочки
        if ((pbot != null) && (nbot != null)) {
            return; // поэтому выходим без создания нового бота
        }
        bot.health = bot.health - 150; // бот затрачивает 150 единиц энергии на создание копии
        if (bot.health <= 0) {
            return; // если у него было меньше 150, то пора помирать
        }
        int n = findEmptyDirection(bot); // проверим, окружен ли бот

        if (n == 8) {  // если бот окружен, то он в муках погибает
            bot.health = 0;
            return;
        }
        Bot newbot = new Bot(this.world);
        newbot.pest = bot.pest;

        int xt = xFromVektorR(bot, n);   // координаты X и Y
        int yt = yFromVektorR(bot, n);

        System.arraycopy(bot.mind, 0, newbot.mind, 0, MIND_SIZE);   // копируем геном в нового бота

        if (Math.random() < 0.25) { // в одном случае из четырех случайным образом меняем один случайный байт в геноме
            newbot.modifyMind();
        }

        newbot.adr = 0;     // указатель текущей команды в новорожденном устанавливается в 0
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = bot.health / 2;   // забирается половина здоровья у предка
        bot.health = bot.health / 2;
        newbot.mineral = bot.mineral / 2; // забирается половина минералов у предка
        bot.mineral = bot.mineral / 2;

        newbot.alive = LV_ALIVE;        // отмечаем, что бот живой

        newbot.c_red = bot.c_red;       // цвет такой же, как у предка
        newbot.c_green = bot.c_green;   // цвет такой же, как у предка
        newbot.c_blue = bot.c_blue;     // цвет такой же, как у предка

        newbot.direction = (int) (Math.random() * 8);   // направление, куда повернут новорожденный, генерируется случайно

        world.setBot(newbot);    // отмечаем нового бота в массиве matrix
// todo: https://github.com/xlam/CyberBiology/issues/2
//        this.world.population++;

        if (nbot == null) { // если у бота-предка ссылка на следующего бота в многоклеточной цепочке пуста
            bot.mnext = newbot;     // то вставляем туда новорожденного бота
            newbot.mprev = bot;     // у новорожденного ссылка на предыдущего указывает на бота-предка
            newbot.mnext = null;    // ссылка на следующего пуста, новорожденный бот является крайним в цепочке
        } else {            // если у бота-предка ссылка на предыдущего бота в многоклеточной цепочке пуста
            bot.mprev = newbot;     // то вставляем туда новорожденного бота
            newbot.mnext = bot;     // у новорожденного ссылка на следующего указывает на бота-предка
            newbot.mprev = null;    // ссылка на предыдущего пуста, новорожденный бот является крайним в цепочке
        }
    }

    /**
     * Меняет один ген случайным образом.
     *
     * @return byte[3] массив с информацией об измененном гене [0] - адрес
     * измененной команды [1] - старая команда [2] - новая команда
     */
    @Override
    public byte[] modifyMind() {

        byte[] modified = new byte[3];

        // случайным образом меняется один ген
        byte ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
        byte mc = (byte) (Math.random() * MIND_SIZE);  // 0..63

        modified[0] = ma;
        modified[1] = mind[ma];

        // корректируем счетчик паразитных генов
        // TODO переделать механизм события смены гена,
        // т.к. подсчет генов это не забота данного метода
        if (mind[ma] == 49) {
            pest--;
        }
        if (mc == 49) {
            pest++;
        }

        setMind(ma, mc);

        modified[2] = mc;

        return modified;
    }

    /**
     * Копится ли энергия?.
     *
     * @param bot
     * @return 1 - да, 2 - нет
     */
    int isHealthGrow(Bot bot) {
        int t;
        if (bot.mineral < 100) {
            t = 0;
        } else if (bot.mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int hlt = 10 - (15 * bot.y / world.height) + t; // SEZON!!!
        if (hlt >= 3) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Родственники ли боты?.
     *
     * @param bot0
     * @param bot1
     * @return 0 - нет, 1 - да
     */
    int isRelative(Bot bot0, Bot bot1) {
        if (bot1.alive != LV_ALIVE) {
            return 0;
        }
        int dif = 0;    // счетчик несовпадений в геноме
        for (int i = 0; i < MIND_SIZE; i++) {
            if (bot0.mind[i] != bot1.mind[i]) {
                dif = dif + 1;
                if (dif == 2) {
                    return 0;
                }   // если несовпадений в генеме больше 1
            }       // то боты не родственики
        }
        return 1;
    }

    /**
     * Делаем бота более зеленым на экране.
     *
     * @param bot
     * @param num номер бота, на сколько озеленить
     */
    void goGreen(Bot bot, int num) {  // добавляем зелени
        bot.c_green = bot.c_green + num;
        if (bot.c_green + num > 255) {
            bot.c_green = 255;
        }
        int nm = num / 2;
        // убавляем красноту
        bot.c_red = bot.c_red - nm;
        if (bot.c_red < 0) {
            bot.c_blue = bot.c_blue + bot.c_red;
        }
        // убавляем синеву
        bot.c_blue = bot.c_blue - nm;
        if (bot.c_blue < 0) {
            bot.c_red = bot.c_red + bot.c_blue;
        }
        if (bot.c_red < 0) {
            bot.c_red = 0;
        }
        if (bot.c_blue < 0) {
            bot.c_blue = 0;
        }
    }

    /**
     * Делаем бота более синим на экране.
     *
     * @param bot
     * @param num номер бота, на сколько осинить
     */
    void goBlue(Bot bot, int num) {  // добавляем синевы
        bot.c_blue = bot.c_blue + num;
        if (bot.c_blue > 255) {
            bot.c_blue = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        bot.c_green = bot.c_green - nm;
        if (bot.c_green < 0) {
            bot.c_red = bot.c_red + bot.c_green;
        }
        // убавляем красноту
        bot.c_red = bot.c_red - nm;
        if (bot.c_red < 0) {
            bot.c_green = bot.c_green + bot.c_red;
        }
        if (bot.c_red < 0) {
            bot.c_red = 0;
        }
        if (bot.c_green < 0) {
            bot.c_green = 0;
        }
    }

    /**
     * Делаем бота более красным на экране.
     *
     * @param bot
     * @param num номер бота, на сколько окраснить
     */
    void goRed(Bot bot, int num) {  // добавляем красноты
        bot.c_red = bot.c_red + num;
        if (bot.c_red > 255) {
            bot.c_red = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        bot.c_green = bot.c_green - nm;
        if (bot.c_green < 0) {
            bot.c_blue = bot.c_blue + bot.c_green;
        }
        // убавляем синеву
        bot.c_blue = bot.c_blue - nm;
        if (bot.c_blue < 0) {
            bot.c_green = bot.c_green + bot.c_blue;
        }
        if (bot.c_blue < 0) {
            bot.c_blue = 0;
        }
        if (bot.c_green < 0) {
            bot.c_green = 0;
        }
    }

    @Override
    public int getParam() {
        return this.botGetParam(this);
    }

    @Override
    public int getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(int newdrct) {
        if (newdrct >= 8) {
            newdrct = newdrct - 8; // результат должен быть в пределах от 0 до 8
        }
        this.direction = newdrct;
    }

    @Override
    public void incCommandAddress(int i) {
        botIncCommandAddress(this, i);
    }

    @Override
    public void eatSun() {
        botEatSun(this);
    }

    @Override
    public void indirectIncCmdAddress(int a) {
        botIndirectIncCmdAddress(this, a);
    }

    @Override
    public int isMulti() {
        return isMulti(this);
    }

    @Override
    public int move(int drct, int i) {
        return this.botMove(this, drct, i);
    }

    @Override
    public int eat(int drct, int i) {
        return botEat(this, drct, i);
    }

    @Override
    public int seeBots(int drct, int i) {
        return botSeeBots(this, drct, i);
    }

    @Override
    public int care(int drct, int i) {
        return botCare(this, drct, i);
    }

    @Override
    public int give(int drct, int i) {
        return botGive(this, drct, i);
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMineral() {
        return mineral;
    }

    @Override
    public void Double() {
        this.botDouble(this);
    }

    @Override
    public void multi() {
        this.botMulti(this);
    }

    @Override
    public int fullAroud() {
        return fullAroud(this);
    }

    @Override
    public int isHealthGrow() {
        return isHealthGrow(this);
    }

    @Override
    public void mineral2Energy() {
        botMineral2Energy(this);
    }

    @Override
    public void setMind(byte ma, byte mc) {
        if (this.mind[ma] == 49) {
            this.pest--;
        }
        if (mc == 49) {
            this.pest++;
        }
        this.mind[ma] = mc;
    }

    @Override
    public void genAttack() {
        this.botGenAttack(this);

    }

    @Override
    public void pestAttack() {
        int xt = xFromVektorR(this, 0);
        int yt = yFromVektorR(this, 0);
        Bot victim;
        if ((yt >= 0) && (yt < world.height) && ((victim = world.getBot(xt, yt)) != null)) {
            // паразит атакует только живых и незараженных ботов
            if (victim.alive == LV_ALIVE && victim.pest == 0) {
                int healthDrain = victim.health > 100 ? 100 : victim.health;
                this.health = this.health + healthDrain;
                if (this.health > 1000) {
                    this.health = 1000;
                }
                victim.health = victim.health - healthDrain;
                if (victim.health < 1) {
                    bot2Organic(victim);
                }
                // с каждой атакой количество генов паразитирования увеличивается,
                // но не более определенного уровня.
                addPestGenes();
            }
        }
    }

    /**
     * Увеличивает количество генов паразитизма бота.
     */
    private void addPestGenes() {
        if (pest >= MAX_PEST_GENES) {
            return;
        }

        // предположим, что с каждой атакой добавляются 3 гена
        int addPest = 3;

        for (int i = 0; i < addPest; i++) {
            byte ma = (byte) (Math.random() * MIND_SIZE);  // 0..63
            setMind(ma, (byte) 49);
            if (pest >= MAX_PEST_GENES) {
                break;
            }
        }
    }

    @Override
    public int imitate(int dir) {
        // на выходе пусто - 2  стена - 3  органик - 4  бот - 5
        health = health - 4; // бот теряет на этом 4 энергии в независимости от результата
        // вычисляем относительное направление действия
        int xt = xFromVektorR(this, direction);
        int yt = yFromVektorR(this, direction);
        if ((yt < 0) || (yt >= world.height)) {  // если там стена возвращаем 3
            return 3;
        }
        Bot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
        }
        if (bot.alive <= LV_ORGANIC_SINK) {   // если там оказалась органика
            return 4; // возвращаем 4
        }

        Random rnd = new Random();

        // размер копируемого участка от четверти до половины генома
        int len = (int) (MIND_SIZE / 4) + rnd.nextInt((int) (MIND_SIZE / 4));

        // адрес начала участка копирования из генома другого бота
        int adrFrom = (int) (Math.random() * MIND_SIZE);

        // адрес начала участка записи в геном бота
        // следующая ячейка после параметра команды imitate
        int adrTo = adr + 2;

        // копируем данные самым примитивным способом
        for (int i = 0; i < len; i++) {
            adrFrom += i;
            if (adrFrom > MIND_SIZE - 1) {
                // TODO проверить граничные условия
                adrFrom -= MIND_SIZE;
            }
            adrTo += i;
            if (adrTo > MIND_SIZE - 1) {
                // TODO проверить граничные условия
                adrTo -= MIND_SIZE;
            }
            setMind((byte) adrTo, bot.mind[adrFrom]);
        }

        return 2;
    }

    @Override
    public boolean isAlive() {
        return alive == LV_ALIVE;
    }

    @Override
    public boolean isOrganic() {
        return (alive == LV_ORGANIC_SINK) || (alive == LV_ORGANIC_HOLD);
    }
}