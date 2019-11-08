package main.shapes;
import main.util.Vector3;
import main.util.Intersection;
import main.util.Material;

public class Sphere implements Shape{

    private double radius;
    private Vector3 center;
    private Material material;

    public Sphere(double radius, Vector3 center, Material material){
        this.radius = radius;
        this.center = center;
        this.material = material;
    }

    public double getRadius() {
        return radius;
    }

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

        double a = direction.vectorproduct(direction);
        Vector3 eyecenter = origin.subtract(center);
        double b = 2 * direction.vectorproduct(eyecenter);
        double c = eyecenter.vectorproduct(eyecenter) - radius*radius;

        return new Intersection(a,b,c, this);
    }
}
