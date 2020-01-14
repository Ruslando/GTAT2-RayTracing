package main;

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

                Vector3 ray1Direction = camera.getRayDirection(j+0.25, i+0.25);
                Vector3 ray2Direction = camera.getRayDirection(j+0.75, i+0.75);
                int argb;

                Ray ray1 = new Ray(camera.getWorldposition(), ray1Direction, scene.getScene());
                Ray ray2 = new Ray(camera.getWorldposition(), ray2Direction, scene.getScene());
                ray1.shootRay();
                ray2.shootRay();
                Vector3 ray1Result = new Vector3(255,255,255), ray2Result = new Vector3(255,255,255), outputColor = new Vector3();
                boolean anyHit = false;
                if(ray1.hasIntersected()){
                    anyHit = true;
                    ray1Result = ray1.getShape().getMaterial().getOutputColor(ray1, scene.getLights());
                }
                if(ray2.hasIntersected()) {
                    anyHit = true;
                    ray2Result = ray2.getShape().getMaterial().getOutputColor(ray2, scene.getLights());
                }
                if(anyHit) {
                    double similaritySum = Math.abs(ray1Result.getX() + ray1Result.getY() + ray1Result.getZ() - ray2Result.getX() + ray2Result.getY() + ray2Result.getZ());
                    if(similaritySum < 30) { // Farbwerte ähnlich genug
                        outputColor = ray1Result.add(ray2Result).scalarmultiplication(1./2.);
                    }
                    else {
                        Vector3 ray3Direction = camera.getRayDirection(j+0.5, i+0.5);
                        Ray ray3 = new Ray(camera.getWorldposition(), ray3Direction, scene.getScene());
                        ray3.shootRay();
                        Vector3 ray3Result = new Vector3(255,255,255);
                        if(ray3.hasIntersected()) {
                            ray3Result = ray3.getShape().getMaterial().getOutputColor(ray3, scene.getLights());
                        }
                        outputColor = ray1Result.add(ray2Result).add(ray3Result).scalarmultiplication(1./3.);
                    }

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16) | (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }
            }
        }
    }

}
