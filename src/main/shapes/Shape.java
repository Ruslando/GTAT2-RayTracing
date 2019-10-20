package main.shapes;

import javafx.scene.paint.Color;
import main.util.Vector3;
import main.util.Intersection;

public interface Shape {

    Intersection intersect(Vector3 origin, Vector3 direction);

    Vector3 getCenter();

    Color getColor();

}
