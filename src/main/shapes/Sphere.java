package main.shapes;

import javafx.scene.paint.Color;
import main.util.Vector3;
import main.util.Intersection;

public class Sphere implements Shape{

    double radius;
    Vector3 center;
    Color localcolor;

    public Sphere(double radius, Vector3 center, Color color){
        this.radius = radius;
        this.center = center;
        this.localcolor = color;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public Vector3 getCenter() {
        return center;
    }

    @Override
    public Color getColor(){
        return localcolor;
    }

    @Override
    /**
     * Returns a data type "Intersection" which gives information about possible intersections of the ray with the
     * object.
     */
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        /* Dividing up the ray and the circle for use for the abc-formula*/
        double a = direction.vectorproduct(direction);
        Vector3 eyecenter = origin.subtract(center);
        double b = 2 * direction.vectorproduct(eyecenter);
        double c = eyecenter.vectorproduct(eyecenter) - radius*radius;

        return new Intersection(a,b,c, this);
    }
}
