package csci2320;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazeToGraph {
  public static record MazeLocation(int row, int col) {
  }

  public static record GraphAndLocMap(List<List<Integer>> graph, Map<MazeLocation, Integer> locMap) {
  }

  /**
   * This method takes a maze as a grid if ints and produces an adjacency list
   * representation of the maze.
   * 
   * @param maze grid of values, 0 is a path and anything else is a wall
   * @return the adjacency list representation of the maze
   */
  public static GraphAndLocMap mazeToAdjacencyList(int[][] maze) {
    int[][] nodeNumbers = new int[maze.length][maze[0].length];
    Map<MazeLocation, Integer> locMap = new HashMap<>();
    int nodeCount = 0;
    for (int i = 0; i < nodeNumbers.length; i++) {
      for (int j = 0; j < nodeNumbers[i].length; j++) {
        if (maze[i][j] == 0) {
          nodeNumbers[i][j] = nodeCount;
          locMap.put(new MazeLocation(i, j), nodeCount);
          nodeCount++;
        } else {
          nodeNumbers[i][j] = -1;
        }
      }
    }
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < nodeNumbers.length; i++) {
      for (int j = 0; j < nodeNumbers[i].length; j++) {
        if (nodeNumbers[i][j] != -1) {
          List<Integer> neighbors = new ArrayList<>();
          if (i > 0 && nodeNumbers[i - 1][j] != -1) {
            neighbors.add(nodeNumbers[i - 1][j]);
          }
          if (i < nodeNumbers.length - 1 && nodeNumbers[i + 1][j] != -1) {
            neighbors.add(nodeNumbers[i + 1][j]);
          }
          if (j > 0 && nodeNumbers[i][j - 1] != -1) {
            neighbors.add(nodeNumbers[i][j - 1]);
          }
          if (j < nodeNumbers[i].length - 1 && nodeNumbers[i][j + 1] != -1) {
            neighbors.add(nodeNumbers[i][j + 1]);
          }
          adj.add(neighbors);
        }
      }
    }
    return new GraphAndLocMap(adj, locMap);
  }

  public static List<Integer> breadthFirstSearch(GraphAndLocMap GraphAndLocMap, int startVertex, int endVertex) {
    List<List<Integer>> graph = GraphAndLocMap.graph();
    Map<MazeLocation, Integer> locMap = GraphAndLocMap.locMap();

    List<Integer> visited = new ArrayList<>();
    ArrayQueue<VertexDistance> queue = new ArrayQueue<>();
    List<Integer> path = new ArrayList<>();

    queue.enqueue(new VertexDistance(startVertex, 0));
    visited.add(startVertex);

    while (!queue.isEmpty()) {
      VertexDistance current = queue.dequeue();
      int currentVertex = current.vertex();
      int distance = current.distance();

      path.add(currentVertex);

      if (currentVertex == endVertex) {
        return path;
      }

      for (int neighbor : graph.get(currentVertex)) {
        if (!visited.contains(neighbor)) {
          queue.enqueue(new VertexDistance(neighbor, distance + 1));
          visited.add(neighbor);
        }
      }
    }
    return Collections.emptyList();
  }

  public static record VertexDistance(int vertex, int distance) {
  }
}
