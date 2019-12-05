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

                //Initializes first ray direction from camera
                Vector3 rayDirection = camera.getRayDirection(j, i);
                int argb;

                // Creates new ray from camera postion to the pixel location of i,j
                Ray ray = new Ray(camera.getWorldposition(), rayDirection, scene);
                // Shoots ray
                ray.shootRay();
                // Checks if ray intersects
                if(ray.hasIntersected()){
                    Vector3 outputColor;

                    // Checks if ray is in shadow. Right now sets shadows to be a solid black color
                    if(ray.isInShadow()){
                        outputColor = new Vector3(0,0,0);   // Sets color to black
                    }
                    else {
                        // Calculates the output color. Calculation happens in material
                        outputColor = ray.getShape().getMaterial().getOutputColor(ray, scene.getLights());
                    }

                    // Writes output color as argb value
                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }
            }
        }
    }

}
