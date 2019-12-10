package main.shapes;
import main.util.Vector3;
import main.util.Intersection;
import main.shader.Material;

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

        double a = direction.vectormultiplication(direction);
        Vector3 eyecenter = origin.subtract(center);
        double b = 2 * direction.vectormultiplication(eyecenter);
        double c = eyecenter.vectormultiplication(eyecenter) - radius*radius;

        return new Intersection(a,b,c, this);
    }

    @Override
    public Vector3 getNormal(Vector3 point) {
        return point.subtract(center).scalarmultiplication(1.0/radius);
    }

    @Override
    public void translate(double x, double y, double z) {

    }

    @Override
    public void rotate(double phi) {

    }

    @Override
    public void scale(double x, double y, double z) {

    }
}
