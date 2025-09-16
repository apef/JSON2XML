public class Lamp {
  int OBJECTID;
  int ID;
  String Owner;
  String vaghallare;

  int[] geometry;
 
  private static class Geometry {
    float X;
    float Y;

    Geometry(float newX, float newY) {
      this.X = newX;
      this.Y = newY;
    }
  }
}
  //  "attributes": {
  //       "OBJECTID": 2227910,
  //       "ID": "7910",
  //       "Owner": "Kalmar Energi",
  //       "Vaghallare": "Kalmar kommun"
  //     },
  //     "geometry": {
  //       "x": 140767.4570000004,
  //       "y": 6287207.636700001
  //     }