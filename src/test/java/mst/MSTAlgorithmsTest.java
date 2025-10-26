package mst;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.io.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class MSTAlgorithmsTest {
    @Test
    public void testSmallExampleMatchesCosts() throws Exception {

        MSTAlgorithms.generateSampleInput("temp_input.json");
        Reader r = new FileReader("temp_input.json");
        JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
        JsonArray arr = root.getAsJsonArray("graphs");
        for (JsonElement e : arr) {
            JsonObject g = e.getAsJsonObject();
            int id = g.get("id").getAsInt();
            List<String> nodes = new Gson().fromJson(g.get("nodes"), new TypeToken<List<String>>(){}.getType());
            List<Edge> edges = new Gson().fromJson(g.get("edges"), new TypeToken<List<Edge>>(){}.getType());
            Graph graph = new Graph(id, nodes, edges);

            MSTAlgorithms.PrimResult pr = MSTAlgorithms.prim(graph);
            MSTAlgorithms.KruskalResult kr = MSTAlgorithms.kruskal(graph);

            if (!pr.disconnected && !kr.disconnected) {
                assertEquals(pr.totalCost, kr.totalCost, "Costs must match for graph " + id);
                assertEquals(graph.nVertices()-1, pr.mstEdges.size(), "Prim edge count correct");
                assertEquals(graph.nVertices()-1, kr.mstEdges.size(), "Kruskal edge count correct");

                assertTrue(MSTAlgorithms.isAcyclicAndConnected(pr.mstEdges, graph.nodes));
                assertTrue(MSTAlgorithms.isAcyclicAndConnected(kr.mstEdges, graph.nodes));

                assertTrue(pr.execMs >= 0);
                assertTrue(kr.execMs >= 0);
                assertTrue(pr.opComparisons + pr.opDecreaseKey + pr.heapOps >= 0);
                assertTrue(kr.finds + kr.unions + kr.sortComparisonsEstimate >= 0);
            } else {

                assertTrue(pr.disconnected || pr.mstEdges == null);
                assertTrue(kr.disconnected || kr.mstEdges == null);
            }
        }
    }
}
