package tasks;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import common.Util;
import common.Coord;
import common.EdgeCosts;
import common.Node;

public class TaskThreeOptimised {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    Map<String, Map<String, Double>> distWeightMap = Util.buildWeightMap("Dist.json");
    Map<String, Map<String, Double>> energyWeightMap = Util.buildWeightMap("Cost.json");
    Map<String, Coord> coordinatesMap = Util.buildCoordinateMap("Coord.json");

    final String root = "1";
    final String goal = "50";
    final double energyBudget = 287932;

    long startTime = System.nanoTime();
    Node meetingPoint = biDirectionalAStar(distWeightMap, energyWeightMap, coordinatesMap, root, goal, energyBudget);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;

    if (meetingPoint == null) {
      System.out.println("Path is not found!");
      return;
    }

    String path = Util.buildPathFromMeetingPoint(meetingPoint);
    System.out
        .println(
            "Shortest Path from node 1 to 50 with energy budget constraint, using eucledian distance heuristic, with bidirectional search");
    System.out.println("============ Task Three Optimised =============");
    System.out.println("Algorithm Runtime: " + duration + " ms");
    System.out.println("Total Distance Cost: " + df.format(meetingPoint.distFromRoot + meetingPoint.distFromGoal));
    System.out.println("Total Energy Cost: " + df.format(meetingPoint.energyFromRoot + meetingPoint.energyFromGoal));
    System.out.println("Shortest Path: \n" + path);
  }

  // biDirectionalAStar performs an A* search from root node to goal node with an
  // energy budget constraint. The heuristic function used is the euclidean
  // distance from node to the goal node and returns the meeting point node, else
  // returns null
  private static Node biDirectionalAStar(Map<String, Map<String, Double>> distWeightMap,
      Map<String, Map<String, Double>> energyWeightMap,
      Map<String, Coord> coordMap,
      String root, String goal, double energyBudget) {

    Coord rootNodeCoord = coordMap.get(root);
    Coord goalNodeCoord = coordMap.get(goal);

    // order in ascending f(x) = g(x) + h(x)
    PriorityQueue<Node> pq = new PriorityQueue<>(
        (a, b) -> (int) ((Util.f(a.getBidirDistCost(), coordMap.get(a.id), a.targetNodeCoord)
            - Util.f(b.getBidirDistCost(), coordMap.get(b.id), b.targetNodeCoord))
            % Integer.MAX_VALUE));

    Node rootNode = new Node(root);
    rootNode.distFromRoot = 0;
    rootNode.energyFromRoot = 0;
    rootNode.pathFromRoot = true;
    rootNode.targetNodeCoord = goalNodeCoord;
    pq.offer(rootNode);

    Node goalNode = new Node(goal);
    goalNode.distFromGoal = 0;
    goalNode.energyFromGoal = 0;
    goalNode.pathFromGoal = true;
    goalNode.targetNodeCoord = rootNodeCoord;
    // pq.offer(goalNode);

    Map<String, List<EdgeCosts>> visitedFromRoot = new HashMap<>();
    visitedFromRoot.put(rootNode.id, new ArrayList<>());
    EdgeCosts rootEC = new EdgeCosts(0d, 0d);
    rootEC.node = rootNode;
    visitedFromRoot.get(rootNode.id).add(rootEC);

    Map<String, List<EdgeCosts>> visitedFromGoal = new HashMap<>();
    visitedFromGoal.put(goalNode.id, new ArrayList<>());
    EdgeCosts goalEC = new EdgeCosts(0d, 0d);
    goalEC.node = goalNode;
    visitedFromGoal.get(goalNode.id).add(goalEC);

    Node meetingPoint = null;
    while (!pq.isEmpty()) {
      Node cur = pq.poll();
      // System.out.println("cur id: " + cur.id + " dist cost: " + cur.distFromRoot +
      // " energy cost: " + cur.energyFromRoot
      // + " has path to root: " + cur.pathFromRoot);

      // * we allow more than 1 visits to the same node, because they might have
      // different energy costs

      if (cur.pathFromRoot && cur.pathFromGoal) {
        if (meetingPoint == null || cur.getBidirDistCost() < meetingPoint.getBidirDistCost()) {
          meetingPoint = cur;
        }
      }

      for (String neighbourId : distWeightMap.get(cur.id).keySet()) {
        double newDistCost = -1, newEnergyCost = -1;

        Map<String, List<EdgeCosts>> visited = cur.pathFromRoot ? visitedFromRoot : visitedFromGoal;

        if (cur.pathFromRoot) { // forward direction
          newDistCost = cur.distFromRoot + distWeightMap.get(cur.id).get(neighbourId);
          newEnergyCost = cur.energyFromRoot + energyWeightMap.get(cur.id).get(neighbourId);
        } else {
          // small note to take dist from a -> b, instead of b -> a
          newDistCost = cur.distFromGoal + distWeightMap.get(neighbourId).get(cur.id);
          newEnergyCost = cur.energyFromGoal + energyWeightMap.get(neighbourId).get(cur.id);
        }
        EdgeCosts newEdgeCost = new EdgeCosts(newDistCost, newEnergyCost);
        visited.putIfAbsent(neighbourId, new ArrayList<>());

        if (newEnergyCost <= energyBudget
            && Util.hasPotential(newEdgeCost, visited.get(neighbourId))) {

          Node nextNode = new Node(neighbourId);
          // System.out.println("here: " + newDistCost + " " + newEnergyCost);
          nextNode.targetNodeCoord = cur.targetNodeCoord;

          if (cur.pathFromGoal) { // keep track of node links to rebuild path from goal node to source node
            nextNode.distFromGoal = newDistCost;
            nextNode.energyFromGoal = newEnergyCost;
            nextNode.nextToGoal = cur;
            nextNode.pathFromGoal = true;
          } else {
            nextNode.distFromRoot = newDistCost;
            nextNode.energyFromRoot = newEnergyCost;
            nextNode.parentFromRoot = cur;
            nextNode.pathFromRoot = true;
          }
          newEdgeCost.node = nextNode;

          // additional step to look for meeting point if exists
          // consider, the best possible option if multiple possible routes exist

          // boolean possible = false;
          Map<String, List<EdgeCosts>> visitedFromOtherEnd = cur.pathFromRoot ? visitedFromGoal : visitedFromRoot;
          visitedFromOtherEnd.putIfAbsent(nextNode.id, new ArrayList<>());
          for (EdgeCosts e : visitedFromOtherEnd.get(nextNode.id)) {
            if ((cur.pathFromRoot && nextNode.energyFromRoot + e.node.energyFromGoal <= energyBudget)
                || (cur.pathFromGoal && nextNode.energyFromGoal + e.node.energyFromRoot <= energyBudget)) {
              // possible = true;
              if (cur.pathFromRoot) {
                nextNode.distFromGoal = Math.min(nextNode.distFromGoal, e.node.distFromGoal);
                nextNode.energyFromGoal = Math.min(nextNode.energyFromGoal, e.node.energyFromGoal);
                nextNode.nextToGoal = e.node.nextToGoal;
                nextNode.pathFromGoal = true;
              } else {
                nextNode.distFromRoot = Math.min(nextNode.distFromRoot, e.node.distFromRoot);
                nextNode.energyFromRoot = Math.min(nextNode.energyFromRoot, e.node.energyFromRoot);
                nextNode.parentFromRoot = e.node.parentFromRoot;
                nextNode.pathFromRoot = true;
              }
            }
          }
          // if (possible) {
          // return nextNode;
          // }

          // this is optimal to allow "inbetweeners".
          // consider (distance, energy): (10, 2), (2, 10), (5, 5), (12, 5) all 4 should
          // be allowed, but (12, 12), (7, 7) are not as they are inferior to all other
          // combinations
          visited.get(neighbourId).add(newEdgeCost);

          // System.out.println("can visit next: " + nextNode.id + " next node costs: " +
          // nextNode.distFromRoot + " "
          // + nextNode.energyFromRoot);
          pq.offer(nextNode);
        }
      }
    }

    return meetingPoint;
  }
}
