package sample;

public class Camera {

    Vector3 worldposition;
    Vector3 UP;
    Vector3 UPRotated;
    Vector3 right;
    double roll;

    Vector3 lookat;
    Vector3 view;

    public Camera(){
        this.roll = 0;
        this.worldposition = new Vector3(0.0,0.0,0.0);
        this.lookat = new Vector3(0.0, 0.0, 1.0);

        this.view = lookat.subtract(worldposition).normalize();
        this.UPRotated = new Vector3(Math.sin(roll), Math.cos(roll), 0);
        this.right = view.cross(UPRotated).normalize();
        this.UP = right.cross(view);

    }

}
