package main.shapes;

import main.util.Material;
import main.util.Vector3;

public class Zylinder extends Quadric {

    public Zylinder(int radius){
        super(0,1,1,0,0,0,0,0,0,Math.sqrt(radius) * -1, new Material(new Vector3(0,  1, 0)));
    }
}
