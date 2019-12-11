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

    private int recursionStep;
    private int maxRecursionDepth;

    private Shape ignoreShape;

    private Intersection previousIntersection;

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene, Shape ignoreShape, Intersection previousIntersection, int recursionStep, int maxRecursionDepth){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = recursionStep;
        this.maxRecursionDepth = maxRecursionDepth;
        this.ignoreShape = ignoreShape;
        this.previousIntersection = previousIntersection;
    }

    /*
    This describes the way of the ray. It will return a color. The color is created incrementally.
     */
    public Vector3 shootRay(){ // Preferably a nullable object

        // Main ray. Looks if it hits something. Will update intersection field if it does.
        shootTraceRay();

        // If something is hit, it will calculate its local color.
        if(intersection != null) {

            outputColor = getShape().getMaterial().getLocalColor(this, scene.getLights());

            if(getShape().getMaterial().isReflective()) {
                Vector3 result = shootReflectionRay();
                if(result != null){
                    outputColor = result;
                    shootReflectionRay();
                }
            }

            if(shootShadowRay()){
                //Shadow will not be completly black
                outputColor = outputColor.dotproduct(new Vector3 (0.5,0.5,0.5));

            }

            return outputColor;
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

            if(!(s.equals(ignoreShape))){
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

    private Vector3 shootReflectionRay(){

        Vector3 reflectiveColor = null;
        Vector3 output = null;
        Vector3 reflectivity = getShape().getMaterial().getReflectivity();

        if(recursionStep <= maxRecursionDepth){
            Vector3 transposedIntersectionPoint = getIntersectionPoint();
            Vector3 reflectionRayDirection = getRayDirection().subtract(getNormal().scalarmultiplication(2).dotproduct(getNormal().dotproduct(getRayDirection()))).normalize();
            Ray ray = new Ray(transposedIntersectionPoint, reflectionRayDirection, scene, getShape() ,recursionStep + 1, maxRecursionDepth);
            Vector3 result = ray.shootRay();

            if (result != null) {
                reflectiveColor = result;
            }
            else{
                System.out.println("no reflection");
            }

            // Calculates new albedo and change the albedo from reflectiveColor if it exists.
            if(reflectiveColor != null){
                Vector3 objectAlbedo = getShape().getMaterial().getAlbedo();
                Vector3 newAlbedo = reflectivity.scalarmultiplication(-1).add(1).dotproduct(objectAlbedo).add(reflectivity.dotproduct(reflectiveColor));

                getShape().getMaterial().setAlbedo(newAlbedo);
                output = getShape().getMaterial().getLocalColor(this, scene.getLights());
                getShape().getMaterial().setAlbedo(objectAlbedo);
            }
        }

        return output;
    }

    private Vector3 shootRefractionRay(double refractionIndex){

        Vector3 intersectionPoint = getIntersectionPoint();
        double refractionIndexOfHitObject = getShape().getMaterial().getRefractionIndex();
        double i = refractionIndex / refractionIndexOfHitObject;

        Vector3 hitNormal = getNormal();

        Vector3 v1 = getRayDirection(); // first ray direction;

        double w1 = v1.scalar(hitNormal) / (v1.magnitude() * hitNormal.magnitude());
        w1 = Math.acos(w1);
        double w2 = v1.scalar(hitNormal.scalarmultiplication(-1)) / (v1.magnitude() * hitNormal.scalarmultiplication(-1).magnitude());
        w2 = Math.acos(w2);

        double a = Math.cos(w1);
        double b = Math.sqrt(1 - (i * i) * (1 - (a * a)));

        Vector3 v2 = // s. 147, ansonsten v2 benutzen (unten)

        Vector3 v2dot1 = (hitNormal.dotproduct(v1.add(Math.cos(w1)))).scalarmultiplication(i);
        Vector3 v2dot2 = hitNormal.scalarmultiplication(Math.sqrt(1 - (i * i) * (1 - (Math.cos(w1) * Math.cos(w1))))); // cos2 vielleicht nicht richtig
        Vector3 v2 = v2dot1.add(v2dot2.scalarmultiplication(-1));


        return null;
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

    public Intersection getPreviousIntersection(){
        return previousIntersection;
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

}
