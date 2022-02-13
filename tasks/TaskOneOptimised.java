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

  private static final String root = "1";
  private static final String goal = "50";

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Node> graph = Util.buildGraph();

    PriorityQueue<Node> pq = new PriorityQueue<>(
        (a, b) -> (int) ((Math.min(a.distFromRoot, a.distFromGoal) - Math.min(b.distFromRoot, b.distFromGoal))
            % Integer.MAX_VALUE));
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
    visitedFromRoot.put(rootNode.id, (double) 0);

    Map<String, Double> visitedFromGoal = new HashMap<>();
    visitedFromGoal.put(goalNode.id, (double) 0);

    Node meetingPoint = null;
    while (!pq.isEmpty()) {
      Node cur = pq.poll();
      if (cur.pathFromRoot && cur.pathFromGoal) { // found optimal path connecting root and goal
        meetingPoint = cur;
        break;
      }
      Map<String, Double> visited = cur.pathFromRoot ? visitedFromRoot : visitedFromGoal;
      if (visited.get(cur.id) == -1) { // ensure every node is only expanded once
        continue;
      }
      visited.put(cur.id, (double) -1); // prevent edge case of 2 path with equal cost reaching same node, expanding the
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
          } else {
            n.distFromGoal = newDistCost;
            n.energyFromGoal = newEnergyCost;
            n.nextToGoal = cur;
          }
          pq.offer(n);

          n.pathFromRoot = n.pathFromRoot || cur.pathFromRoot;
          n.pathFromGoal = n.pathFromGoal || cur.pathFromGoal;
        }
      }
    }

    String path = Util.buildPathFromMeetingPoint(meetingPoint);
    System.out.println("Shortest Path from node 1 to 50");
    System.out.println("====== Task One Optimised Bidirectional UCS ======");
    System.out.println("Total Distance Cost: " + df.format(meetingPoint.distFromRoot + meetingPoint.distFromGoal));
    System.out.println("Total Energy Cost: " + df.format(meetingPoint.energyFromRoot + meetingPoint.energyFromGoal));
    System.out.println("Shortest Path: \n" + path);
  }
}
