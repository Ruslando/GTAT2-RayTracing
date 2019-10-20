package sample.shapes;

import javafx.scene.paint.Color;
import sample.Vector3;
import util.Intersection;

public interface Shape {

    Intersection intersect(Vector3 origin, Vector3 direction);

    Vector3 getCenter();

    Color getColor();

}
