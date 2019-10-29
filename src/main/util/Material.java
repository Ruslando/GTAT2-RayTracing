package main.util;

public class Material {
    private Vector3 albedo;

    public Material(Vector3 material) {
        this.albedo = material;
    }

    public Vector3 getAlbedo() { return this.albedo; }
}
