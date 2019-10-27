package main.util;

public class Vector3 {

    double x;
    double y;
    double z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3 add(Vector3 v){
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public double scalar(Vector3 b){
        return Math.sqrt(this.x * b.x + this.y * b.y + this.z * b.z);
    }

    public double vectorproduct (Vector3 b){
        return this.x * b.x + this.y * b.y + this.z * b.z;
    }

    public Vector3 multiply(Vector3 v) {
        return new Vector3(this.x*v.x, this.y*v.y,this.z*v.z);
    }

    public Vector3 scalarmultiplication(double s){
        return new Vector3(s*this.x, s*this.y, s*this.z);
    }

    public Vector3 subtract(Vector3 b){
        return new Vector3(this.x - b.x, this.y - b.y, this.z - b.z);
    }

    public Vector3 normalize(){
        double length = scalar(this);
        if(Math.abs(length) == 0){
            return new Vector3();
        }
        return new Vector3(this.x / length, this.y / length, this.z / length);
    }

    public Vector3 cross(Vector3 b){
        return new Vector3(this.y * b.z - this.z * b.y,
                this.z * b.x - this.x * b.z,
                this.x *b.y - this.y * b.x);
    }

    public String toString(){
        return "x: " + x + ", y: " + y + ", z: " + z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
