package tasks;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import common.Node;
import common.Util;

public class TaskOneOptimised {
  /*
   * This section is a completely optional implementation of bidirectional UCS to
   * observe improvement to runtime performance.
   */

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Node> graph = Util.buildGraph();

    final String root = "1";
    final String goal = "50";

    long startTime = System.nanoTime();
    Node meetingPoint = bidirectionalDistUCS(graph, root, goal);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    if (meetingPoint == null) {
      System.out.println("Path is not found!");
      return;
    }

    String path = Util.buildPathFromMeetingPoint(meetingPoint);
    System.out.println("Shortest Path from node 1 to 50");
    System.out.println("====== Task One Optimised Bidirectional UCS ======");
    System.out.println("Algorithm Runtime: " + duration + " ms");
    System.out.println("Total Distance Cost: " + df.format(meetingPoint.distFromRoot + meetingPoint.distFromGoal));
    System.out.println("Total Energy Cost: " + df.format(meetingPoint.energyFromRoot + meetingPoint.energyFromGoal));
    System.out.println("Shortest Path: \n" + path);
  }

  // bidirectionalDistUCS performs bidirectional UCS from source and goal node
  // simulatenously. Returns the meeting point of both search if a path exists,
  // otherwise returns null
  private static Node bidirectionalDistUCS(Map<String, Node> graph, String root, String goal) {
    PriorityQueue<Node> pq = new PriorityQueue<>(
        (a, b) -> (int) ((a.getBidirDistCost() - b.getBidirDistCost()) % Integer.MAX_VALUE));
    Node rootNode = graph.get(root);
    Node goalNode = graph.get(goal);
    rootNode.distFromRoot = 0;
    rootNode.energyFromRoot = 0;
    rootNode.pathFromRoot = true;

    goalNode.distFromGoal = 0;
    goalNode.energyFromGoal = 0;
    goalNode.pathFromGoal = true;

    pq.offer(rootNode);
    pq.offer(goalNode);

    Map<String, Double> visitedFromRoot = new HashMap<>();
    visitedFromRoot.put(rootNode.id, 0d);

    Map<String, Double> visitedFromGoal = new HashMap<>();
    visitedFromGoal.put(goalNode.id, 0d);

    while (!pq.isEmpty()) {
      Node cur = pq.poll();
      if (cur.pathFromRoot && cur.pathFromGoal) { // found optimal path connecting root and goal
        return cur;
      }
      Map<String, Double> visited = cur.pathFromRoot ? visitedFromRoot : visitedFromGoal;
      if (visited.get(cur.id) == -1d) { // ensure every node is only expanded once
        continue;
      }
      visited.put(cur.id, -1d); // prevent edge case of 2 path with equal cost reaching same node, expanding the
                                // same node twice

      for (Node n : cur.neighbours.keySet()) {
        // to handle edge case of a -> b having a different cost of b -> a, which is
        // never the case but just in case
        Double newDistCost = cur.pathFromRoot ? cur.distFromRoot + cur.neighbours.get(n).distEdgeCost
            : cur.distFromGoal + n.neighbours.get(cur).distEdgeCost;
        Double newEnergyCost = cur.pathFromRoot ? cur.energyFromRoot + cur.neighbours.get(n).energyEdgeCost
            : cur.energyFromGoal + n.neighbours.get(cur).energyEdgeCost;

        if (newDistCost < visited.getOrDefault(n.id, Double.MAX_VALUE)) {
          visited.put(n.id, newDistCost);
          if (cur.pathFromRoot) {
            n.distFromRoot = newDistCost;
            n.energyFromRoot = newEnergyCost;
            n.parentFromRoot = cur; // keep track of parent node to rebuild path from goal node to source node

            n.pathFromRoot = true;
          } else {
            n.distFromGoal = newDistCost;
            n.energyFromGoal = newEnergyCost;
            n.nextToGoal = cur;

            n.pathFromGoal = true;
          }
          pq.offer(n);

          // n.pathFromRoot = n.pathFromRoot || cur.pathFromRoot;
          // n.pathFromGoal = n.pathFromGoal || cur.pathFromGoal;
        }
      }
    }

    return null;
  }
}
