package main;

import main.util.Vector3;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RayTracer {

    private Camera camera;
    private Scene scene;
    private OutputController output;
    private BufferedImage skydome;

    public RayTracer(Camera camera, Scene scene, OutputController output){
        this.camera = camera;
        this.scene = scene;
        this.output = output;

        if(skydome == null) {
            try{
                skydome = ImageIO.read(new File("skymap.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void trace() {
        for (int i = 0; i < Main.HEIGHT; i++){      //Loop every pixel
            for(int j = 0; j < Main.WIDTH; j++){

                Vector3 rayDirection = camera.getRayDirection(j, i);
                int argb;

                Ray ray = new Ray(camera.getWorldposition(), rayDirection, scene.getScene());
                ray.shootRay();
                if(ray.hasIntersected()){
                    Vector3 outputColor = ray.getShape().getMaterial().getOutputColor(ray, scene.getLights());

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }
                else {
                    // check ob ray y ebene schneidet, wenn

                    double textureCoordinateX = (ray.getRayDirection().getX() + 1 ) / 2;
                    double textureCoordinateY = (ray.getRayDirection().getY() + 1 ) / 2;
                    textureCoordinateX *= skydome.getWidth();
                    textureCoordinateY *= skydome.getHeight();
                    argb = skydome.getRGB((int)textureCoordinateX, (int)textureCoordinateY);
                    output.writePixel(j,i, argb );
                }
            }
        }
    }

}
