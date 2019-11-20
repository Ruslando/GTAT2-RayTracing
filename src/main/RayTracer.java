package main;

import main.shapes.Quadric;
import main.shapes.Shape;
import main.shapes.Sphere;
import main.util.Intersection;
import main.util.Material;
import main.util.Vector3;

public class RayTracer {

    private Camera camera;
    private Scene scene;
    private OutputController output;

    public RayTracer(Camera camera, Scene scene, OutputController output){
        this.camera = camera;
        this.scene = scene;
        this.output = output;
    }

    public void trace() {
        for (int i = 0; i < Main.HEIGHT; i++){      //Loop every pixel
            for(int j = 0; j < Main.WIDTH; j++){

                Vector3 tracedir = camera.getRayDirection(j, i);


                Intersection intersection = null;
                double distance = Double.MAX_VALUE;

                /* Loops through every object that is in the scene. Only the intersection that is nearest to the screen
                is drawn onto the canvas. If the ray does not hit anything, the intersection field stays null.
                 */
                for(Shape shapes: scene.getScene()){
                    Intersection inter = shapes.intersect(camera.getWorldposition(), tracedir);

                    if(inter.getNearestIntersection() < distance){
                        distance = inter.getNearestIntersection();
                        intersection = inter;
                    }
                }

                /* Currently takes the shape color and draws it on the canvas*/
                if(intersection != null){
                    int argb;
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    // Berechnung der Schnittpunktkoordinaten
                    Vector3 point = (tracedir.scalarmultiplication(distance)).add(camera.getWorldposition());

                    Vector3 normal = intersection.getShape().getNormal(point);

                    for(Light l : scene.getLights()) {
                        // Direction of the light
                        Vector3 r = point.subtract(l.getPosition()).normalize();
                        double nTimesMinusR = normal.scalar(r.scalarmultiplication(-1));
                        if((nTimesMinusR < 0)) {
                            nTimesMinusR = 0;
                        }
                        double brightness = l.getBrightness();
                        Vector3 lightColor = l.getRgb().removeGamma();
                        Vector3 albedo = intersection.getShape().getMaterial().getAlbedo().removeGamma();

                        /*
                        if(l.getQuadraticDecay()) {
                            double pointToLightDistance = Math.sqrt(Math.pow(point.getX() - l.getPosition().getX(), 2.) +
                                    Math.pow(point.getY() - l.getPosition().getY(), 2.) +
                                    Math.pow(point.getZ() - l.getPosition().getZ(), 2.));
                                    outputColor = outputColor.scalarmultiplication(1/Math.pow(pointToLightDistance,2));
                        }
                        red += (int) outputColor.getX();
                        green += (int) outputColor.getY();
                        blue += (int) outputColor.getZ();*/

                        // neues Beleuchtungsmodell
                        Material mat = intersection.getShape().getMaterial();
                        double metalness = mat.getMetalness();
                        double roughness = mat.getRoughness();
                        Vector3 V = tracedir.scalarmultiplication(-1);
                        Vector3 L = l.getPosition().subtract(point).normalize(); //l.getPosition().subtract(point).normalize();
                        Vector3 H = V.add(L).scalarmultiplication(0.5).normalize();
                        double D = (roughness * roughness) / (Math.PI * Math.pow(((normal.scalar(H) * normal.scalar(H)) * (roughness * roughness - 1) + 1), 2));
                        double F = 0.04 + (1 - 0.04) * Math.pow((1 - normal.scalar(V)), 5);
                        double G = normal.scalar(V) / ((normal.scalar(V) * (1 - (roughness / 2)) + (roughness / 2))
                        * normal.scalar(L) / (normal.scalar(L) * (1 - (roughness / 2)) + (roughness / 2)));
                        double ks = D * F * G;
                        double kd = (1-0.04) * (1 - metalness); // alternativ für 0.04 ks benutzen

                        Vector3 outputColor = lightColor.multiply(albedo.scalarmultiplication(kd).add(ks)).scalarmultiplication(brightness * (normal.scalar(L))).addGamma();


                        red += (int) outputColor.getX();
                        green += (int) outputColor.getY();
                        blue += (int) outputColor.getZ();

                    }

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, red)) << 16)| (Math.max(0, Math.min(255, green)) << 8) | (Math.max(0, Math.min(255, blue)));
                    output.writePixel(j,i, argb);

                }
            }
        }
    }

}
