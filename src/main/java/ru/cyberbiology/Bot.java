package ru.cyberbiology;

public interface Bot {

    public static final int MIND_SIZE = 64;

    /**
     * Получение параметра для команды.
     *
     * @return возвращает число из днк, следующее за выполняемой командой
     */
    public int getParam();

     /**
     * Возвращает параметр текущего гена с указанным индексом
     * @param index индекс параметра относительно адреса текущего гена
     * @return значение параметра
     */
    public int getParamByIndex(int index);

    /**
     * Получение направления движения.
     *
     * @return возвращает число от 0 до 8 (0 вверх, 5 вниз)
     */
    public int getDirection();

    /**
     * Устанавливает направления движения.
     *
     * @param newdrct число от 0 до 8 (0 вверх, 5 вниз)
     */
    public void setDirection(int newdrct);

    /**
     * Увеличение адреса команды.
     *
     * @param increment насколько прибавить адрес
     */
    public void incCommandAddress(int increment);

    /**
     * Фотосинтез.
     */
    public void eatSun();

    /**
     * Многоклеточный ли бот?.
     *
     * @return
     */
    public int isMulti();

    public boolean isAlive();

    public boolean isOrganic();

    public int move(int drct, int i);

    public void indirectIncCmdAddress(int move);

    public int eat(int drct, int i);

    public int seeBots(int drct, int i);

    public int care(int drct, int i);

    public int give(int drct, int i);

    public int getY();

    public int getHealth();

    public int getMineral();

    public void doubleFree();

    public void doubleMulti();

    public int fullAroud();

    public int isHealthGrow();

    public void mineral2Energy();

    public void setMind(byte ma, byte mc);

    public byte[] modifyMind();

    public void genAttack();

    public void pestAttack();

    public World getWorld();

    public int imitate(int dir);

}
