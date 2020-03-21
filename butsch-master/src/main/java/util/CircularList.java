package util;

import jdk.jshell.spi.ExecutionControl;

import java.util.*;

public class CircularList<T extends Object> implements List<T> {
    private final List<T> list;

    public CircularList(final List<T> list) {
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        return list.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(final int index) {
        return list.get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return list.set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        list.add(index, element);
    }

    @Override
    public T remove(final int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        if (index >= list.size()) {
            throw new IndexOutOfBoundsException();
        }

        return new ListIterator<T>() {
            boolean restarted = false;
            ListIterator<T> iterator = list.listIterator(index);
            int popedElems = 0;
            final int maxElemsToPop = list.size();

            @Override
            public boolean hasNext() {
                return popedElems < maxElemsToPop;
            }

            @Override
            public T next() {
                if (iterator.hasNext()) {
                    popedElems++;
                    return iterator.next();
                } else if (!iterator.hasNext() && !restarted) {
                    iterator = list.listIterator(0);
                    popedElems++;
                    return iterator.next();
                } else {
                    throw new NoSuchElementException("There are no more elements");
                }
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public T previous() {
                return null;
            }

            @Override
            public int nextIndex() {
                return -1;
            }

            @Override
            public int previousIndex() {
                return -1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("no removing");
            }

            @Override
            public void set(final T t) {
                throw new UnsupportedOperationException("no setting");
            }

            @Override
            public void add(final T t) {
                throw new UnsupportedOperationException("no adding");
            }
        };
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
