package csci2320;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinimumSpanningTree {
  /**
   * This represents a weighted edge in a graph.
   */
  public static record Edge(int v1, int v2, double weight) {
  }

  /**
   * Runs Kruskal's algorithm to find the minimum spanning tree is a weighted,
   * undirected
   * graph. The input graph can be assumed to have vertices numbered from 0 to
   * numVertices-1
   * and in every edge, v1 < v2.
   * 
   * To make sure your output agrees with the test output,
   * have the return edges go from lowest to highest weight. Note, that is the
   * natural order
   * that will result from an implementation and shouldn't require extra work.
   * 
   * @param numVertices the number of vertices in the graph
   * @param graph       the edges in the graph
   * @return a list of the edges in the minimum spanning tree
   */
  public static List<Edge> kruskals(int numVertices, List<Edge> graph) {
    Collections.sort(graph, (e1, e2) -> Double.compare(e1.weight(), e2.weight()));
    List<DisjointSet<Integer>> sets = new ArrayList<>();
    for (int i = 0; i < numVertices; i++) {
      sets.add(DisjointSet.makeSet(i));
    }
    List<Edge> mstEdges = new ArrayList<>();
    int edgesAdded = 0;
    int edgeI = 0;
    while (edgesAdded < numVertices - 1 && edgeI < graph.size()) {
      Edge edge = graph.get(edgeI++);
      DisjointSet<Integer> set1 = sets.get(edge.v1());
      DisjointSet<Integer> set2 = sets.get(edge.v2());
      if (set1.findSet() != set2.findSet()) {
        mstEdges.add(edge);
        edgesAdded++;
        set1.union(set2);
      }
    }

    return mstEdges;
  }
}
