package sample;

public class Sphere {

    double radius;
    Vector3 center;

    public Sphere(double radius, Vector3 center){
        this.radius = radius;
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public Vector3 getCenter() {
        return center;
    }

}
