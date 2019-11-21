package main.shapes;

import main.util.Vector3;
import main.util.Intersection;
import main.shader.Material;

public interface Shape {

    Intersection intersect(Vector3 origin, Vector3 direction);

    Material getMaterial();

    Vector3 getNormal(Vector3 point);

    void translate(double x, double y, double z);

    void rotate(double phi);

    void scale(double x, double y, double z);
}
