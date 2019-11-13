package main.shapes;

import main.util.Intersection;
import main.util.Material;
import main.util.Vector3;

public class Vereinigung implements Shape{
    private Shape a;
    private Shape b;
    private Material material;

    public Vereinigung(Shape q1, Shape q2) {
        a = q1;
        b = q2;
        material = q1.getMaterial();
    }

    @Override
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        Intersection i1 = a.intersect(origin, direction);
        Intersection i2 = b.intersect(origin, direction);
        if(i1.getNearestIntersection() == Double.MAX_VALUE && i2.getNearestIntersection() == Double.MAX_VALUE) return i1;
        else if (i1.getNearestIntersection() != Double.MAX_VALUE && i2.getNearestIntersection() == Double.MAX_VALUE) return i1;
        else if (i2.getNearestIntersection() != Double.MAX_VALUE && i1.getNearestIntersection() == Double.MAX_VALUE) return i2;
        else if(i1.getNearestIntersection() <= i2.getNearestIntersection()) {
            return i1;
        }
        return i2;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public Vector3 getNormal(Vector3 point) {
        return b.getNormal(point);
    }

    @Override
    public void translate(double x, double y, double z) {
        a.translate(x,y,z);
        b.translate(x,y,z);
    }

    @Override
    public void rotate(double phi) {
        a.rotate(phi);
        b.rotate(phi);
    }

    @Override
    public void scale(double x, double y, double z) {
        a.scale(x,y,z);
        b.scale(x,y,z);
    }
}
