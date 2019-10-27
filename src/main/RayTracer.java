package main;

import main.shapes.Shape;
import main.shapes.Sphere;
import main.util.Intersection;
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
                /* Calculates ray-vector. Get shot from screen-center*/
                double u = camera.getL() + (camera.getR() - camera.getL()) * (j + 0.5) / Main.WIDTH;
                double v = camera.getT() + (camera.getB() - camera.getT()) * (i + 0.5) / Main.HEIGHT;

                //Takes into consideration current camera-axis vectors. W_d_negates is current screen ratio.
                Vector3 s = camera.getU().scalarmultiplication(u)
                        .add(camera.getUP().scalarmultiplication(v))
                        .add(camera.getW_d_negated());

                Vector3 tracedir = s.normalize();


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
                    Vector3 point = (tracedir.scalarmultiplication(distance)).add(camera.getWorldposition()).normalize();

                    // Überprüfung, ob Kugel oder nicht Kugel, muss eingebaut werden
                    Vector3 sphereCenter = ((Sphere)intersection.getShape()).getCenter();
                    double radius = ((Sphere)intersection.getShape()).getRadius();
                    Vector3 normal = point.subtract(sphereCenter).scalarmultiplication(1./radius);

                    for(Light l : scene.getLights()) {
                        // Direction of the light
                        Vector3 r = point.subtract(l.getPosition()).normalize();
                        Vector3 nTimesMinusR = normal.multiply(r.scalarmultiplication(-1));
                        if((nTimesMinusR.getX() < 0) && (nTimesMinusR.getY() < 0)&& (nTimesMinusR.getZ() < 0)) {
                            nTimesMinusR = new Vector3(0,0,0);
                        }
                        double brightness = l.getBrightness();
                        Vector3 lightColor = l.getRgb();
                        Vector3 albedo = intersection.getShape().getMaterial().getMaterial();
                        Vector3 outputColor = nTimesMinusR.multiply(lightColor).scalarmultiplication(brightness).multiply(albedo);
                        red += (int) outputColor.getX();
                        green += (int) outputColor.getY();
                        blue += (int) outputColor.getZ();
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
