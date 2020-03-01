package org.jgrapht.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VisitedManagerTest {
    @Test
    public void testNothingVisited() {
        final VisitedManager<Integer> visitedManager = new VisitedManager<>();

        assertFalse(visitedManager.isVisited(2));
    }

    @Test
    public void testVisitedOneElement() {
        final VisitedManager<Integer> visitedManager = new VisitedManager<>();

        visitedManager.visited(2);

        assertTrue(visitedManager.isVisited(2));
        assertFalse(visitedManager.isVisited(1));
    }

    @Test
    public void testVisitedSecondElement() {
        final VisitedManager<Integer> visitedManager = new VisitedManager<>();

        visitedManager.visited(2);
        assertTrue(visitedManager.isVisited(2));
        assertFalse(visitedManager.isVisited(1));
        assertFalse(visitedManager.isVisited(0));

        visitedManager.visited(1);
        assertTrue(visitedManager.isVisited(2));
        assertTrue(visitedManager.isVisited(1));
        assertFalse(visitedManager.isVisited(0));
    }

    @Test
    public void testVisitedSecondElementList() {
        final VisitedManager<Integer> visitedManager = new VisitedManager<>();

        visitedManager.visited(Arrays.asList(2,1));
        assertTrue(visitedManager.isVisited(2));
        assertTrue(visitedManager.isVisited(1));
        assertFalse(visitedManager.isVisited(0));
    }
}
