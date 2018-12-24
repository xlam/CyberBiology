package ru.cyberbiology;

/**
 * Класс представляет собой список выполненных генов в виде очереди.
 * 
 * <p>В отличие от классической очереди, данная очередь не позволяет
 * извлекать или получать элементы, их можно только добавлять. Эта очередь
 * имеет фиксированный размер. При заполнении очереди, вновь добавляемые
 * элементы переписывают самые старые.
 * Все элементы очереди можно получить в виде массива с помощью <code>toArray()</code>.
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class GenesHistory {

    private final int size;
    private final int[] data;
    private int tail;
    private int count = 0;

    /**
     * Создает объект очереди.
     *
     * @param size размер очереди - максимальное количество сохраняемых элементов
     */
    public GenesHistory(int size) {
        this.size = size;
        data = new int[size];
    }

    /**
     * Проверяет пуста ли очередь.
     *
     * @return  <code>true</code> если очередь пуста;
     *          <code>false</code> в противном случае
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Возвращает количество элементов в очереди.
     *
     * @return количество элементов в очереди
     */
    public int count() {
        return count;
    }

    /**
     * Добавляет элемент в очередь.
     *
     * @param i добавляемый элемент
     */
    public void add(int i) {
        if (++tail == size) {
            tail = 0;
        }
        data[tail] = i;
        if (count < size) {
            count++;
        }
    }

    /**
     * Возвращает представление очереди в виде массива.
     *
     * <p>Массив будет иметь размер, соответствующий количеству элементов.
     * Элементы будут расположены в порядке от самого нового до самого старого.
     *
     * @return упорядоченный массив очереди
     */
    public int[] toArray() {
        if (isEmpty()) {
            return null;
        }
        int[] result = new int[count];
        int j = tail + 1;
        for (int i = 0; i < count; i++) {
            if (--j < 0) {
                j = size - 1;
            }
            result[i] = data[j];
        }
        return result;
    }

    /**
     * Возвращает представление очереди в виде массива строк.
     *
     * <p>Свойства массива такие же как у возвращаемого <code>toArray()</code>
     *
     * @return упорядоченный массив очереди
     */
    public String[] toStringArray() {
        if (isEmpty()) {
            return null;
        }
        int[] arr = toArray();
        String[] result = new String[arr.length];
        int j = 0;
        for (int i : arr) {
            result[j++] = String.valueOf(i);
        }
        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        int[] arr = toArray();
        String result = "[" + arr[0];
        for (int i = 1; i < arr.length; i++) {
            result += "," + arr[i];
        }
        return result += "]";
    }
}
