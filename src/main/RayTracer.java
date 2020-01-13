package main;

import main.shapes.Shape;
import main.util.Vector3;

import java.util.ArrayList;

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
        Vector3 camWorldPos = camera.getWorldposition();
        ArrayList<Shape> shapes = scene.getScene();
        ArrayList<Light> lights = scene.getLights();

        for (int i = 0; i < Main.HEIGHT; i++){      //Loop every pixel
            for(int j = 0; j < Main.WIDTH; j++){

                Vector3 outputColor = supersample(j, i, camWorldPos, shapes, lights, 0);
                int argb;

                argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                output.writePixel(j, i, argb);
                /*
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

                    argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
                    output.writePixel(j,i, argb);
                }
                */
            }
        }
    }

    private Vector3 supersample(double i, double j, Vector3 camWorldPos, ArrayList<Shape> shapes, ArrayList<Light> lights, int step) {
        Vector3 result;

        double[][] squareCenters = getSquareCenters(i, j, 1./Math.pow(2,step));
        Vector3[] cornerColors = new Vector3[4];

        for(int index = 0; index < 4; index++) {
            double rayX = squareCenters[0][index];
            double rayY = squareCenters[1][index];
            Vector3 rayDirection = camera.getRayDirection(rayX, rayY);
            Ray r = new Ray(camWorldPos, rayDirection, shapes);
            r.shootRay();
            if(r.hasIntersected()) {
                cornerColors[index] = r.getShape().getMaterial().getOutputColor(r, lights);
            }
            else {
                cornerColors[index] = new Vector3(255, 255,255);
            }
        }

        if(areSquareColorsSimilar(cornerColors)) {
            result = cornerColors[0];
        }
        else {
            result = supersample(squareCenters[0][0], squareCenters[1][0], camWorldPos, shapes, lights, step++);
        }

        return result;
    }

    /**
     * Assuming we have a 2x2 kernel, this method should return the coordinates of the four center points of each cell.
     **/
    private double[][] getSquareCenters(double x, double y, double cellLength) {
        double[][] result = new double[2][4]; // 4 times x and y coordinates
        double topLeftX = x + cellLength/2;
        double topLeftY = y + cellLength/2;
        double topRightX = x + cellLength * 1.5;
        double topRightY = y + cellLength/2;
        double bottomLeftX = x + cellLength/2;
        double bottomLeftY = y + cellLength * 1.5;
        double bottomRightX = x + cellLength * 1.5;
        double bottomRightY = y + cellLength * 1.5;
        result[0][0] = topLeftX;
        result[1][0] = topLeftY;
        result[0][1] = topRightX;
        result[1][1] = topRightY;
        result[0][2] = bottomLeftX;
        result[1][2] = bottomLeftY;
        result[0][3] = bottomRightX;
        result[1][3] = bottomRightY;
        return result;
    }

    /**
     * Returns the x,y coordinates of the points between the square centers when given the coordinates of the four
     * corners.
     */
    private double[][] getMiddlePoints(double[][] squareCornersCoordinates) {
        double[][] result = new double[2][5];

        double upX = (squareCornersCoordinates[0][0] + squareCornersCoordinates[0][1]) / 2.;
        double upY = squareCornersCoordinates[0][0];
        double leftX = squareCornersCoordinates[0][0];
        double leftY = (squareCornersCoordinates[1][0] + squareCornersCoordinates[1][2]) / 2.;
        double middleX = upX;
        double middleY = leftY;
        double rightX = squareCornersCoordinates[0][1];
        double rightY = (squareCornersCoordinates[1][1] + squareCornersCoordinates[1][3]) / 2.;
        double bottomX = (squareCornersCoordinates[0][2] + squareCornersCoordinates[0][3]) / 2.;
        double bottomY = squareCornersCoordinates[1][2];

        result[0][0] = upX;
        result[1][0] = upY;
        result[0][1] = leftX;
        result[1][1] = leftY;
        result[0][2] = middleX;
        result[1][2] = middleY;
        result[0][3] = rightX;
        result[1][3] = rightY;
        result[0][4] = bottomX;
        result[1][4] = bottomY;

        return result;
    }

    private boolean areSquareColorsSimilar(Vector3[] colors) {
        double color = colors[0].getX() + colors[0].getY() + colors[0].getZ();
        for(int i = 1; i < 4; i++) {
            double otherColor = colors[i].getX() + colors[i].getY() + colors[i].getZ();
            if(Math.abs(color - otherColor) > 25) return false;
        }
        return true;
    }
}
