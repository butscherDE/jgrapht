package util;


import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;

public class CircularListTest {
    @Test
    public void testCircularIteratorFromStart() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(0);
        assertTrue(listIt.hasNext());
        assertEquals(1, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(2, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(3, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(4, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(5, listIt.next());
        assertFalse(listIt.hasNext());
    }

    @Test
    public void testCircularIteratorFromMiddle() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(2);
        assertTrue(listIt.hasNext());
        assertEquals(3, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(4, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(5, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(1, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(2, listIt.next());
        assertFalse(listIt.hasNext());
    }

    @Test
    public void testCircularIteratorFromEnd() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(4);
        assertTrue(listIt.hasNext());
        assertEquals(5, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(1, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(2, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(3, listIt.next());
        assertTrue(listIt.hasNext());
        assertEquals(4, listIt.next());
        assertFalse(listIt.hasNext());
    }

    @Test
    public void testCircularIteratorOutOfBounds() {
        final List<Integer> list = getIntegers();

        assertThrows(IndexOutOfBoundsException.class, () -> {list.listIterator(5);});
    }

    private List<Integer> getIntegers() {
        final List<Integer> list = new CircularList<>(new LinkedList<>());
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        return list;
    }
}
