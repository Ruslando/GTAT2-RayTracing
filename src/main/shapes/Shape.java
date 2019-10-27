package main.shapes;

import javafx.scene.paint.Color;
import main.util.Vector3;
import main.util.Intersection;
import main.util.Material;

public interface Shape {

    Intersection intersect(Vector3 origin, Vector3 direction);

    Vector3 getCenter();

    Material getMaterial();

}
