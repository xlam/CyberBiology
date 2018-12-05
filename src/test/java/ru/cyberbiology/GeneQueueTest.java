package ru.cyberbiology;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Sergey Sokolov (xlamserg@gmail.com)
 */
public class GeneQueueTest {

    private final int size = 5;
    private GeneQueue queue;

    @Before
    public void setUp() {
        queue = new GeneQueue(size);
    }

    @After
    public void tearDown() {
        queue = null;
    }

    @Test
    public void newQueueIsEmpty() {
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.count());
    }

    @Test
    public void emptyQueueToArrayReturnsNull() {
        assertNull(queue.toArray());
    }

    @Test
    public void queueElementsCountEqualsAddedElementsCount() {
        add(3);
        assertEquals(3, queue.count());
    }

    @Test
    public void elementsCountDoesNotExceedQueueSize() {
        add(6);
        assertEquals(size, queue.count());
    }

    @Test
    public void addingInFullQueueOverwritesTheOldestElement() {
        add(5);
        queue.add(25);
        assertEquals(25, (queue.toArray())[0]);
        queue.add(30);
        assertEquals(30, (queue.toArray())[0]);
    }

    private void add(int count) {
        for (int i = 0; i < count; i++) {
            queue.add(i+1);
        }
    }
}
