package main.util;

public class Material {
    private Vector3 albedo;
    private double roughness;
    private double metalness;

    public Material(Vector3 material, double roughness, double metalness) {
        this.albedo = material;
        this.roughness = roughness;
        this.metalness = metalness;
    }

    public Vector3 getAlbedo() { return this.albedo; }
    public double getRoughness() { return this.roughness; }
    public double getMetalness() { return this.metalness;}
}
