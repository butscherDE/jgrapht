package util;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
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

        assertThrows(IndexOutOfBoundsException.class, () -> list.listIterator(6));
    }

    @Test
    public void testCircularIteratorReverseFromStart() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(0);
        assertTrue(listIt.hasPrevious());
        assertEquals(5, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(4, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(3, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(2, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(1, listIt.previous());
        assertFalse(listIt.hasPrevious());
    }

    @Test
    public void testCircularIteratorReverseFromMiddle() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(2);
        assertTrue(listIt.hasPrevious());
        assertEquals(2, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(1, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(5, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(4, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(3, listIt.previous());
        assertFalse(listIt.hasPrevious());
    }

    @Test
    public void testCircularIteratorReverseFromEnd() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> listIt = list.listIterator(5);
        assertTrue(listIt.hasPrevious());
        assertEquals(5, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(4, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(3, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(2, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(1, listIt.previous());
        assertFalse(listIt.hasPrevious());
    }

    @Test
    public void testCircularIteratorForwardAndBackward() {
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

        assertTrue(listIt.hasPrevious());
        assertEquals(4, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(3, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(2, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(1, listIt.previous());
        assertTrue(listIt.hasPrevious());

        assertEquals(5, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(4, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(3, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(2, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(1, listIt.previous());
        assertTrue(listIt.hasPrevious());
        assertEquals(5, listIt.previous());
        assertFalse(listIt.hasPrevious());
    }

    @Test
    public void isSizeUpdatedOnListIteratorModification() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> it = list.listIterator();
        it.next();
        it.remove();

        assertEquals(4, list.size());
    }

    @Test
    public void setElement() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> it = list.listIterator();
        it.next();
        it.set(11);
        it.next();
        it.set(12);
        it.next();
        it.next();
        it.previous();
        it.set(13);

        assertEquals(11, list.get(0));
        assertEquals(12, list.get(1));
        assertEquals(13, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(5, list.get(4));
    }

    @Test
    public void setElementCircularForward() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> it = list.listIterator(4);
        it.next();
        it.set(15);
        it.next();
        it.set(11);

        assertEquals(11, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(15, list.get(4));
    }


    @Test
    public void setElementCircularReverse() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> it = list.listIterator();
        it.next();
        it.set(11);
        it.previous();
        it.set(15);

        assertEquals(11, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
        assertEquals(15, list.get(4));
    }

    @Test
    public void listIteratorIdentity() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> itParameterLess = list.listIterator();
        final ListIterator<Integer> itParameter = list.listIterator(0);

        assertEquals(itParameterLess.next(), itParameter.next());
    }

    @Test
    public void learningTestBuiltInListIterator() {
        final List<Integer> list = new LinkedList<>(Arrays.asList(1,2,3,4,5));

        final ListIterator<Integer> it = list.listIterator();
        it.next();
        it.next();
        assertEquals(2, it.previous());
        assertEquals(1, it.previous());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
    }

    @Test
    public void learningTestBuiltInListIteratorIndex() {
        final List<Integer> list = new LinkedList<>(Arrays.asList(1,2,3,4,5));

        final ListIterator<Integer> it = list.listIterator();
        it.next();
        it.next();
        assertEquals(1, it.previousIndex());
        assertEquals(2, it.previous());
        assertEquals(1, it.previous());
        assertEquals(1, it.next());
        assertEquals(2, it.next());
    }

    @Test
    public void backAndForthIterationDoesntTakeExtraStepsAsInNormalListIteratorImplementation() {
        final List<Integer> list = getIntegers();

        final ListIterator<Integer> it = list.listIterator();
        assertEquals(1, it.next());
        assertEquals(2, it.next());
        assertEquals(1, it.previous());
        assertEquals(5, it.previous());
        assertEquals(1, it.next());
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
