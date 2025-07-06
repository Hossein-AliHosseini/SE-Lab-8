package org.example.graphTravelers;

import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphAdapter<V> implements Graph<V> {
    private final SimpleDirectedGraph<V, String> graph;

    public GraphAdapter() {
        this.graph = new SimpleDirectedGraph<>(String.class);
    }

    @Override
    public void addVertex(V vertex) {
        graph.addVertex(vertex);
    }

    @Override
    public void addEdge(String name, V source, V destination) {
        graph.addEdge(source, destination, name);
    }

    @Override
    public List<V> getNeighbors(V vertex) {
        List<V> neighbors = new ArrayList<>();
        for (String edge : graph.outgoingEdgesOf(vertex)) {
            neighbors.add(graph.getEdgeTarget(edge));
        }
        for (String edge : graph.incomingEdgesOf(vertex)) {
            neighbors.add(source);
        }
        return neighbors;
    }
}