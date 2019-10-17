package sample;

public class Camera {

    Vector3 worldposition;
    Vector3 UP;
    Vector3 rightvector;

    Vector3 lookat;
    Vector3 view;

    public Camera(){
        this.worldposition = new Vector3(0.0,0.0,0.0);
        this.UP = new Vector3(0.0, 1.0, 0.0);


    }

}
