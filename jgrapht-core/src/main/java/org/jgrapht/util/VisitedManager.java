package org.jgrapht.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VisitedManager<T> {
    private final Map<T, Boolean> visited = new HashMap<>();

    public void visited(T element) {
        visited.put(element, true);
    }

    public void visited(Collection<T> elements) {
        for (final T element : elements) {
            visited(element);
        }
    }

    public boolean isVisited(T element) {
        Boolean mapResult = visited.get(element);

        if (mapResult == null || mapResult.equals(false)) {
            return false;
        } else {
            return true;
        }
    }
}
