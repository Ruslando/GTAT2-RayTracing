package main;

import main.shapes.Shape;

import java.util.ArrayList;

public class Scene {

    private ArrayList<Shape> scene;

    private ArrayList<Light> lights;

    public Scene(){
        scene = new ArrayList<>();
        lights = new ArrayList<>();
    }

    public void addShape(Shape shape){
        scene.add(shape);
    }

    public void addLight(Light light) { lights.add(light); }

    public ArrayList<Shape> getShapes(){
        return scene;
    }

    public ArrayList<Light> getLights() { return lights; }
}
