package org.example.graphTravelers;

import edu.uci.ics.jung.graph.SparseMultigraph;
import java.util.ArrayList;
import java.util.List;

public class GraphAdaptor<V> implements Graph<V> {
    private final SparseMultigraph<V, String> graph;

    public GraphAdaptor() {
        this.graph = new SparseMultigraph<>();
    }

    @Override
    public void addVertex(V vertex) {
        graph.addVertex(vertex);
    }

    @Override
    public void addEdge(String name, V source, V destination) {
        graph.addEdge(edgeName, source, destination);
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        return new ArrayList<>(graph.getNeighbors(vertex));
    }