package sample;

public class Camera {

    private int l = -Main.WIDTH /  2;
    private int b = -Main.HEIGHT / 2;
    private int r = l * -1;
    private int t = b * -1;

    private Vector3 worldposition;
    private Vector3 lookat;
    private Vector3 view;

    private Vector3 UPRotated;
    private Vector3 UP;
    private Vector3 U;
    private Vector3 W;
    private Vector3 V;
    private double roll;

    private double d;
    private Vector3 W_d_negated;


    public Camera(){
        this.roll = 0;
        this.worldposition = new Vector3(0,0,-10);
        this.lookat = new Vector3(0.0, 0.0, 1);
        this.UP = new Vector3(0,1,0);

        this.view = lookat.subtract(worldposition).normalize();
        this.UPRotated = new Vector3(Math.sin(roll), Math.cos(roll), 0);

        this.W = worldposition.subtract(view).normalize();
        this.U = UPRotated.cross(view).normalize();
        this.V = U.cross(W).normalize();

        d = t/Math.tan(Math.PI/4)/2;
        W_d_negated = W.scalarmultiplication(d*-1);
    }

    // GETTERS AND SETTERS


    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }

    public int getR() {
        return r;
    }

    public int getB() {
        return b;
    }

    public Vector3 getWorldposition() {
        return worldposition;
    }

    public Vector3 getView() {
        return view;
    }

    public Vector3 getUP() {
        return UP;
    }

    public Vector3 getU() {
        return U;
    }

    public Vector3 getV() {
        return V;
    }

    public Vector3 getW() {
        return W;
    }

    public Vector3 getW_d_negated() {
        return W_d_negated;
    }
}
