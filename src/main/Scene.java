package main;

import main.shapes.Shape;

import java.util.ArrayList;

public class Scene {

    private ArrayList<Shape> scene;

    public Scene(){
        scene = new ArrayList<>();
    }

    public void addShape(Shape shape){
        scene.add(shape);
    }

    public ArrayList<Shape> getScene(){
        return scene;
    }
}
