package main.shapes;

import main.util.Intersection;
import main.util.Material;
import main.util.Vector3;

public class Quadric implements Shape{

    private double a;
    private double b;
    private double c;
    private double d;
    private double e;
    private double f;
    private double g;
    private double h;
    private double i;
    private double j;
    private Material material;

    public Quadric(double a, double b, double c, double d, double e,
                   double f, double g, double h, double i, double j,
                   Material material) {
        this.a = a; this.b = b; this.c = c; this.d = d; this.e = e;
        this.f = f; this.g = g; this.h = h; this.i = i; this.j = j;
        this.material = material;
    }

    @Override
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        double px = origin.getX(); double py = origin.getY(); double pz = origin.getZ();
        double vx = direction.getX(); double vy = direction.getY(); double vz = direction.getZ();

        double A = a * vx * vx + b * vy * vy + c * vz * vz + 2 * (d * vx * vy + e * vx * vz + f * vy * vz);
        double B = 2 * (a * px * vx + b * py * vy + c * pz * vz + d * (px * vy + vx * py) +
                e * (px * vz + vx * pz) + f * (py * vz + vy * pz) + g * vx + h * vy + i * vz);
        double C = 2 * (d * px * py + e * px * pz + f * py * pz + g * px + h * py * i * pz) +
                a * px * px + b * py * py + c * pz * pz + j;

        return new Intersection(A, B, C, this);
    }

    @Override
    public Vector3 getCenter() {
        return null;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    public Vector3 getNormal(Vector3 point) {
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        Vector3 normal = new Vector3(a*x + b*y + e*z + g, b * y + d * x + f * z + h, c * z + e * x + f * y + i);

        normal.normalize();
        return normal;
    }
}
