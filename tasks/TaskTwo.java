package tasks;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import common.Util;
import common.EdgeCosts;
import common.Node;

public class TaskTwo {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Map<String, Double>> distWeightMap = Util.buildWeightMap("Dist.json");
    Map<String, Map<String, Double>> energyWeightMap = Util.buildWeightMap("Cost.json");

    final String root = "1";
    final String goal = "50";
    final double energyBudget = 287932;

    long startTime = System.nanoTime();
    Node goalNode = ucs(distWeightMap, energyWeightMap, root, goal, energyBudget);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    if (goalNode == null) {
      System.out.println("Path is not found!");
      return;
    }

    String path = Util.buildPath(goalNode);
    System.out.println("Shortest Path from node 1 to 50 with energy budget constraint");
    System.out.println("============ Task Two =============");
    System.out.println("Algorithm Runtime: " + duration + " ms");
    System.out.println("Total Distance Cost: " + df.format(goalNode.distCost));
    System.out.println("Total Energy Cost: " + df.format(goalNode.energyCost));
    System.out.println("Shortest Path: \n" + path);
  }

  // ucs performs a simple uniform cost search from root node to goal node with an
  // energy budget constraint and returns the goal node if a path is found to it,
  // otherwise returns null
  private static Node ucs(Map<String, Map<String, Double>> distWeightMap,
      Map<String, Map<String, Double>> energyWeightMap, String root, String goal, double energyBudget) {
    PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> (int) ((a.distCost - b.distCost) % Integer.MAX_VALUE));
    Node rootNode = new Node("1");
    pq.offer(rootNode);

    Map<String, EdgeCosts> visited = new HashMap<>();
    visited.put(rootNode.id, new EdgeCosts(0d, 0d));

    // Set<String> set = new HashSet<>();

    while (!pq.isEmpty()) {
      Node cur = pq.poll();
      if (cur.id.equals(goal)) { // found optimal path to goal node 50
        return cur;
      }
      // * we allow more than 1 visits to the same node, because they might have
      // different energy costs

      for (String neighbourId : distWeightMap.get(cur.id).keySet()) {
        double newDistCost = cur.distCost + distWeightMap.get(cur.id).get(neighbourId);
        double newEnergyCost = cur.energyCost + energyWeightMap.get(cur.id).get(neighbourId);
        EdgeCosts newEdgeCost = new EdgeCosts(newDistCost, newEnergyCost);
        if (newEnergyCost <= energyBudget && newEdgeCost.oneIsLessThan(
            visited.getOrDefault(neighbourId, new EdgeCosts(Double.MAX_VALUE, Double.MAX_VALUE)))) {
          // if (newEnergyCost <= energyBudget && ?????) {

          if (visited.containsKey(neighbourId)) {
            // track the maximums, this is optimal to allow "inbetweeners".
            // consider (distance, energy): (10, 2), (2, 10), (5, 5), (12, 5) all 4 should
            // be allowed, but (12, 12), (7, 7) are not as they are inferior to all other
            // combinations
            visited.get(neighbourId).distEdgeCost = Math.max(visited.get(neighbourId).distEdgeCost, newDistCost);
            visited.get(neighbourId).energyEdgeCost = Math.max(visited.get(neighbourId).energyEdgeCost, newEnergyCost);
          } else {
            visited.put(neighbourId, newEdgeCost);
          }
          Node nextNode = new Node(neighbourId);

          nextNode.distCost = newDistCost;
          nextNode.energyCost = newEnergyCost;
          pq.offer(nextNode);
          nextNode.parent = cur; // keep track of parent node to rebuild path from goal node to source node
        }
      }
    }

    return null;
  }
}
