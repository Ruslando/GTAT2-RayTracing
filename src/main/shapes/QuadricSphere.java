package main.shapes;

import main.shader.Material;

public class QuadricSphere extends Quadric {

    public QuadricSphere(Material material){
        super(1,1,1,0,0,0,0,0,0,-2, material);
    }
}
