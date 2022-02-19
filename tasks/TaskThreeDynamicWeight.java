package tasks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import common.Util;
import common.Coord;
import common.EdgeCosts;
import common.Node;

public class TaskThreeDynamicWeight {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Map<String, Double>> distWeightMap = Util.buildWeightMap("Dist.json");
    Map<String, Map<String, Double>> energyWeightMap = Util.buildWeightMap("Cost.json");
    Map<String, Coord> coordinatesMap = Util.buildCoordinateMap("Coord.json");

    final String root = "1";
    final String goal = "50";
    final double energyBudget = 287932;
    final double weight = 1.2;

    long startTime = System.nanoTime();
    Node goalNode = staticWeightAStar(distWeightMap, energyWeightMap,
        coordinatesMap, root, goal, energyBudget, weight);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    if (goalNode == null) {
      System.out.println("Path is not found!");
      return;
    }

    String path = Util.buildPath(goalNode);
    System.out.println(
        "Shortest Path from node 1 to 50 with energy budget constraint, using dynamic weight eucledian distance heuristic");
    System.out.println("============ Task Three Optimised =============");
    System.out.println("Algorithm Runtime: " + duration + " ms");
    System.out.println("Total Distance Cost: " + df.format(goalNode.distCost));
    System.out.println("Total Energy Cost: " + df.format(goalNode.energyCost));
    System.out.println("Shortest Path: \n" + path);
  }

  // staticWeightAStar performs a weighted A* search from root node to goal node
  // with an energy budget constraint. The heuristic function used is the
  // euclidean distance from node to the goal node and returns the goal node if a
  // path is found to it, otherwise returns null
  private static Node staticWeightAStar(Map<String, Map<String, Double>> distWeightMap,
      Map<String, Map<String, Double>> energyWeightMap,
      Map<String, Coord> coordMap,
      String root, String goal, double energyBudget, double weight) {

    Coord goalNodeCoord = coordMap.get(goal);
    // order in ascending f(x) = g(x) + w(x) * h(x)
    PriorityQueue<Node> pq = new PriorityQueue<>(
        (a, b) -> (int) ((dynamicWeightCostFunc(a.distCost, coordMap.get(a.id), goalNodeCoord, weight)
            - dynamicWeightCostFunc(b.distCost, coordMap.get(b.id), goalNodeCoord, weight))
            % Integer.MAX_VALUE));
    Node rootNode = new Node(root);
    pq.offer(rootNode);

    Map<String, List<EdgeCosts>> visited = new HashMap<>();
    visited.put(rootNode.id, new ArrayList<>());
    visited.get(rootNode.id).add(new EdgeCosts(0d, 0d));

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
        if (newEnergyCost <= energyBudget
            && Util.hasPotential(newEdgeCost, visited.getOrDefault(neighbourId, new ArrayList<>()))) {
          visited.putIfAbsent(neighbourId, new ArrayList<>());
          // this is optimal to allow "inbetweeners".
          // consider (distance, energy): (10, 2), (2, 10), (5, 5), (12, 5) all 4 should
          // be allowed, but (12, 12), (7, 7) are not as they are inferior to all other
          // combinations
          visited.get(neighbourId).add(newEdgeCost);

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

  private static double dynamicWeightCostFunc(double nodeDistCost, Coord nodeCoord, Coord goalCoord, double weight) {
    double eucledianDist = nodeCoord.calcEuclideanDistTo(goalCoord);
    return nodeDistCost < eucledianDist ? nodeDistCost + eucledianDist
        : (nodeDistCost + (2 * weight - 1) * eucledianDist) / weight;
  }
}
