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

                Vector3 rayDirection = camera.getRayDirection(j, i);
                int argb;

                Ray ray = new Ray(camera.getWorldposition(), rayDirection, scene.getScene());
                ray.shootRay();
                if(ray.hasIntersected()){
                    Vector3 outputColor = ray.getShape().getMaterial().getOutputColor(ray, scene.getLights(), 0);

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }
            }
        }
    }

}
