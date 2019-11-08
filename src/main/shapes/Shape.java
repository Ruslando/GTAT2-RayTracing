package main.shapes;

import main.util.Vector3;
import main.util.Intersection;
import main.util.Material;

public interface Shape {

    Intersection intersect(Vector3 origin, Vector3 direction);

    Material getMaterial();

}
