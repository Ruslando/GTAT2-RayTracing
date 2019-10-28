package main;

import main.util.Vector3;

public class Camera {

    private Vector3 worldposition;
    private Vector3 lookat;
    private Vector3 view;

    private Vector3 UP;
    private Vector3 U;
    private Vector3 W;
    private Vector3 V;
    private double roll;
    private double cameraangle;



    public Camera(){
        this.roll = 0;
        this.worldposition = new Vector3(0,0,-10);
        this.lookat = new Vector3(0.0, 0.0, 1);

        setup();
    }

    public Camera(Vector3 position, Vector3 lookat){
        this.roll = 0;
        this.worldposition = position;
        this.lookat = lookat;
        cameraangle = Math.PI / 4;

        setup();
    }

    // GETTERS AND SETTERS

    private void setup(){
        this.view = lookat.subtract(worldposition).normalize();
        this.UP = new Vector3(Math.sin(roll), Math.cos(roll), 0);

        this.W = view.scalarmultiplication(-1.0).normalize();
        this.U = W.cross(UP).normalize();
        this.V = U.cross(W).normalize();
    }

    public Vector3 getRayDirection(int x, int y){
        double s = (Main.HEIGHT / 2.0) / Math.tan(cameraangle / 2.0);
        double left = x - ((Main.WIDTH ) / 2.0);
        double top =  ((Main.HEIGHT) / 2.0) - y;

        Vector3 r = this.W.scalarmultiplication(-1.0)
                .scalarmultiplication(s)
                .add(this.U.scalarmultiplication(left))
                .add(this.V.scalarmultiplication(top));

        return r.normalize();
    }


    public Vector3 getWorldposition() {
        return worldposition;
    }
}
