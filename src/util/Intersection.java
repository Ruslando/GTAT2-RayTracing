package util;

import sample.shapes.Shape;

public class Intersection {
    private int intersections;
    private Shape shape;

    private double x1;
    private double x2;

    public Intersection(double a, double b, double c, Shape shape){

        this.shape = shape;

        double discriminante = b*b - 4*a*c;
        if(discriminante < 0){
            intersections = 0;
        }
        else if(discriminante == 0){
            x1 = -b / 2*a;
            intersections = 1;
        }
        else if (discriminante > 0){
            intersections = 2;
            x1 = -b - Math.sqrt(discriminante) / 2*a;
            x2 = -b + Math.sqrt(discriminante) / 2*a;
        }

    }

    public double getNearestIntersection(){

        double result = Double.MAX_VALUE;

        /*if(intersections == 0){               doesnt matter here, will be max value anyway
            result = Double.MAX_VALUE;
        }*/
        if(intersections == 1){
            result = x1;
        }
        else if(intersections == 2){
            result = Math.min(x1, x2);
            if(result < 0) {
                result = x2;
                if (result < 0) {
                    result = Double.MAX_VALUE;
                }
            }
        }

        return result;
    }

    public Shape getShape() {
        return shape;
    }
}
