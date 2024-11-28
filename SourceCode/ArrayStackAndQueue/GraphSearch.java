package csci2320;

import java.util.List;

public class GraphSearch {
  // Use this type in your queue.
  static record VertexDistancePair(int vertex, int distance) {}

  /**
   * This method takes an adjacency list and two vertices, start and end, and returns the length of the shortest path from start to end.
   * @param adj adjacency list representation of a graph
   * @param start index of the vertex to start at
   * @param end index of the vertex to end at
   * @return the length of the shortest path from start to end
   */
  public static int bfsShortestPath(List<List<Integer>> adj, int start, int end) {
    // You will use a queue in here.
    // Put your code here.
    return 3;  // This makes my one test pass. Change this according to your code.
  }
}
