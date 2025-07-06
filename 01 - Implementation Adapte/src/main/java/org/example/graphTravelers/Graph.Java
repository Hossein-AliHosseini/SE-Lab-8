package org.example.graphTravelers;

import java.util.List;

public interface Graph<V> {
    void addVertex(V vertex);
    void addEdge(String name, V source, V destination);
    List<V> getNeighbors(V vertex);
}