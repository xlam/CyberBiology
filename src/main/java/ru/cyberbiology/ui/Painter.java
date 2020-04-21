package ru.cyberbiology.ui;

import ru.cyberbiology.World;

/**
 * Интерфейс отрисовщика мира.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public interface Painter {

    /**
     * Устанавливает мир для отрисовки.
     * 
     * @param world мир для отрисовки
     */
    public void setWorld(World world);

    /**
     * Отрисовывает мир.
     */
    public void paint();

}
