package ru.cyberbiology;

import java.util.concurrent.ThreadLocalRandom;
import ru.cyberbiology.gene.Gene;
import ru.cyberbiology.util.ProjectProperties;

public class BasicBot implements Bot {

    // максимальное количество генов паразитирования в геноме
    private static final int MAX_PEST_GENES = 32;

    // максимальное количество шагов для незавершающих команд
    private static final int MAX_STEPS = MIND_SIZE / 4;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    // Далее следуют константы состояния бота, которое отмеченно для каждого бота в массиве bots[].
    /**
     * Место свободно, здесь может быть размещен новый бот.
     */
    public static final int LV_FREE = 0;
    /**
     * Бот погиб и представляет из себя органику в подвешенном состоянии.
     */
    public static final int LV_ORGANIC_HOLD = 1;
    /**
     * Тонущая ораника. Органика тонет, пока не встретит препятствие, после чего остается в подвешенном состоянии
     * (LV_ORGANIC_HOLD).
     */
    public static final int LV_ORGANIC_SINK = 2;
    /**
     * Живой бот.
     */
    public static final int LV_ALIVE = 3;

    public int adr;
    public int posX;
    public int posY;
    public int health;
    public int mineral;
    public int alive;
    public int colorRed;
    public int colorGreen;
    public int colorBlue;
    public int direction;
    public int pest;
    public BasicBot mprev;
    public BasicBot mnext;

    private final ProjectProperties properties = ProjectProperties.getInstance();
    private final Gene[] genes;

    public byte[] mind = new byte[MIND_SIZE];   // геном бота содержит 64 команды

    public GenesHistory genesHistory = new GenesHistory(100);

    /**
     * Поля нужны для сериализации ботов координаты соседних клеток многоклеточного.
     */
    public int mprevX;
    public int mprevY;
    public int mnextX;
    public int mnextY;

    private final BasicWorld world;

    /**
     * Создает нового бота.
     *
     * @param world мир, в котором будет жить бот
     */
    public BasicBot(BasicWorld world) {
        this.world = world;
        genes = world.getGenes();
        direction = RANDOM.nextInt(8);
        health = 5;
        alive = LV_ALIVE;
    }

    /**
     * Получить контроллер гена, на который указывает УТК генома.
     *
     * @return IBotGeneController контроллер гена
     */
    public Gene getCurrentGeneId() {
        return genes[adr];
    }

    /**
     * Возвращает контроллер гена для указанного номера команды.
     *
     * @param id номер команды
     * @return контроллер гена, или null
     */
    public Gene getGeneById(int id) {
        return genes[id];
    }

    @Override
    public int getParamByIndex(int index) {
        int address = (adr + Math.abs(index)) % MIND_SIZE;
        return mind[address];
    }

    /**
     * Главная функция жизнедеятельности бота. Здесь выполняется код его мозга-генома
     */
    public void step() {

        if (checkForOrganicAndMoveIt()) {
            return;
        }

        execGenes();

        /**
         * Выход из функции и передача управления следующему боту. Но перед выходом нужно проверить, входит ли бот в
         * многоклеточную цепочку и если да, то нужно распределить энергию и минералы с соседями, а также проверить
         * количество накопленой энергии - возможно пришло время подохнуть или породить потомка.
         */
        if (alive == LV_ALIVE) {
            processMultiCellResources();
            bornChild();
            updateHealth();

            if (health < 1) {       // если энергии стало меньше 1
                bot2Organic(this);  // то время умирать, превращаясь в огранику
                return;             // и передаем управление к следующему боту
            }

            updateMinerals();
        }
    }

    private boolean checkForOrganicAndMoveIt() {

        if (alive == LV_FREE || alive == LV_ORGANIC_HOLD) {
            return true;
        } else if (alive == LV_ORGANIC_SINK) {
            // движение вниз в абсолютном направлении и остановка, если
            // уперлись в препятствие
            if (move(5, 1) != 2) {
                alive = LV_ORGANIC_HOLD;
            }
            return true; // это труп - выходим!
        }
        return false;
    }

    private void execGenes() {

        Gene gene;

        int steps = getParamByIndex(2);
        if (steps > MAX_STEPS) {
            steps = MAX_STEPS;
        }

        for (int cyc = 0; cyc < steps; cyc++) { //15
            int id = mind[adr];

            // Получаем обработчика команды
            gene = genes[id];
            genesHistory.add(id);
            if (gene != null) { // если обработчик такой команды назначен
                if (gene.exec(this)) { // передаем ему управление
                    break; // если обрабочик говорит, что он последний - завершаем цикл?
                }
            } else {
                // если ни с одной команд не совпало значит безусловный переход
                // прибавляем к указателю текущей команды значение команды
                incCommandAddress(id);
                break;
            }
            if (alive != LV_ALIVE || health <= 0) {
                break;
            }
        }
    }

    private void processMultiCellResources() {

        int a = isMulti();

        // распределяем энергию  минералы по многоклеточному организму
        // возможны три варианта, бот находится внутри цепочки
        // бот имеет предыдущего бота в цепочке и не имеет следующего
        // бот имеет следующего бота в цепочке и не имеет предыдущего
        if (a == 3) {   // бот находится внутри цепочки
            BasicBot pb = mprev; // ссылка на предыдущего бота в цепочке
            BasicBot nb = mnext; // ссылка на следующего бота в цепочке
            // делим минералы
            int m = mineral + nb.mineral + pb.mineral; // общая сумма минералов
            //распределяем минералы между всеми тремя ботами
            m /= 3;
            mineral = m;
            nb.mineral = m;
            pb.mineral = m;
            // делим энергию
            // проверим, являются ли следующий и предыдущий боты в цепочке крайними
            // если они не являются крайними, то распределяем энергию поровну
            // связанно это с тем, что в крайних ботах в цепочке должно быть больше энергии
            // что бы они плодили новых ботов и удлиняли цепочку
            int apb = pb.isMulti();
            int anb = nb.isMulti();
            if ((anb == 3) && (apb == 3)) { // если следующий и предыдущий боты не являются крайними
                // то распределяем энергию поровну
                int h = health + nb.health + pb.health;
                h /= 3;
                health = h;
                nb.health = h;
                pb.health = h;
            }
        }
        // бот является крайним в цепочке и имеет предыдкщего бота
        if (a == 1) {
            BasicBot pb = mprev; // ссылка на предыдущего бота
            int apb = pb.isMulti();  // проверим, является ли предыдущий бот крайним в цепочке
            if (apb == 3) { // если нет, то распределяем энергию в пользу текущего бота
                // так как он крайний и ему нужна энергия для роста цепочки
                int h = health + pb.health;
                h /= 4;
                health = h * 3;
                pb.health = h;
            }
        }
        // бот является крайним в цепочке и имеет следующего бота
        if (a == 2) {
            BasicBot nb = mnext; // ссылка на следующего бота
            int anb = nb.isMulti();  // проверим, является ли следующий бот крайним в цепочке
            if (anb == 3) {         // если нет, то распределяем энергию в пользу текущего бота
                // так как он крайний и ему нужна энергия для роста цепочки
                int h = health + nb.health;
                h /= 4;
                health = h * 3;
                nb.health = h;
            }
        }
    }

    private void bornChild() {

        int a = isMulti();

        // Проверим уровень энергии у бота, возможно пришла пора помереть или родить.
        // Вопрос стоит ли так делать, родждение прописано в генных командах
        // Sergey Sokolov: ответ - стоит, ибо то, что прописано в генах это спонтанные роды,
        // а тут естественные.
        if (health > 999 || (health > 399 && pest > 0)) { // паразиту достаточно 400 энергии для размножения
            if ((a == 1) || (a == 2)) {
                doubleMulti(); // если бот был крайним в цепочке, то его потомок входит в состав цепочки
            } else {
                doubleFree(); // если бот был свободным или находился внутри цепочки
                // то его потомок рождается свободным
            }
        }
    }

    private void updateHealth() {
        // трата энергии за ход
        if (pest > 0) {
            health -= 20; // паразит
        } else {
            health -= 3; // здоровый бот
        }
    }

    private void updateMinerals() {
        // бот накапливает минералы, но не более 999
        if (mineral < 999) {
            mineral += world.getMineralsAt(posY);
            if (mineral > 999) {
                mineral = 999;
            }
        }
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    /**
     * Вычисляет Х-координату рядом с био по относительному направлению.
     *
     * @param bot бот (deprecated)
     * @param n направление
     * @return X - координата по абсолютному направлению
     */
    private int getRelativeDirectionX(int n) {
        int xt = posX + Constant.INCREMENT_X[direction + n];
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
     * @param bot бот (deprecated)
     * @param n направление
     * @return X - координата по абсолютному направлению
     */
    private int getAbsoluteDirectionX(int n) {
        return correctX(posX + Constant.INCREMENT_X[n]);
    }

    /**
     * Проверка и корректировка координаты X при выходе за границы мира.
     *
     * @param x - проверяемая координата X
     * @return 0 - если координата вышла за правую границу;
     *      [ширина мира]-1 - если координата вышла за левую границу
     */
    private int correctX(int x) {
        return (x >= world.width) ? 0 : ((x < 0) ? world.width - 1 : x);
    }

    /**
     * Вычисляет Y-координату рядом с био по относительному направлению.
     *
     * @param bot бот (deprecated)
     * @param n направление
     * @return Y координата по относительному направлению
     */
    private int getRelativeDirectionY(int n) {
        return posY + Constant.INCREMENT_Y[direction + n];
    }

    /**
     * Вычисляет Y-координату рядом с био по абсолютному направлению.
     *
     * @param bot бот (deprecated)
     * @param n направление
     * @return Y координата по абсолютному направлению
     */
    private int getAbsoluteDirectionY(int n) {
        return posY + Constant.INCREMENT_Y[n];
    }

    /**
     * Окружен ли бот?.
     *
     * @return 1-окружен 2-нет
     */
    @Override
    public int fullAroud() {
        for (int i = 0; i < 8; i++) {
            int xt = getRelativeDirectionX(i);
            int yt = getRelativeDirectionY(i);
            if ((yt >= 0) && (yt < world.height) && world.getBot(xt, yt) == null) {
                return 2;
            }
        }
        return 1;
    }

    /**
     * Ищет свободные ячейки вокруг бота кругу через низ.
     *
     * @return номер направление или 8 , если свободных нет
     */
    private int findEmptyDirection() {
        for (int i = 0; i < 8; i++) {
            int xt = getRelativeDirectionX(i);
            int yt = getRelativeDirectionY(i);
            if (yt >= 0 && yt < world.height && world.getBot(xt, yt) == null) {
                return i;
            }
        }
        // свободных направлений нет
        return 8;
    }

    /**
     * Получает параметр для текущей команды.
     *
     * @return возвращает число из днк, следующее за выполняемой командой
     */
    @Override
    public int getParam() {
        int paramadr = adr + 1;
        if (paramadr >= MIND_SIZE) {
            paramadr -= MIND_SIZE;
        }
        return mind[paramadr]; // возвращает число, следующее за выполняемой командой
    }

    /**
     * Увеличивает адрес команды.
     *
     * @param a насколько прибавить адрес
     */
    @Override
    public void incCommandAddress(int a) {
        int paramadr = adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr -= MIND_SIZE;
        }
        adr = paramadr;
    }

    /**
     * Косвенно увеличивает адрес команды.
     *
     * @param a смещение до команды, которая станет смещением
     */
    @Override
    public void indirectIncCmdAddress(int a) {
        int paramadr = adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr -= MIND_SIZE;
        }
        int bias = mind[paramadr];
        incCommandAddress(bias);
    }

    /**
     * Превращение бота в органику.
     */
    private void bot2Organic(BasicBot bot) {
        bot.alive = LV_ORGANIC_SINK;    // отметим в массиве bots[], что бот органика
        BasicBot pbot = bot.mprev;
        BasicBot nbot = bot.mnext;
        if (pbot != null) {
            pbot.mnext = null;
        } // удаление бота из многоклеточной цепочки
        if (nbot != null) {
            nbot.mprev = null;
        }
        bot.mprev = null;
        bot.mnext = null;
    }

    /**
     * Нахожусь ли я в многоклеточной цепочке?.
     *
     * @return 0 - нет, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
     */
    @Override
    public int isMulti() {
        int a = 0;
        if (mprev != null) {
            a = 1;
        }
        if (mnext != null) {
            a += 2;
        }
        return a;
    }

    /**
     * Перемещает бота в нужную точку без проверок.
     *
     * @param xt новые координаты x
     * @param yt новые координаты y
     */
    private void moveBot(int xt, int yt) {
        world.matrix[world.width * yt + xt] = this;
        world.clearBot(posX, posY);
        posX = xt;
        posY = yt;
    }

    /**
     * Удаление бота.
     *
     * @param bot бот
     */
    private void deleteBot(BasicBot bot) {
        bot.alive = LV_FREE;
        BasicBot pbot = bot.mprev;
        BasicBot nbot = bot.mnext;
        if (pbot != null) {
            pbot.mnext = null;
        } // удаление бота из многоклеточной цепочки
        if (nbot != null) {
            nbot.mprev = null;
        }
        bot.mprev = null;
        bot.mnext = null;
        world.clearBot(bot.posX, bot.posY); // удаление бота с карты
    }

    /**
     * Фотосинтез.Этой командой забит геном первого бота бот получает энергию солнца в зависимости от глубины и
     * количества минералов, накопленных ботом.
     */
    @Override
    public void eatSun() {
        int t;
        if (mineral < 100) {
            t = 0;
        } else if (mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int a = 0;
        if (mprev != null) {
            a += 4;
        }
        if (mnext != null) {
            a += 4;
        }
        int hlt = a + 1 * (11 - (15 * posY / world.height) + t); // формула вычисления энергии
        if (hlt > 0) {
            health += hlt;  // прибавляем полученную энергия к энергии бота
            goGreen(hlt);              // бот от этого зеленеет
        }
    }

    /**
     * Преобразование минералов в энергию.
     */
    @Override
    public void mineral2Energy() {
        if (mineral > 100) {   // максимальное количество минералов, которые можно преобразовать в энергию = 100
            mineral -= 100;
            health += 400; // 1 минерал = 4 энергии
            goBlue(100);  // бот от этого синеет
        } else {  // если минералов меньше 100, то все минералы переходят в энергию
            goBlue(mineral);
            health += 4 * mineral;
            mineral = 0;
        }
    }

    /**
     * Перемещение бота.
     *
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return  2-пусто; 3-стена; 4-органика; 5-бот; 6-родня;
     *          7-похожий по минералам; 8-похожий по энергии
     */
    @Override
    public int move(int direction, int ra) {
        int xt;
        int yt;
        if (ra == 0) {          // вычисляем координату клетки, куда перемещается бот (относительное направление)
            xt = getRelativeDirectionX(direction);
            yt = getRelativeDirectionY(direction);
        } else {                // вычисляем координату клетки, куда перемещается бот (абсолютное направление)
            xt = getAbsoluteDirectionX(direction);
            yt = getAbsoluteDirectionY(direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там ... стена
            return 3;                       // то возвращаем 3
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка была пустая,
            moveBot(xt, yt);    // то перемещаем бота
            return 2;                       // и функция возвращает 2
        }
        // осталось 2 варианта: ограника или бот
        if (bot.alive <= LV_ORGANIC_SINK) { // если на клетке находится органика
            return 4;                       // то возвращаем 4
        }
        if (isRelative(bot) == 1) {  // если на клетке родня
            return 6;                      // то возвращаем 6
        }
        if (properties.getBoolean("EnableRelativeByMinerals") && mineralsRelative(bot)) {
            return 7;
        }
        if (properties.getBoolean("EnableRelativeByEnergy") && energyRelative(bot)) {
            return 8;
        }
        return 5;                         // остался только один вариант - на клетке какой-то бот возвращаем 5
    }

    /**
     * Скушать другого бота или органику.
     *
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return пусто - 2 стена - 3 органик - 4 бот - 5
     */
    @Override
    public int eat(int direction, int ra) {
        // на выходе пусто - 2  стена - 3  органик - 4  бот - 5
        health -= 4; // бот теряет на этом 4 энергии в независимости от результата
        int xt;
        int yt;
        if (ra == 0) {  // вычисляем координату клетки, с которой хочет скушать бот (относительное направление)
            xt = getRelativeDirectionX(direction);
            yt = getRelativeDirectionY(direction);
        } else {        // вычисляем координату клетки, с которой хочет скушать бот (абсолютное направление)
            xt = getAbsoluteDirectionX(direction);
            yt = getAbsoluteDirectionY(direction);
        }
        if ((yt < 0) || (yt >= world.height)) {  // если там стена возвращаем 3
            return 3;
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
            // осталось 2 варианта: ограника или бот
        } else if (bot.alive <= LV_ORGANIC_SINK) { // если там оказалась органика
            deleteBot(bot);                        // то удаляем её из списков
            health += 100;  //здоровье увеличилось на 100
            goRed(100);               // бот покраснел
            return 4;                       // возвращаем 4
        }
        // дошли до сюда, значит впереди живой бот
        int min0 = mineral;     // определим количество минералов у бота
        int min1 = bot.mineral;    // определим количество минералов у потенциального обеда
        int hl = bot.health;       // определим энергию у потенциального обеда
        // если у бота минералов больше
        if (min0 >= min1) {
            // количество минералов у бота уменьшается на количество минералов у жертвы,
            // типа, стесал свои зубы о панцирь жертвы
            mineral = min0 - min1;
            deleteBot(bot);          // удаляем жертву из списков
            int cl = 100 + (hl / 2);           // количество энергии у бота прибавляется на 100+(половина от энергии жертвы)
            health += cl;
            goRed(cl);                    // бот краснеет
            return 5;                              // возвращаем 5
        }
        // если у жертвы минералов больше
        mineral = 0; // то бот израсходовал все свои минералы на преодоление защиты
        min1 -= min0;       // у жертвы количество минералов тоже уменьшилось
        bot.mineral = min1;
        // если здоровья в 2 раза больше, чем минералов у жертвы,
        // то здоровьем проламываем минералы
        if (health > 2 * min1) {
            deleteBot(bot);         // удаляем жертву из списков
            int cl = 100 + (hl / 2); // вычисляем, сколько энергии смог получить бот
            health = health - (2 * min1) + cl;

            goRed(cl);    // бот краснеет
            return 5;           // возвращаем 5
        }
        // если здоровья меньше, чем (минералов у жертвы)*2, то бот погибает от жертвы
        bot.mineral = min1 - (health / 2);  // у жертвы минералы истраченны
        alive = LV_FREE;
        health = 0; // здоровье уходит в ноль
        return 5;       // возвращаем 5
    }

    /**
     * Посмотреть.
     *
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return  2-пусто; 3-стена; 4-органика; 5-бот; 6-родня;
     *          7-похожий по минералам; 8-похожий по энергии
     */
    @Override
    public int seeBots(int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        int xt;
        int yt;
        if (ra == 0) {  // выясняем, есть ли что в этом  направлении (относительном)
            xt = getRelativeDirectionX(direction);
            yt = getRelativeDirectionY(direction);
        } else {       // выясняем, есть ли что в этом  направлении (абсолютном)
            xt = getAbsoluteDirectionX(direction);
            yt = getAbsoluteDirectionY(direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        } else if (isRelative(bot) == 1) {  // если родня, то возвращаем 6
            return 6;
        } else if (properties.getBoolean("EnableRelativeByMinerals") && mineralsRelative(bot)) {
            return 7;
        } else if (properties.getBoolean("EnableRelativeByEnergy") && energyRelative(bot)) {
            return 8;
        } else { // если какой-то бот, то возвращаем 5
            return 5;
        }
    }

    /**
     * Атака на геном соседа, меняем случайный ген случайным образом.
     */
    @Override
    public void genAttack() {   // вычисляем кто у нас перед ботом (используется только относительное направление вперед)
        int xt = getRelativeDirectionX(0);
        int yt = getRelativeDirectionY(0);
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {
            return;
        }
        if ((yt >= 0) && (yt < world.height) && (bot.alive == LV_ALIVE)) {
            health -= 10; // то атакуюий бот теряет на атаку 10 энергии
            if (health > 0) {
                bot.modifyMind();
            }
        }
    }

    /**
     * Поделится. Если у бота больше энергии или минералов, чем у соседа в заданном направлении то бот делится
     * излишками.
     *
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return
     */
    @Override
    public int care(int direction, int ra) { // на входе ссылка на бота, направлелие и флажок(относительное или абсолютное направление)
        // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) {  // определяем координаты для относительного направления
            xt = getRelativeDirectionX(direction);
            yt = getRelativeDirectionY(direction);
        } else {        // определяем координаты для абсолютного направления
            xt = getAbsoluteDirectionX(direction);
            yt = getAbsoluteDirectionY(direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        }
        // если мы здесь, то в данном направлении живой
        int hlt0 = health;      // определим количество энергии и минералов
        int hlt1 = bot.health;     // у бота и его соседа
        int min0 = mineral;
        int min1 = bot.mineral;
        if (hlt0 > hlt1) {                  // если у бота больше энергии, чем у соседа
            int hlt = (hlt0 - hlt1) / 2;    // то распределяем энергию поровну
            health -= hlt;
            bot.health += hlt;
        }
        if (min0 > min1) {                  // если у бота больше минералов, чем у соседа
            int min = (min0 - min1) / 2;    // то распределяем их поровну
            mineral -= min;
            bot.mineral += min;
        }
        return 5;
    }

    /**
     * Отдать безвозместно, то есть даром.
     *
     * @param direction направлелие
     * @param ra флажок(относительное или абсолютное направление)
     * @return стена - 2 пусто - 3 органика - 4 удачно - 5
     */
    @Override
    public int give(int direction, int ra) { // на выходе стена - 2 пусто - 3 органика - 4 удачно - 5
        int xt;
        int yt;
        if (ra == 0) { // определяем координаты для относительного направления
            xt = getRelativeDirectionX(direction);
            yt = getRelativeDirectionY(direction);
        } else {        // определяем координаты для абсолютного направления
            xt = getAbsoluteDirectionX(direction);
            yt = getAbsoluteDirectionY(direction);
        }
        if (yt < 0 || yt >= world.height) {  // если там стена возвращаем 3
            return 3;
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
        } else if (bot.alive <= LV_ORGANIC_SINK) { // если органика возвращаем 4
            return 4;
        }
        // если мы здесь, то в данном направлении живой
        int hlt0 = health;  // бот отдает четверть своей энергии
        int hlt = hlt0 / 4;
        health = hlt0 - hlt;
        bot.health += hlt;

        int min0 = mineral; // бот отдает четверть своих минералов
        if (min0 > 3) {         // только если их у него не меньше 4
            int min = min0 / 4;
            mineral = min0 - min;
            bot.mineral += min;
            if (bot.mineral > 999) {
                bot.mineral = 999;
            }
        }
        return 5;
    }

    /**
     * Рождение нового бота делением.
     */
    @Override
    public void doubleFree() {
        health -= 150;  // бот затрачивает 150 единиц энергии на создание копии
        if (health <= 0) {
            return; // если у него было меньше 150, то пора помирать
        }
        int n = findEmptyDirection();    // проверим, окружен ли бот
        if (n == 8) {                       // если бот окружен, то он в муках погибает
            alive = LV_FREE;
            health = 0;
            return;
        }

        BasicBot newbot = new BasicBot(this.world);

        System.arraycopy(mind, 0, newbot.mind, 0, MIND_SIZE);

        // информация о паразитных генах сохраняется в новом боте
        newbot.pest = pest;

        if (RANDOM.nextInt(100) <= 25) { // в одном случае из четырех случайным образом меняем один случайный байт в геноме
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

        newbot.adr = adr;
        newbot.posX = getRelativeDirectionX(n);
        newbot.posY = getRelativeDirectionY(n);

        newbot.health = health / 2;   // забирается половина здоровья у предка
        health /= 2;
        newbot.mineral = mineral / 2; // забирается половина минералов у предка
        mineral /= 2;

        newbot.alive = LV_ALIVE;        // отмечаем, что бот живой

        newbot.colorRed = colorRed;       // цвет такой же, как у предка
        newbot.colorGreen = colorGreen;   // цвет такой же, как у предка
        newbot.colorBlue = colorBlue;     // цвет такой же, как у предка

        world.setBot(newbot);    // отмечаем нового бота в массиве matrix
    }

    /**
     * Рождение новой клетки многоклеточного.
     */
    @Override
    public void doubleMulti() {
        BasicBot pbot = mprev;    // ссылки на предыдущего и следущего в многоклеточной цепочке
        BasicBot nbot = mnext;
        // если обе ссылки больше 0, то бот уже внутри цепочки
        if ((pbot != null) && (nbot != null)) {
            return; // поэтому выходим без создания нового бота
        }
        health -= 150; // бот затрачивает 150 единиц энергии на создание копии
        if (health <= 0) {
            return; // если у него было меньше 150, то пора помирать
        }
        int n = findEmptyDirection(); // проверим, окружен ли бот

        if (n == 8) {  // если бот окружен, то он в муках погибает
            alive = LV_FREE;
            health = 0;
            return;
        }
        BasicBot newbot = new BasicBot(this.world);
        newbot.pest = pest;

        System.arraycopy(mind, 0, newbot.mind, 0, MIND_SIZE);   // копируем геном в нового бота

        if (RANDOM.nextInt(100) <= 25) { // в одном случае из четырех случайным образом меняем один случайный байт в геноме
            newbot.modifyMind();
        }

        newbot.adr = 0;     // указатель текущей команды в новорожденном устанавливается в 0
        newbot.posX = getRelativeDirectionX(n);
        newbot.posY = getRelativeDirectionY(n);

        newbot.health = health / 2;   // забирается половина здоровья у предка
        health /= 2;
        newbot.mineral = mineral / 2; // забирается половина минералов у предка
        mineral /= 2;

        newbot.alive = LV_ALIVE;        // отмечаем, что бот живой

        newbot.colorRed = colorRed;       // цвет такой же, как у предка
        newbot.colorGreen = colorGreen;   // цвет такой же, как у предка
        newbot.colorBlue = colorBlue;     // цвет такой же, как у предка

        world.setBot(newbot);    // отмечаем нового бота в массиве matrix

        if (nbot == null) { // если у бота-предка ссылка на следующего бота в многоклеточной цепочке пуста
            mnext = newbot;     // то вставляем туда новорожденного бота
            newbot.mprev = this;     // у новорожденного ссылка на предыдущего указывает на бота-предка
            newbot.mnext = null;    // ссылка на следующего пуста, новорожденный бот является крайним в цепочке
        } else {            // если у бота-предка ссылка на предыдущего бота в многоклеточной цепочке пуста
            mprev = newbot;     // то вставляем туда новорожденного бота
            newbot.mnext = this;     // у новорожденного ссылка на следующего указывает на бота-предка
            newbot.mprev = null;    // ссылка на предыдущего пуста, новорожденный бот является крайним в цепочке
        }
    }

    /**
     * Меняет один ген случайным образом.
     *
     * @return byte[3] массив с информацией об измененном гене, где:
     *      [0] - адрес измененной команды
     *      [1] - старая команда
     *      [2] - новая команда
     */
    @Override
    public byte[] modifyMind() {

        byte[] modified = new byte[3];

        // случайным образом меняется один ген
        byte ma = (byte) RANDOM.nextInt(MIND_SIZE); // 0..63
        byte mc = (byte) RANDOM.nextInt(MIND_SIZE); // 0..63

        modified[0] = ma;
        modified[1] = mind[ma];
        setMind(ma, mc);
        modified[2] = mc;
        return modified;
    }

    /**
     * Копится ли энергия?.
     *
     * @return 1 - да, 2 - нет
     */
    @Override
    public int isHealthGrow() {
        int t;
        if (mineral < 100) {
            t = 0;
        } else if (mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int hlt = 10 - (15 * posY / world.height) + t; // SEZON!!!
        if (hlt >= 3) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Родственники ли боты?.
     *
     * @return 0 - нет, 1 - да
     */
    private int isRelative(BasicBot bot) {
        if (bot.alive != LV_ALIVE) {
            return 0;
        }
        int dif = 0;    // счетчик несовпадений в геноме
        for (int i = 0; i < MIND_SIZE; i++) {
            if (mind[i] != bot.mind[i]) {
                dif += 1;
                if (dif == 2) {
                    return 0;
                }   // если несовпадений в генеме больше 1
            }       // то боты не родственики
        }
        return 1;
    }

    private boolean mineralsRelative(BasicBot bot) {
        // 10% от своих минералов
        int p = (int) (mineral * 0.01 * 10);
        return Math.abs(mineral - bot.mineral) < p;
    }

    private boolean energyRelative(BasicBot bot) {
        // 10% от своей энергии
        int p = (int) (health * 0.01 * 10);
        return Math.abs(health - bot.health) < p;
    }

    /**
     * Делаем бота более зеленым на экране.
     *
     * @param num номер бота, на сколько озеленить
     */
    private void goGreen(int num) {  // добавляем зелени
        colorGreen += num;
        if (colorGreen > 255) {
            colorGreen = 255;
        }
        int nm = num / 2;
        // убавляем красноту
        colorRed -= nm;
        if (colorRed < 0) {
            colorBlue += colorRed;
        }
        // убавляем синеву
        colorBlue -= nm;
        if (colorBlue < 0) {
            colorRed += colorBlue;
        }
        if (colorRed < 0) {
            colorRed = 0;
        }
        if (colorBlue < 0) {
            colorBlue = 0;
        }
    }

    /**
     * Делаем бота более синим на экране.
     *
     * @param num номер бота, на сколько осинить
     */
    private void goBlue(int num) {  // добавляем синевы
        colorBlue += num;
        if (colorBlue > 255) {
            colorBlue = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        colorGreen -= nm;
        if (colorGreen < 0) {
            colorRed += colorGreen;
        }
        // убавляем красноту
        colorRed -= nm;
        if (colorRed < 0) {
            colorGreen += colorRed;
        }
        if (colorRed < 0) {
            colorRed = 0;
        }
        if (colorGreen < 0) {
            colorGreen = 0;
        }
    }

    /**
     * Делаем бота более красным на экране.
     *
     * @param num номер бота, на сколько окраснить
     */
    private void goRed(int num) {  // добавляем красноты
        colorRed += num;
        if (colorRed > 255) {
            colorRed = 255;
        }
        int nm = num / 2;
        // убавляем зелень
        colorGreen -= nm;
        if (colorGreen < 0) {
            colorBlue += colorGreen;
        }
        // убавляем синеву
        colorBlue -= nm;
        if (colorBlue < 0) {
            colorGreen += colorBlue;
        }
        if (colorBlue < 0) {
            colorBlue = 0;
        }
        if (colorGreen < 0) {
            colorGreen = 0;
        }
    }

    @Override
    public int getDirection() {
        return direction;
    }

    @Override
    public void setDirection(int newdrct) {
        int dir = newdrct;
        if (dir >= 8) {
            dir -= 8; // результат должен быть в пределах от 0 до 8
        }
        direction = dir;
    }

    @Override
    public int getY() {
        return posY;
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
    public void setMind(byte ma, byte mc) {
        if (mind[ma] == 49) {
            pest--;
        }
        if (mc == 49) {
            pest++;
        }
        mind[ma] = mc;
    }

    @Override
    public void pestAttack() {
        int xt = getRelativeDirectionX(0);
        int yt = getRelativeDirectionY(0);
        BasicBot victim;
        if (yt >= 0 && yt < world.height) {
            victim = world.getBot(xt, yt);
            // паразит атакует только живых и незараженных ботов
            if (victim != null && victim.alive == LV_ALIVE && victim.pest == 0) {
                int healthDrain = victim.health > 100 ? 100 : victim.health;
                this.health += healthDrain;
                if (this.health > 1000) {
                    this.health = 1000;
                }
                victim.health = healthDrain - victim.health;
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
            byte ma = (byte) RANDOM.nextInt(MIND_SIZE); // 0..63
            setMind(ma, (byte) 49);
            if (pest >= MAX_PEST_GENES) {
                break;
            }
        }
    }

    @Override
    public int imitate(int dir) {
        // на выходе пусто - 2  стена - 3  органик - 4  бот - 5
        health -= 4; // бот теряет на этом 4 энергии в независимости от результата
        // вычисляем относительное направление действия
        int xt = getRelativeDirectionX(direction);
        int yt = getRelativeDirectionY(direction);
        if ((yt < 0) || (yt >= world.height)) {  // если там стена возвращаем 3
            return 3;
        }
        BasicBot bot = world.getBot(xt, yt);
        if (bot == null) {  // если клетка пустая возвращаем 2
            return 2;
        }
        if (bot.alive <= LV_ORGANIC_SINK) {   // если там оказалась органика
            return 4; // возвращаем 4
        }

        // размер копируемого участка от четверти до половины генома
        int len = (int) (MIND_SIZE / 4) + RANDOM.nextInt((int) (MIND_SIZE / 4));

        // адрес начала участка копирования из генома другого бота
        int adrFrom = RANDOM.nextInt(MIND_SIZE);

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
