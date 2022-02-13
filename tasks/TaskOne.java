package tasks;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import common.Util;
import common.Node;

public class TaskOne {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Node> graph = Util.buildGraph();

    final String root = "1";
    final String goal = "50";

    long startTime = System.nanoTime();
    Node goalNode = ucs(graph, root, goal);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    if (goalNode == null) {
      System.out.println("Path is not found!");
      return;
    }

    String path = Util.buildPath(goalNode);
    System.out.println("Shortest Path from node 1 to 50");
    System.out.println("============ Task One =============");
    System.out.println("Algorithm Runtime: " + duration + " ms");
    System.out.println("Total Distance Cost: " + df.format(goalNode.distCost));
    System.out.println("Total Energy Cost: " + df.format(goalNode.energyCost));
    System.out.println("Shortest Path: \n" + path);
  }

  // UCS performs a simple uniform cost search from root node to goal node and
  // returns the goal node if a path is found to it, otherwise returns null
  private static Node ucs(Map<String, Node> graph, String root, String goal) {
    PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (int) ((a.distCost - b.distCost) % Integer.MAX_VALUE));
    Node rootNode = graph.get(root);
    pq.offer(rootNode);

    Map<String, Double> visited = new HashMap<>();
    visited.put(rootNode.id, (double) 0);

    while (!pq.isEmpty()) {
      Node cur = pq.poll();
      if (cur.id.equals(goal)) { // found optimal path to goal node 50
        break;
      }
      if (visited.get(cur.id) == -1) { // ensure every node is only expanded once
        continue;
      }

      visited.put(cur.id, (double) -1); // prevent edge case of 2 path with equal cost reaching same node, expanding the
                                        // same node twice
      for (Node n : cur.neighbours.keySet()) {
        Double newDistCost = cur.distCost + cur.neighbours.get(n).distEdgeCost;
        Double newEnergyCost = cur.energyCost + cur.neighbours.get(n).energyEdgeCost;
        if (newDistCost < visited.getOrDefault(n.id, Double.MAX_VALUE)) {
          visited.put(n.id, newDistCost);
          n.distCost = newDistCost;
          n.energyCost = newEnergyCost;
          pq.offer(n);
          n.parent = cur; // keep track of parent node to rebuild path from goal node to source node
        }
      }
    }

    if (!visited.containsKey(goal)) {
      return null;
    }

    return graph.get(goal);
  }
}
