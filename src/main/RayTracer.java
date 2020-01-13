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

                if(i == Main.HEIGHT / 2 + 125 && j == Main.WIDTH / 2){
                    System.out.println();
                }
                int argb;

                // Creates new ray from camera position to the pixel location of i,j
                Ray ray = new Ray(camera.getWorldposition(), rayDirection, scene, 1, 7);
                // Shoots ray and waits for a Color to return;
                Vector3 outputColor = null;
                outputColor = ray.shootRay();

                /* If the outputColor is null, it means it has hit nothing along its way.
                in this case we don't write any pixels
                 */
                if(outputColor != null)  {
                    outputColor = outputColor.normalizedToColor();

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16) | (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }


            }
        }
    }

}
