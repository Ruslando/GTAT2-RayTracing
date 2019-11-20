package main.shapes;

import main.util.Material;
import main.util.Vector3;

public class QuadricSphere extends Quadric {

    public QuadricSphere(Material material){
        super(1,1,1,0,0,0,0,0,0,-2, material);
    }
}
