package common;

public class EdgeCosts {
  public double distEdgeCost;
  public double energyEdgeCost;

  // only used for bidrectional to map back a pair of edge cost to the node it
  // belongs to
  public Node node;

  public EdgeCosts(double distEdgeCost, double energyEdgeCost) {
    this.distEdgeCost = distEdgeCost;
    this.energyEdgeCost = energyEdgeCost;

    this.node = null;
  }

  public boolean oneIsLessThan(EdgeCosts other) {
    return this.distEdgeCost < other.distEdgeCost || this.energyEdgeCost < other.energyEdgeCost;
  }
}