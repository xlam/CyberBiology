package ru.cyberbiology.gene;

import ru.cyberbiology.Bot;

/**
 * Интерфейс обработчика гена бота.
 *
 * @author Nickolay
 */
public interface Gene {

    /**
     * Реализация одного шага интерпетации гена.
     *
     * @param bot бот, над которыым проводится процедура
     * @return true, если обработка в данной серии этому боту больше
     *      не требуется.
     */
    public boolean exec(Bot bot);

    public String getDescription();
}
