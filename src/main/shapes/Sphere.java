package main.shapes;

import javafx.scene.paint.Color;
import main.util.Vector3;
import main.util.Intersection;
import main.util.Material;

public class Sphere implements Shape{

    double radius;
    Vector3 center;
    Material material;

    public Sphere(double radius, Vector3 center, Material material){
        this.radius = radius;
        this.center = center;
        this.material = material;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public Vector3 getCenter() {
        return center;
    }

    @Override
    public Material getMaterial(){
        return material;
    }

    @Override
    /**
     * Returns a data type "Intersection" which gives information about possible intersections of the ray with the
     * object.
     */
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        /* Dividing up the ray and the circle for use for the Citardauq-formula.*/

        double px = origin.getX();
        double py = origin.getY();
        double pz = origin.getZ();

        double vx = direction.getX();
        double vy = direction.getY();
        double vz = direction.getZ();

        double cx = center.getX();
        double cy = center.getY();
        double cz = center.getZ();

        double a = vx * vx + vy * vy + vz * vz;
        double b = 2*px*vx + 2*py*vy + 2*pz*vz - 2*vx*cx - 2*vy*cy - 2*vz*cz;
        double c = px * px + py * py + pz * pz + cx * cx + cy * cy + cz * cz - radius * radius;

        return new Intersection(a,b,c, this);
    }
}
