/*
 * (C) Copyright 2018-2018, by Alexandru Valeanu and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */package org.jgrapht.alg.spanning;

import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.util.IntegerVertexFactory;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.jgrapht.alg.spanning.MinimumSpanningTreeTest.*;
import static org.junit.Assert.assertEquals;

public class PrimMinimumSpanningTreeTest {

    @Test
    public void testRandomInstances()
    {
        final Random rng = new Random(33);
        final double edgeProbability = 0.5;
        final int numberVertices = 100;
        final int repeat = 200;

        GraphGenerator<Integer, DefaultWeightedEdge, Integer> gg =
                new GnpRandomGraphGenerator<>(
                        numberVertices, edgeProbability, rng, false);

        for (int i = 0; i < repeat; i++) {
            WeightedPseudograph<Integer, DefaultWeightedEdge> g =
                    new WeightedPseudograph<>(DefaultWeightedEdge.class);
            gg.generateGraph(g, new IntegerVertexFactory(), null);

            for (DefaultWeightedEdge e : g.edgeSet()) {
                g.setEdgeWeight(e, rng.nextDouble());
            }

            SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> tree1 = new KruskalMinimumSpanningTree<>(g).getSpanningTree();
            SpanningTreeAlgorithm.SpanningTree<DefaultWeightedEdge> tree2 = new PrimMinimumSpanningTree<>(g).getSpanningTree();

            assertEquals(tree1.getWeight(), tree2.getWeight(), 1e-9);
        }
    }

    @Test
    public void testPrim()
    {
        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        MinimumSpanningTreeTest.createSimpleConnectedWeightedGraph()).getSpanningTree(),
                Arrays.asList(AB, AC, BD, DE), 15.0);

        testMinimumSpanningTreeBuilding(
                new PrimMinimumSpanningTree<>(
                        createSimpleDisconnectedWeightedGraph()).getSpanningTree(),
                Arrays.asList(AB, AC, BD, EG, GH, FH), 60.0);
    }
}