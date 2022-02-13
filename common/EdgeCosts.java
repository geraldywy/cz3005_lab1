package common;

public class EdgeCosts {
  public double distEdgeCost;
  public double energyEdgeCost;

  public EdgeCosts(double distEdgeCost, double energyEdgeCost) {
    this.distEdgeCost = distEdgeCost;
    this.energyEdgeCost = energyEdgeCost;
  }

  public boolean oneIsLessThan(EdgeCosts other) {
    return this.distEdgeCost < other.distEdgeCost || this.energyEdgeCost < other.energyEdgeCost;
  }
}