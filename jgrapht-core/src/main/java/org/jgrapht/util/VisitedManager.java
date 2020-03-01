package org.jgrapht.util;

import java.util.HashMap;
import java.util.Map;

public class VisitedManager<T> {
    private final Map<T, Boolean> visited = new HashMap<>();

    public void visited(T element) {
        visited.put(element, true);
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
