package main;

import main.shader.Material;
import main.shapes.Shape;
import main.util.Intersection;
import main.util.Vector3;

public class Ray {

    private Vector3 startingPoint;
    private Vector3 rayDirection;
    private Scene scene;

    private Intersection intersection;

    private int recursionStep;
    private int maxRecursionDepth;

    private Shape ignoreShape;
    private double currentRefractionIndex;
    private double reflectionRate;
    private boolean insideObject;


    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene, int recursionStep, int maxRecursionDepth, boolean insideObject){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.recursionStep = recursionStep;
        this.maxRecursionDepth = maxRecursionDepth;
        this.currentRefractionIndex = 1;
        this.insideObject = insideObject;
    }

    public Ray(Vector3 startingPoint, Vector3 raydirection, Scene scene){
        this.startingPoint = startingPoint;
        this.rayDirection = raydirection.normalize();
        this.scene = scene;
        this.currentRefractionIndex = 1;
    }

    /*
    This describes the way of the ray. It will return a color. The color is created incrementally.
     */
    public Vector3 shootRay(){ // Preferably a nullable object

        Vector3 outputColor = null;

        // Main ray. Looks if it hits something. Will update intersection field if it does.
        shootTraceRay();

        // If something is hit
        if(intersection != null) {

            Material m = getShape().getMaterial();

            Vector3 reflectedColor = null;
            Vector3 localColor = null;

            if(m.isTransparent()){
                localColor = shootRefractionRay();
            }
            else{
                localColor = m.getAlbedo();
            }

            if(m.isReflective()){
                Vector3 result = shootReflectionRay();
                if(result != null){
                    reflectedColor = result;
                }
                else{
                    reflectedColor = m.getAlbedo();
                }
            }

            // Performance: für jeden strahl zu jedem objeckt shoot shadow schicken
            if(reflectedColor != null && localColor != null){
                outputColor = m.getLocalColor(this, scene.getLights(), reflectedColor, localColor);
            }
            else{
                outputColor = new Vector3(1,1,1);
            }

            return outputColor;
        }

        return new Vector3(1,1,1);
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
        Ray ray;

        for(Light light : scene.getLights()) {
            ray = new Ray(transposePositionInNormalDirection(true, 0.1), light.getPosition().normalize(), scene);
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
        Shape shape = getShape();

        if(recursionStep <= maxRecursionDepth){
            Vector3 transposedIntersectionPoint;
            Vector3 reflectionRayDirection;

            double a = getRayDirection().scalarmultiplication(-1).scalar(getNormal());

            Ray ray;

            // If ray moves outside of object
            if(a < 0 ){
                Vector3 normalReversed = getNormal().scalarmultiplication(-1);
                Vector3 intersectpoint = getIntersectionPoint();
                transposedIntersectionPoint = transposePositionInNormalDirection(false, 1);
                //transposedIntersectionPoint = transposePositionInOwnDirection(true, 1);
                reflectionRayDirection = getRayDirection().subtract(normalReversed.scalarmultiplication(2).dotproduct(normalReversed.dotproduct(getRayDirection()))).normalize();

                ray = new Ray(transposedIntersectionPoint, reflectionRayDirection, scene ,recursionStep + 1, maxRecursionDepth, false);
            }
            // If ray moves inside object
            else{
                transposedIntersectionPoint = transposePositionInNormalDirection(true, 1);
                //transposedIntersectionPoint = transposePositionInOwnDirection(false, 1);
                reflectionRayDirection = getRayDirection().subtract(getNormal().scalarmultiplication(2).dotproduct(getNormal().dotproduct(getRayDirection()))).normalize();

                ray = new Ray(transposedIntersectionPoint, reflectionRayDirection, scene ,recursionStep + 1, maxRecursionDepth, true);
            }


            // If the material is not transparent, it will ignore itself in the enxt ray so that it will not accidently hit itself again.
            if(!shape.getMaterial().isTransparent()){
                ray.setIgnoreShape(getShape());
            }
            return ray.shootRay();

        }

        return output;
    }

    private Vector3 shootRefractionRay(){
        Vector3 output = null;

        if(recursionStep <= maxRecursionDepth){
            // Checks the raydirection of the refraction ray inside or outside of the object
            Vector3 hitNormal = getNormal();

            Vector3 transposedIntersectionPoint;
            double a = getRayDirection().scalarmultiplication(-1).scalar(hitNormal);

            double i1 = this.currentRefractionIndex;
            double i2;

            boolean inside;

            // When ray moves outside of object
            if(a < 0){
                a *= -1;
                transposedIntersectionPoint = transposePositionInNormalDirection(true, 0.5);
                i2 = 1.0;   // for testing, ray will always hit air first when leaving an object, will be changed later
                inside = false;
            }
            else{   // When ray moves inside object
                hitNormal = hitNormal.scalarmultiplication(-1);
                transposedIntersectionPoint = transposePositionInNormalDirection(false, 0.5);
                i2 = getShape().getMaterial().getRefractionIndex();
                inside = true;
            }

            double i = i1 / i2;
            double b = 1 - (i * i) * (1 - (a * a));


            Vector3 v1 = getRayDirection(); // first ray direction;
               // This is the refracted ray direction

            double FReflected;
            // Check if angle is too big, if it is reflections occur
            if(b < 0){
                reflectionRate = 1;
            }
            else{
                b = Math.sqrt(b);

                // Calculate reflection rate
                double FVertical = ((i1 * a - i2 * b) / (i1 * a + i2 * b));
                FVertical *= FVertical;
                double FParallel = ((i2 * a - i1 * b) / (i2 * a + i1 * b));
                FParallel *= FParallel;
                reflectionRate = (FVertical + FParallel) / 2;

                Vector3 v2 = v1.scalarmultiplication(i).add(hitNormal.scalarmultiplication(i*a - b)).normalize();

                // Prepares new refracted ray
                Ray ray = new Ray(transposedIntersectionPoint, v2, scene, recursionStep+1, maxRecursionDepth, inside);
                ray.setCurrentRefractionIndex(i2);
                output = ray.shootRay();
                // When the ray returns null as output, this means that the ray hit its last object. It is then that the local color is calculated.
                if(output == null){
                    //output = getShape().getMaterial().getLocalColorRefraction(this, scene.getLights(), calculateReflectivityAmount());
                    //output = getShape().getMaterial().getLocalColor(this, scene.getLights());   // Eventuell falsch
                }

            }
        }
        return output;
    }

    /**
     * Transposes the given vector by a fraction of the ray hit normal
     * @param outward A boolean that decides the direction of the normal. If true, the normal pointing outwards is
     *                selected, if false, the normal direction is inward into the hit object.
     * @param percentage The amount of the transposition.
     * @return Returns the position vector that has been transposed
     */
    private Vector3 transposePositionInNormalDirection(boolean outward, double percentage){

        if(outward){
            return getIntersectionPoint().add(getNormal().scalarmultiplication(percentage));
        }
        else{
            return getIntersectionPoint().add(getNormal().scalarmultiplication(-1).scalarmultiplication(percentage));
        }
    }

    private Vector3 transposePositionInOwnDirection(boolean forward, double percentage){
        if(forward){
            return getIntersectionPoint().add(getRayDirection().scalarmultiplication(percentage));
        }
        else{
            return getIntersectionPoint().add(getRayDirection().scalarmultiplication(-1).scalarmultiplication(percentage));
        }
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

    public Vector3 getIntersectionPoint(){
        return rayDirection.scalarmultiplication(intersection.getNearestIntersection()).add(startingPoint);
    }

    public Shape getShape(){
        return intersection.getShape();
    }

    public Vector3 getNormal(){
        return getShape().getNormal(getIntersectionPoint()).normalize();
    }

    private void setIgnoreShape(Shape ignoreShape){
        this.ignoreShape = ignoreShape;
    }

    public double getReflectionRate() {
        return reflectionRate;
    }

    public double getCurrentRefractionIndex() {
        return currentRefractionIndex;
    }

    public void setCurrentRefractionIndex(double currentRefractionIndex) {
        this.currentRefractionIndex = currentRefractionIndex;
    }

}
