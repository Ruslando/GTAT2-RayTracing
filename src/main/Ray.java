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

    private Shape ignoreSelf;

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene, Shape ignoreShape, int recursionStep, int maxRecursionDepth){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = recursionStep;
        this.maxRecursionDepth = maxRecursionDepth;
        this.ignoreSelf = ignoreShape;
    }

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = 1;
        this.maxRecursionDepth = 4;
        this.ignoreSelf = null;
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
                Vector3 result = shootReflectiveRay();
                if(result != null){
                    outputColor = result;
                    shootReflectiveRay();
                }
            }

            if(shootShadowRay()){
                //Shadow will not be completly black
                outputColor = outputColor.dotproduct(new Vector3 (0.2,0.2,0.2));

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

    private Vector3 shootReflectiveRay(){

        Vector3 reflectiveColor = null;
        Vector3 output = null;
        Vector3 reflectivity = getShape().getMaterial().getReflectivity();

        if(recursionStep <= maxRecursionDepth){
            Vector3 transposedIntersectionPoint = getIntersectionPoint().add(getNormal()).scalarmultiplication(0.01);
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

                Vector3 refColor = reflectivity.dotproduct(reflectiveColor);    // reflectivity only takes red color into consideration
                Vector3 refl = reflectivity.scalarmultiplication(-1).add(1);
                Vector3 reflalbedo = refl.dotproduct(objectAlbedo);
                Vector3 newAlbedo = reflalbedo.add(refColor);
                Vector3 newAlbedo2 = reflectivity.scalarmultiplication(-1).add(1).dotproduct(objectAlbedo).add(reflectivity.dotproduct(reflectiveColor));

                getShape().getMaterial().setAlbedo(newAlbedo);
                output = getShape().getMaterial().getLocalColor(this, scene.getLights());
                getShape().getMaterial().setAlbedo(objectAlbedo);
            }
        }

        return output;
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
