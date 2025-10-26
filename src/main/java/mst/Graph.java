package mst;

import java.util.*;

public class Graph {
    public final int id;
    public final List<String> nodes;
    public final List<Edge> edges;

    public Graph(int id, List<String> nodes, List<Edge> edges) {
        this.id = id;
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);

        for (Edge e : edges) {
            if (!this.nodes.contains(e.from)) this.nodes.add(e.from);
            if (!this.nodes.contains(e.to)) this.nodes.add(e.to);
        }
    }

    public int nVertices() { return nodes.size(); }
    public int nEdges() { return edges.size(); }
}
