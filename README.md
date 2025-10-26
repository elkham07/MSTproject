Analytical Report – Optimization of a City Transportation Network (MST)
1. Summary of Input Data and Algorithm Results

Two sample graphs were processed as input datasets. Both Prim’s and Kruskal’s algorithms were applied to compute the Minimum Spanning Tree (MST).

Input Graphs:

Graph 1: 5 vertices, 7 edges

Graph 2: 4 vertices, 5 edges

Results Table:

Graph ID	Vertices	Edges	Prim MST Cost	Kruskal MST Cost	Prim Time (ms)	Kruskal Time (ms)	Prim Operations	Kruskal Operations
1	5	7	16	16	0.881	0.379	40	44
2	4	5	6	6	0.032	0.012	29	28

Notes:

MST total costs match between Prim’s and Kruskal’s algorithms for each graph.

Operation counts and execution times are recorded.

2. Comparison of Prim’s and Kruskal’s Algorithms
   Criteria	Prim’s Algorithm	Kruskal’s Algorithm
   Graph Type Suitability	Dense graphs (many edges)	Sparse graphs (few edges)
   Time Complexity (theoretical)	O(V²) or O(E log V) (with heap)	O(E log E)
   Observed Execution Time	Slightly higher on dense graphs	Slightly lower on small/sparse graphs
   Key Operations	Comparisons, decrease-key, heap ops	Find and union operations
   MST Cost Accuracy	Accurate, matches Kruskal	Accurate, matches Prim

Observations:

For smaller graphs (4–5 vertices), both algorithms perform very quickly and differences are negligible.

For larger or denser graphs, Prim’s algorithm may involve more heap operations, while Kruskal is efficient if edges are fewer and sorting cost is manageable.

Both algorithms produce valid MSTs (acyclic and connected).

3. Conclusions

Correctness: Both Prim’s and Kruskal’s algorithms produced MSTs with identical total costs.

Performance:

Prim’s is preferable for dense graphs due to adjacency-list optimization.

Kruskal’s is preferable for sparse graphs with fewer edges.

Operations and Time: Metrics collected confirm algorithmic complexity and provide insight for optimization analysis.

Overall:I  successfully implemented and tested both algorithms with proper input/output handling, automated tests, and optional bonus tasks (Graph/Edge classes).