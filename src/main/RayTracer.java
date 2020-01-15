package main;

import main.shapes.Shape;
import main.util.AntiAliasing;
import main.util.Vector3;

import java.util.ArrayList;

public class RayTracer {

    private Camera camera;
    private Scene scene;
    private OutputController output;

    private static int maxRecursionStep = 6;

    ArrayList<Shape> shapes;
    ArrayList<Light> lights;

    public RayTracer(Camera camera, Scene scene, OutputController output){
        this.camera = camera;
        this.scene = scene;

        this.shapes = scene.getShapes();
        this.lights = scene.getLights();

        this.output = output;
    }

    public void trace() {
        for (int i = 0; i < Main.HEIGHT; i++){      //Loop every pixel
            for(int j = 0; j < Main.WIDTH; j++){

                applyAntiAliasing(AntiAliasing.SSAA_NEIGHBOR, i, j);

            }
        }
    }

    private void applyAntiAliasing(AntiAliasing mode, int i, int j){
        switch(mode){
            case NOAA:
                noAntiAlisaing(i, j);
                break;
            case SSAA_SINGLE:
                superSamplingSingle(i, j);
                break;
            case SSAA_NEIGHBOR:
                superSamplingNeighbor(i, j);
                break;
            default: noAntiAlisaing(i, j);

        }
    }

    private void noAntiAlisaing(int i, int j){
        Vector3 rayDirection = camera.getRayDirection(j, i);

        int argb;

        // Creates new ray from camera position to the pixel location of i,j
        Ray ray = new Ray(camera.getWorldposition(), rayDirection, shapes, lights, 1, 6);
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

    private void superSamplingSingle(int i, int j){

        Vector3 ray1Direction = camera.getRayDirection(j+0.25, i+0.25);
        Vector3 ray2Direction = camera.getRayDirection(j+0.75, i+0.75);
        int argb;

        Ray ray1 = new Ray(camera.getWorldposition(), ray1Direction, shapes, lights, 1, maxRecursionStep);
        Ray ray2 = new Ray(camera.getWorldposition(), ray2Direction, shapes, lights,1 , maxRecursionStep);
        Vector3 ray1Result = ray1.shootRay().normalizedToColor();
        Vector3 ray2Result = ray2.shootRay().normalizedToColor();

        Vector3 outputColor;

        double similaritySum = Math.abs((ray1Result.getX() + ray1Result.getY() + ray1Result.getZ()) - (ray2Result.getX() + ray2Result.getY() + ray2Result.getZ()));

        if(similaritySum < 30) { // Farbwerte ähnlich genug
            outputColor = ray1Result.add(ray2Result).scalarmultiplication(1./2.);
        }
        else {
            Vector3 ray3Direction = camera.getRayDirection(j+0.5, i+0.5);
            Ray ray3 = new Ray(camera.getWorldposition(), ray3Direction, shapes, lights, 1, maxRecursionStep);
            Vector3 ray3Result = ray3.shootRay().normalizedToColor();
            outputColor = ray1Result.add(ray2Result).add(ray3Result).scalarmultiplication(1./3.);
        }

        argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16) | (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
        output.writePixel(j,i, argb);

    }

    private void superSamplingNeighbor(int i, int j){
        Vector3 camWorldPos = camera.getWorldposition();

        Vector3 outputColor;
        int argb;

        double[][] squareCenters = getSquareCenters(j, i, 1);
        Vector3[] cornerColors = new Vector3[4];

        for(int index = 0; index < 4; index++) {
            double rayX = squareCenters[0][index];
            double rayY = squareCenters[1][index];
            Vector3 rayDirection = camera.getRayDirection(rayX, rayY);
            Ray r = new Ray(camWorldPos, rayDirection, scene.getShapes(), scene.getLights(), 1, maxRecursionStep);

            cornerColors[index] = r.shootRay().normalizedToColor();
        }

        outputColor = supersample(camWorldPos, cornerColors, squareCenters, 0);

        argb = (0xff << 24) | (Math.max(0, Math.min(255, (int) outputColor.getX())) << 16)| (Math.max(0, Math.min(255, (int) outputColor.getY())) << 8) | (Math.max(0, Math.min(255, (int) outputColor.getZ())));
        output.writePixel(j, i, argb);
    }


    private Vector3 supersample(Vector3 camWorldPos, Vector3[] colors, double[][] squareCenters, int step) {
        Vector3 result;

        if(areSquareColorsSimilar(colors) || step > 5) {
            result = colors[0];
        }
        else {
            Vector3[] middleColors = calculateMiddleColors(squareCenters, camWorldPos);

            Vector3[] square1Colors = new Vector3[4];
            Vector3[] square2Colors = new Vector3[4];
            Vector3[] square3Colors = new Vector3[4];
            Vector3[] square4Colors = new Vector3[4];

            square1Colors[0] = colors[0];
            square1Colors[1] = middleColors[0];
            square1Colors[2] = middleColors[1];
            square1Colors[3] = middleColors[2];

            square2Colors[0] = middleColors[0];
            square2Colors[1] = colors[1];
            square2Colors[2] = middleColors[2];
            square2Colors[3] = middleColors[3];

            square3Colors[0] = middleColors[1];
            square3Colors[1] = middleColors[2];
            square3Colors[2] = colors[2];
            square3Colors[3] = middleColors[4];

            square4Colors[0] = middleColors[2];
            square4Colors[1] = middleColors[3];
            square4Colors[2] = middleColors[4];
            square4Colors[3] = colors[3];

            Vector3 color1 = supersample(camWorldPos, square1Colors, getSquareCenters(squareCenters[0][0], squareCenters[1][0], 1./Math.pow(2, step+1)), step+1);
            Vector3 color2 = supersample(camWorldPos, square2Colors, getSquareCenters(squareCenters[0][1], squareCenters[1][1], 1./Math.pow(2, step+1)), step+1);
            Vector3 color3 = supersample(camWorldPos, square3Colors, getSquareCenters(squareCenters[0][2], squareCenters[1][2], 1./Math.pow(2, step+1)), step+1);
            Vector3 color4 = supersample(camWorldPos, square4Colors, getSquareCenters(squareCenters[0][3], squareCenters[1][3], 1./Math.pow(2, step+1)), step+1);
            result = color1.add(color2).add(color3).add(color4).scalarmultiplication(1./4.);
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

    private Vector3[] calculateMiddleColors(double[][] squareCenters, Vector3 camWorldPos) {
        Vector3[] result = new Vector3[5];
        double[][] positions = getMiddlePoints(squareCenters);
        for(int i = 0; i < result.length; i++) {
            double rayX = positions[0][i];
            double rayY = positions[1][i];
            Vector3 rayDirection = camera.getRayDirection(rayX, rayY);
            Ray r = new Ray(camWorldPos, rayDirection, shapes, lights,1, maxRecursionStep);
            result[i] = r.shootRay().normalizedToColor();
        }
        return result;
    }

}
