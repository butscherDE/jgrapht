package util;

import data.Node;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class lala {
    @Test
    public void lala() {
        final List<Node> nodes = Arrays.asList(new Node(0, 0, 0, 0),
                new Node(1, 1, 1, 1),
                new Node(2, 2, 2, 2),
                new Node(3, 3, 3, 3));

        final int evenIdent = 0;
        final List<Double> collect = nodes.stream()
                                          .filter(a -> a.id % 2 == evenIdent)
                                          .map(a -> a.longitude)
                                          .collect(Collectors.toList());


        assertEquals(Arrays.asList(0d, 2d), collect);
    }
}
