package main.util;

public class Material {
    private Vector3 material;

    public Material(Vector3 material) {
        this.material = material;
    }

    public Vector3 getMaterial() { return this.material; }
}
