package common;

public class Coord {
  public double lat;
  public double lon;

  public Coord(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  // avoid use of Math functions like pow
  public double calcEuclideanDistTo(Coord target) {
    if (target == null) {
      return 0;
    }
    double x = this.lat - target.lat;
    double y = this.lon - target.lon;
    return Math.sqrt(x * x + y * y);
  }
}
