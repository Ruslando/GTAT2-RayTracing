package main.shapes;

import main.util.Material;
import main.util.Vector3;

public class QuadricSphere extends Quadric {

    public QuadricSphere(){
        super(1,1,1,0,0,0,0,0,0,-1, new Material(new Vector3(0,  1, 0)));
    }
}
