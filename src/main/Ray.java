package main;

import main.shapes.Shape;
import main.util.Intersection;
import main.util.Vector3;

public class Ray {

    private Vector3 startingPoint;
    private Vector3 rayDirection;
    private Scene scene;

    private Intersection intersection;
    private boolean hasIntersected;

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
    }


    public void shootRay(){ // Preferably a nullable object
        Intersection intersection = null;
        double distance = Double.MAX_VALUE;

                /* Loops through every object that is in the scene. Only the intersection that is nearest to the screen
                is drawn onto the canvas. If the ray does not hit anything, the intersection field stays null.
                 */
        for(Shape s: scene.getScene()){
            Intersection inter = s.intersect(startingPoint, rayDirection);

            if(inter.getNearestIntersection() < distance){
                distance = inter.getNearestIntersection();
                intersection = inter;
            }
        }

        this.intersection = intersection;
    }

    public boolean isInShadow(){
        Vector3 transposedIntersectionPoint = getIntersectionPoint();
        Ray ray;

        for(Light light : scene.getLights()) {
            transposedIntersectionPoint.add(getNormal().scalarmultiplication(0.1));
            ray = new Ray(transposedIntersectionPoint, light.getPosition().normalize(), scene);
            ray.shootRay();

            if (ray.hasIntersected()) {
                return true;
            }
        }
        return false;
    }

    public Vector3 getStartingPoint(){
        return startingPoint;
    }

    public Vector3 getRayDirection(){
        return rayDirection;
    }

    public boolean hasIntersected(){
        return intersection != null;
    }

    public Intersection getIntersection(){
        return intersection;
    }

    public Vector3 getIntersectionPoint(){
        return rayDirection.scalarmultiplication(intersection.getNearestIntersection()).add(startingPoint);
    }

    public Shape getShape(){
        return intersection.getShape();
    }

    public Vector3 getNormal(){
        return getShape().getNormal(getIntersectionPoint());
    }


}
