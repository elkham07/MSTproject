package mst;



import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonObject;

public class MSTAlgorithms {

    public static void generateSampleInput(String path) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray graphs = new JsonArray();


        JsonObject g1 = new JsonObject();
        g1.addProperty("id", 1);
        g1.add("nodes", new Gson().toJsonTree(Arrays.asList("A", "B", "C", "D", "E")));
        JsonArray edges1 = new JsonArray();
        edges1.add(new Gson().toJsonTree(new Edge("A", "B", 4)));
        edges1.add(new Gson().toJsonTree(new Edge("A", "C", 3)));
        edges1.add(new Gson().toJsonTree(new Edge("B", "C", 2)));
        edges1.add(new Gson().toJsonTree(new Edge("B", "D", 5)));
        edges1.add(new Gson().toJsonTree(new Edge("C", "D", 7)));
        edges1.add(new Gson().toJsonTree(new Edge("C", "E", 8)));
        edges1.add(new Gson().toJsonTree(new Edge("D", "E", 6)));
        g1.add("edges", edges1);
        graphs.add(g1);


        JsonObject g2 = new JsonObject();
        g2.addProperty("id", 2);
        g2.add("nodes", new Gson().toJsonTree(Arrays.asList("A", "B", "C", "D")));
        JsonArray edges2 = new JsonArray();
        edges2.add(new Gson().toJsonTree(new Edge("A", "B", 1)));
        edges2.add(new Gson().toJsonTree(new Edge("A", "C", 4)));
        edges2.add(new Gson().toJsonTree(new Edge("B", "C", 2)));
        edges2.add(new Gson().toJsonTree(new Edge("B", "D", 5)));
        edges2.add(new Gson().toJsonTree(new Edge("C", "D", 3)));
        g2.add("edges", edges2);
        graphs.add(g2);

        root.add("graphs", graphs);

        try (Writer w = new FileWriter(path)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, w);
        }
    }










    // ---------- Edge & DSU ----------
    public static class DSU {
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> rank = new HashMap<>();
        public int finds = 0, unions = 0;

        public DSU(Collection<String> elems) {
            for (String e : elems) {
                parent.put(e, e);
                rank.put(e, 0);
            }
        }

        public String find(String x) {
            finds++;
            String p = parent.get(x);
            if (!p.equals(x)) {
                p = find(p);
                parent.put(x, p);
            }
            return p;
        }

        public boolean union(String a, String b) {
            String ra = find(a), rb = find(b);
            if (ra.equals(rb)) return false;
            unions++;
            int rka = rank.get(ra), rkb = rank.get(rb);
            if (rka < rkb) parent.put(ra, rb);
            else if (rka > rkb) parent.put(rb, ra);
            else { parent.put(rb, ra); rank.put(ra, rka + 1); }
            return true;
        }
    }

    // ---------- Prim ----------
    public static class PrimResult {
        public List<Edge> mstEdges;
        public int totalCost;
        public long opComparisons;
        public long opDecreaseKey;
        public long heapOps;
        public double execMs;
        public boolean disconnected;
    }

    public static PrimResult prim(Graph g) {
        long t0 = System.nanoTime();
        long comparisons = 0, decreaseKey = 0, heapOps = 0;

        Map<String, List<Edge>> adj = new HashMap<>();
        for (String v : g.nodes) adj.put(v, new ArrayList<>());
        for (Edge e : g.edges) {
            adj.get(e.from).add(new Edge(e.from, e.to, e.weight));
            adj.get(e.to).add(new Edge(e.to, e.from, e.weight));
        }

        String start = g.nodes.get(0);
        Map<String, Integer> key = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> inMST = new HashSet<>();

        for (String v : g.nodes) key.put(v, Integer.MAX_VALUE);
        key.put(start, 0);

        PriorityQueue<NodeKey> pq = new PriorityQueue<>(Comparator.comparingInt(nk -> nk.key));
        pq.add(new NodeKey(start, 0, null)); heapOps++;

        while (!pq.isEmpty()) {
            NodeKey nk = pq.poll(); heapOps++;
            if (inMST.contains(nk.vertex)) { comparisons++; continue; }
            inMST.add(nk.vertex);
            parent.put(nk.vertex, nk.parent);

            for (Edge e : adj.get(nk.vertex)) {
                comparisons++;
                if (!inMST.contains(e.to) && e.weight < key.get(e.to)) {
                    key.put(e.to, e.weight);
                    parent.put(e.to, nk.vertex);
                    pq.add(new NodeKey(e.to, e.weight, nk.vertex)); heapOps++;
                    decreaseKey++;
                }
            }
        }

        long t1 = System.nanoTime();
        PrimResult res = new PrimResult();
        if (inMST.size() != g.nVertices()) {
            res.disconnected = true;
            res.mstEdges = null;
            res.totalCost = -1;
        } else {
            res.disconnected = false;
            res.mstEdges = new ArrayList<>();
            int cost = 0;
            for (String v : g.nodes) {
                String p = parent.get(v);
                if (p != null) {
                    int w = key.get(v);
                    res.mstEdges.add(new Edge(p, v, w));
                    cost += w;
                }
            }
            res.totalCost = cost;
        }

        res.opComparisons = comparisons;
        res.opDecreaseKey = decreaseKey;
        res.heapOps = heapOps;
        res.execMs = (t1 - t0) / 1e6;
        return res;
    }

    static class NodeKey {
        String vertex; int key; String parent;
        NodeKey(String v, int k, String p) { vertex = v; key = k; parent = p; }
    }

    // ---------- Kruskal ----------
    public static class KruskalResult {
        public List<Edge> mstEdges;
        public int totalCost;
        public long sortComparisonsEstimate;
        public long finds;
        public long unions;
        public double execMs;
        public boolean disconnected;
    }

    public static KruskalResult kruskal(Graph g) {
        long t0 = System.nanoTime();
        List<Edge> edges = new ArrayList<>(g.edges);
        edges.sort(Comparator.comparingInt(e -> e.weight));
        long sortEstimate = (long)(edges.size() * Math.log(Math.max(2, edges.size())) / Math.log(2));

        DSU dsu = new DSU(g.nodes);
        List<Edge> mst = new ArrayList<>();
        int cost = 0;

        for (Edge e : edges) {
            String ra = dsu.find(e.from), rb = dsu.find(e.to);
            if (!ra.equals(rb)) {
                dsu.union(ra, rb);
                mst.add(e);
                cost += e.weight;
                if (mst.size() == g.nVertices() - 1) break;
            }
        }

        long t1 = System.nanoTime();
        KruskalResult res = new KruskalResult();
        if (mst.size() != g.nVertices() - 1) {
            res.disconnected = true;
            res.mstEdges = null;
            res.totalCost = -1;
        } else {
            res.disconnected = false;
            res.mstEdges = mst;
            res.totalCost = cost;
        }

        res.sortComparisonsEstimate = sortEstimate;
        res.finds = dsu.finds;
        res.unions = dsu.unions;
        res.execMs = (t1 - t0) / 1e6;
        return res;
    }


    public static boolean isAcyclicAndConnected(List<Edge> treeEdges, Collection<String> vertices) {
        if (treeEdges == null) return false;
        DSU dsu = new DSU(vertices);
        for (Edge e : treeEdges) {
            if (!dsu.union(e.from, e.to)) return false;
        }
        String first = vertices.iterator().next();
        String comp = dsu.find(first);
        for (String v : vertices) if (!dsu.find(v).equals(comp)) return false;
        return true;
    }
}
