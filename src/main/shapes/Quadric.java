package main.shapes;

import main.util.Intersection;
import main.util.Material;
import main.util.Matrix4;
import main.util.Vector3;

public abstract class Quadric implements Shape{

    private Matrix4 quadricMatrix;

    private Material material;

    public Quadric(double a, double b, double c, double d, double e,
                   double f, double g, double h, double i, double j,
                   Material material) {
        this.quadricMatrix = new Matrix4();
        quadricMatrix.getQuadricMatrix(a,b,c,d,e,f,g,h,i,j);
        this.material = material;
    }

    @Override
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        double px = origin.getX(); double py = origin.getY(); double pz = origin.getZ();
        double vx = direction.getX(); double vy = direction.getY(); double vz = direction.getZ();

        double[][] mat = quadricMatrix.getMatrixArray();

        double A = mat[0][0] * vx * vx + mat[1][1] * vy * vy + mat[2][2] * vz * vz + 2 * (mat[0][1] * vx * vy + mat[0][2] * vx * vz + mat[1][2] * vy * vz);
        double B = 2 * (mat[0][0] * px * vx + mat[1][1] * py * vy + mat[2][2] * pz * vz + mat[0][1] * (px * vy + vx * py) +
                mat[0][2] * (px * vz + vx * pz) + mat[1][2] * (py * vz + vy * pz) + mat[0][3] * vx + mat[1][3] * vy + mat[2][3] * vz);
        double C = 2 * (mat[1][0] * px * py + mat[0][2] * px * pz + mat[1][2] * py * pz + mat[0][3] * px + mat[1][3] * py * mat[2][3] * pz) +
                mat[0][0] * px * px + mat[1][1] * py * py + mat[2][2] * pz * pz + mat[3][3];

        /*double A = a * vx * vx + b * vy * vy + c * vz * vz + 2 * (d * vx * vy + e * vx * vz + f * vy * vz);
        double B = 2 * (a * px * vx + b * py * vy + c * pz * vz + d * (px * vy + vx * py) +
                e * (px * vz + vx * pz) + f * (py * vz + vy * pz) + g * vx + h * vy + i * vz);
        double C = 2 * (d * px * py + e * px * pz + f * py * pz + g * px + h * py * i * pz) +
                a * px * px + b * py * py + c * pz * pz + j;*/

        return new Intersection(A, B, C, this);
    }

    public Vector3 getCenter() {
        return null;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    public Vector3 getNormal(Vector3 point) {
        double[][] mat = quadricMatrix.getMatrixArray();

        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();

        Vector3 normal = new Vector3(mat[0][0]*x + mat[1][1]*y + mat[0][2]*z + mat[0][3], mat[1][1] * y + mat[0][1]
                * x + mat[2][1] * z + mat[1][3], mat[2][2] * z + mat[2][0] * x + mat[1][2] * y + mat[2][3]);

        normal.normalize();
        return normal;
    }

    public Quadric translate(double x, double y, double z){
        Matrix4 translationMatrix = new Matrix4(1,0,0,x*-1,0,1,0,y*-1,0,0,1,z*-1,0
                ,0,0,1);

        quadricMatrix = translationMatrix.getTransposed().multiply(quadricMatrix).multiply(translationMatrix);

        return this;
    }

    public Quadric rotate(double phi){
        double angle = Math.toRadians(phi) * -1;
        Matrix4 rotationMatrix = new Matrix4(Math.cos(angle), -Math.sin(angle), 0, 0,
                Math.sin(angle), Math.cos(angle), 0,0,
                0,0,1,0,
                0,0,0,1);

        quadricMatrix = rotationMatrix.getTransposed().multiply(quadricMatrix).multiply(rotationMatrix);

        return this;
    }

    public Quadric scale(double x, double y, double z){
        Matrix4 scaleMatrix = new Matrix4(1/x, 0,0,0,0,
                1/ y,0,0,0,0,1/z,0,0,0,0, 1);

        quadricMatrix = scaleMatrix.getTransposed().multiply(quadricMatrix).multiply(scaleMatrix);

        return this;
    }
}
