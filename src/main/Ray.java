package main;

import main.shapes.Shape;
import main.util.Intersection;
import main.util.Vector3;

public class Ray {

    private Vector3 startingPoint;
    private Vector3 rayDirection;
    private Scene scene;

    private Vector3 outputColor;
    private Intersection intersection;

    private double energy;
    private int recursionStep;
    private int maxRecursionDepth;

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene, double energy, int recursionStep, int maxRecursionDepth){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = recursionStep;
        this.maxRecursionDepth = maxRecursionDepth;
        this.energy = energy;
    }

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = 1;
        this.maxRecursionDepth = 4;
        this.energy = 1;
    }

    /*
    This describes the way of the ray. It will return a color. The color is created incrementally.
     */
    public Vector3 shootRay(){ // Preferably a nullable object

        // Main ray. Looks if it hits something. Will update intersection field if it does.
        shootTraceRay();

        // If something is hit, it will calculate its local color.
        if(intersection != null) {

            if(getShape().getMaterial().isReflective()){
                Vector3 reflectiveColor = null;
                double reflectivity = getShape().getMaterial().getReflectivity();

                if(recursionStep <= maxRecursionDepth){
                    if (energy > reflectivity){

                        Vector3 result = shootReflectiveRay(energy * reflectivity);
                        if (result != null) {
                            reflectiveColor = result;
                        }
                        else{
                            System.out.println("no reflection");
                        }

                        // Calculates new albedo and change the albedo from reflectiveColor if it exists.
                        if(reflectiveColor != null){
                            Vector3 objectAlbedo = getShape().getMaterial().getAlbedo();
                            Vector3 newAlbedo = objectAlbedo.scalarmultiplication(1 - reflectivity).add(reflectiveColor.scalarmultiplication(reflectivity));  // maybe remove gammes statement
                            getShape().getMaterial().setAlbedo(newAlbedo);
                        }
                    }
                }
            }

            // Outputcolor or working color ???;
            outputColor = getShape().getMaterial().getOutputColorNonGamma(this, scene.getLights()); /////////

            // Shadow calculation
            if(shootShadowRay()){
                // Shadow will not be completly black
                //outputColor = outputColor.multiply(new Vector3 (0.2,0.2,0.2));

                // No shadows
                outputColor = outputColor.dotproduct(new Vector3 (1,1,1));
            }
        }

        if(outputColor != null){
            return outputColor.addGamma();
        }

        return null;
    }

    private void shootTraceRay(){
        Intersection intersection = null;
        double distance = Double.MAX_VALUE;

                /* Loops through every object that is in the scene. Only the intersection that is nearest to the screen
                is drawn onto the canvas. If the ray does not hit anything, the intersection field stays null.
                 */
        for(Shape s: scene.getShapes()){
            Intersection inter = s.intersect(startingPoint, rayDirection);

            // The distance to the nearest intersection
            double d = inter.getNearestIntersection();

            /* check if the new distance is smaller as the last recorded distance. Also checks if the distance is smaller
            than 0*/
            if(d < distance && d >= 0){
                distance = d;
                intersection = inter;
            }
        }

        this.intersection = intersection;
    }

    private boolean shootShadowRay(){
        Vector3 transposedIntersectionPoint = getIntersectionPoint().add(getNormal().scalarmultiplication(0.1));
        Ray ray;

        for(Light light : scene.getLights()) {
            ray = new Ray(transposedIntersectionPoint, light.getPosition().normalize(), scene);
            ray.shootTraceRay();

            if (ray.hasIntersected()) {
                return true;
            }
        }
        return false;
    }

    private Vector3 shootReflectiveRay(double energy){
        Vector3 transposedIntersectionPoint = getIntersectionPoint().add(getNormal().scalarmultiplication(0.01));
        Vector3 reflectionRayDirection = getRayDirection().subtract(getNormal().scalarmultiplication(2).dotproduct(getNormal().dotproduct(getRayDirection()))).normalize();
        Ray ray = new Ray(transposedIntersectionPoint, reflectionRayDirection, scene, energy ,recursionStep + 1, maxRecursionDepth);
        return ray.shootRay();
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
        return getShape().getNormal(getIntersectionPoint()).normalize();
    }

    public Vector3 getOutputColor(){
        return getOutputColor();
    }

}
