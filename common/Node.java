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

  public Node(String id) {
    this.id = id;
    this.neighbours = new HashMap<>();
    this.distCost = 0;
    this.energyCost = 0;
    this.parent = null;
  }
}