package main.shapes;

import main.util.Intersection;
import main.util.Material;
import main.util.Vector3;

import java.util.Arrays;

public class Differenz implements Shape {
    private Shape a;
    private Shape b;
    private Material material;

    public Differenz(Shape q1, Shape q2) {
        a = q1;
        b = q2;
        material = q1.getMaterial();
    }

    @Override
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        double[] intersections = new double[4];
        Intersection i1 = a.intersect(origin, direction);
        Intersection i2 = b.intersect(origin, direction);
        if(i1.intersections == 2 && i2.intersections == 2) {
            intersections[0] = i1.x1;
            intersections[1] = i1.x2;
            intersections[2] = i2.x1;
            intersections[3] = i2.x2;
            Arrays.sort(intersections);
            if(intersections[0] == i1.x1) {
                return i1;
            }
            else if(intersections[0] == i2.x1 ) {
                if(intersections[1] == i1.x1 && intersections[2] == i2.x2) {
                    return i2;
                }
                else if(intersections[1] == i2.x2) {
                    return i1;
                }
            }
        }
        return new Intersection(0,0,0, i1.shape);
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