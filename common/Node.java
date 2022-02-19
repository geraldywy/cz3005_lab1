package common;

import java.util.*;

public class Node {
  public String id;
  public Map<Node, EdgeCosts> neighbours;

  // assumes the largest cost fits in a 64 bit integer and there are no floating
  // weights, only integers.
  public double distCost;
  public double energyCost;

  public Node parent; // keep reference to parent to help build final result path

  // below fields are used for bidirectional searches
  public boolean pathFromRoot;
  public boolean pathFromGoal;
  public double distFromRoot;
  public double distFromGoal;
  public double energyFromRoot;
  public double energyFromGoal;
  public Node parentFromRoot;
  public Node nextToGoal;
  public Coord targetNodeCoord;

  public Node(String id) {
    this.id = id;
    this.neighbours = new HashMap<>();
    this.distCost = 0;
    this.energyCost = 0;
    this.parent = null;

    this.pathFromRoot = false;
    this.pathFromGoal = false;
    this.distFromRoot = Double.MAX_VALUE;
    this.distFromGoal = Double.MAX_VALUE;
    this.energyFromRoot = Double.MAX_VALUE;
    this.energyFromGoal = Double.MAX_VALUE;

    this.parentFromRoot = null;
    this.nextToGoal = null;

    this.targetNodeCoord = null;
  }

  // for bidirectional use only
  public double getBidirDistCost() {
    double rootDist = this.distFromRoot == Double.MAX_VALUE ? 0 : distFromRoot;
    double goalDist = this.distFromGoal == Double.MAX_VALUE ? 0 : distFromGoal;

    return rootDist + goalDist;
  }
}