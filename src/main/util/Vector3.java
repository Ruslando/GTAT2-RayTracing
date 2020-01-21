package main.util;

public class Vector3 {

    private double x;
    private double y;
    private double z;

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

    public Vector3 add(double n){ return new Vector3 (this.x + n, this.y + n, this.z + n);}

    public double scalar(Vector3 b){
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
        double length = length(this);
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

    public double length(Vector3 b){
        return Math.sqrt((this.x * b.x) + (this.y * b.y) + (this.z * b.z));
    }

    public Vector3 removeGamma(){
        return new Vector3(Math.pow(this.x, 2.2), Math.pow(this.y, 2.2), Math.pow(this.z, 2.2));
    }

    public Vector3 addGamma(){
        return new Vector3(Math.pow(this.x, 1/2.2), Math.pow(this.y, 1/2.2), Math.pow(this.z, 1/2.2));
    }

    public Vector3 removeGammaFast(){
        return new Vector3(this.x * this.x, this.y * this.y,  this.z * this.z);
    }

    public Vector3 addGammaFast(){
        return new Vector3(Math.sqrt(this.x), Math.sqrt(this.y), Math.sqrt(this.z));
    }

    public Vector3 clamp(double min, double max){
        return new Vector3(Math.max(min, Math.min(this.x, max)), Math.max(min, Math.min(this.y, max)), Math.max(min, Math.min(this.z, max)));
    }

    public Vector3 clampMin(double min){
        return new Vector3(Math.max(min, this.x), Math.max(min, this.y), Math.max(min, this.z));
    }

    public Vector3 normalizedToColor(){
        return new Vector3(this.x * 255, this.y * 255, this.z * 255);
    }


}
