package main.util;

import main.shapes.Shape;

public class Intersection {
    public int intersections;
    public Shape shape;

    public double x1;
    public double x2;

    private double nearestDistance;

    /** Calculates the abc formula and returns the result as a Intersection data type.
     *
     * @param a The a part of the formula
     * @param b The b part
     * @param c The c part
     * @param shape The shape of the object.
     */
    public Intersection(double a, double b, double c, Shape shape){

        this.shape = shape;

        // If discriminante is negative, the formula will not work. There are no intersections.
        if(a < 0 || (4*a*c > b*b)) {
            x1 = Double.MAX_VALUE;
            x2 = Double.MAX_VALUE;
            intersections = 0;
        }
        else if(a == 0){    // There is exactly one intersection if the discriminante is exactly 0.
            x1 = -c/b;
            intersections = 1;
        }
        else {    // There are two intersections.
            intersections = 2;
            double plusOrMinus = (b < 0) ? -1 : 1;
            double k = (-b -  plusOrMinus * Math.sqrt((b*b - 4*a*c)))/2;
            x1 = c/k;
            x2 = k/a;
        }

    }

    /**
     * Gives the nearest intersection, if there are two.
     * @return A double value of the nearest intersection.
     */
    public double getNearestIntersection(){

        if(nearestDistance == 0){
            double result = Double.MAX_VALUE;

        /*if(intersections == 0){               doesnt matter here, will be max value anyway
            result = Double.MAX_VALUE;
        }*/
            if(intersections == 1){
                result = x1;
            }
            else if(intersections == 2){

                if(x1 >= 0 && x2 >= 0){
                    result = Math.min(x1, x2);
                    return result;
                }
                if(x1 < 0 && x2 < 0){
                    return result;
                }
                if(x1 < 0 || x2 < 0){
                    result = Math.max(x1, x2);
                }
                else{
                    return result;
                }

            }

            nearestDistance = result;
            return result;
        }
        else{
            return nearestDistance;
        }
    }

    public Shape getShape() {
        return shape;
    }
}
