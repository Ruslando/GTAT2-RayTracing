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
                        Vector3 lightColor = l.getRgb();
                        Vector3 albedo = intersection.getShape().getMaterial().getAlbedo();
                        Vector3 outputColor = lightColor.scalarmultiplication(brightness * nTimesMinusR).multiply(albedo);

                        if(l.getQuadraticDecay()) {
                            double pointToLightDistance = Math.sqrt(Math.pow(point.getX() - l.getPosition().getX(), 2.) +
                                    Math.pow(point.getY() - l.getPosition().getY(), 2.) +
                                    Math.pow(point.getZ() - l.getPosition().getZ(), 2.));
                                    outputColor = outputColor.scalarmultiplication(1/Math.pow(pointToLightDistance,2));
                        }
                        red += (int) outputColor.getX();
                        green += (int) outputColor.getY();
                        blue += (int) outputColor.getZ();

                        // neues Beleuchtungsmodell
                        Material mat = intersection.getShape().getMaterial();
                        double roughness = mat.getRoughness();
                        Vector3 view = tracedir.scalarmultiplication(-1);
                        Vector3 L = point.subtract(l.getPosition()).normalize();
                        double kd = (1-0.04) * (1 - ((Material) mat).getMetalness());
                        Vector3 H = view.add(L).scalarmultiplication(0.5).normalize();
                        double D = (roughness * roughness)/Math.PI*Math.pow(((normal.scalar(H) * normal.scalar(H) * (roughness * roughness - 1) + 1)), 2);
                        // F0 + (1 – F0)(1 – N·V)5
                        double F = 0.04 + Math.pow((1 - 0.04) * (1 - normal.scalar(view)), 5);
                        // (N·V(1 – r/2) + r/2) * N·L / (N·L(1 – r/2) + r/2)
                        double G = normal.scalar(view) / ((normal.scalar(view) * (1 - roughness / 2) + roughness / 2)
                        * normal.scalar(L) / (normal.scalar(L) * (1 - roughness / 2) + roughness / 2));

                    }

                    if(red > 255) red = 255;
                    if(green > 255) green = 255;
                    if(blue > 255) blue = 255;
                    if(red < 0) red = 0;
                    if(green < 0) green = 0;
                    if(blue < 0) blue = 0;

                    argb = (0xff << 24) | (red << 16)| (green << 8) | blue;
                    output.writePixel(j,i, argb);

                }
            }
        }
    }

}
