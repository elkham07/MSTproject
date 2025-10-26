package mst;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws Exception {

        InputStream is = Main.class.getClassLoader().getResourceAsStream("input.json");
        if (is == null) throw new FileNotFoundException("Resource input.json not found");

        Reader reader = new InputStreamReader(is);
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();




        JsonArray graphsArray = root.getAsJsonArray("graphs");

        List<Map<String,Object>> csvRows = new ArrayList<>();
        List<Map<String,Object>> outputs = new ArrayList<>();

        for (JsonElement gEl : graphsArray) {
            JsonObject gObj = gEl.getAsJsonObject();
            int id = gObj.get("id").getAsInt();

            Type nodesT = new TypeToken<List<String>>(){}.getType();
            List<String> nodes = gson.fromJson(gObj.get("nodes"), nodesT);

            Type edgesT = new TypeToken<List<Edge>>(){}.getType();
            List<Edge> edges = gson.fromJson(gObj.get("edges"), edgesT);

            Graph g = new Graph(id, nodes, edges);


            MSTAlgorithms.PrimResult pr = MSTAlgorithms.prim(g);
            MSTAlgorithms.KruskalResult kr = MSTAlgorithms.kruskal(g);

            Map<String,Object> result = new LinkedHashMap<>();
            result.put("graph_id", id);
            result.put("vertices", g.nVertices());
            result.put("edges", g.nEdges());
            result.put("prim_total_cost", pr.totalCost);
            result.put("kruskal_total_cost", kr.totalCost);
            result.put("prim_time_ms", pr.execMs);
            result.put("kruskal_time_ms", kr.execMs);
            result.put("prim_operations", pr.opComparisons + pr.opDecreaseKey + pr.heapOps);
            result.put("kruskal_operations", kr.finds + kr.unions + kr.sortComparisonsEstimate);
            outputs.add(result);
            csvRows.add(result);
        }


        try (Writer w = new FileWriter("src/main/resources/output.json")) {
            gson.toJson(Map.of("results", outputs), w);
        }


        try (PrintWriter pw = new PrintWriter(new FileWriter("src/main/resources/summary.csv"))) {
            String[] headers = {"graph_id","vertices","edges","prim_total_cost","kruskal_total_cost","prim_time_ms","kruskal_time_ms","prim_operations","kruskal_operations"};
            pw.println(String.join(",", headers));
            for (Map<String,Object> row : csvRows) {
                List<String> vals = new ArrayList<>();
                for (String h : headers)
                    vals.add(String.valueOf(row.getOrDefault(h, "")));
                pw.println(String.join(",", vals));
            }
        }

        System.out.println("✅ Results written to output.json and summary.csv");
    }
}
