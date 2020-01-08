package main.shader;

import main.Light;
import main.Ray;
import main.util.Vector3;

import java.util.ArrayList;
import java.util.Random;

public class Material {
    private Vector3 albedo;
    private double roughness;
    private double metalness;

    public Material(Vector3 material, double roughness, double metalness) {
        this.albedo = material.removeGamma();
        this.roughness = roughness;
        this.metalness = metalness;
    }

    public Vector3 getAlbedo() { return this.albedo; }
    public double getRoughness() { return this.roughness; }
    public double getMetalness() { return this.metalness;}

    public Vector3 getOutputColor(Ray ray, ArrayList<Light> lights){

        Random r = new Random();

        Vector3 outputColor = new Vector3(0,0,0);
        Vector3 intersection = ray.getIntersectionPoint();
        Vector3 normal = ray.getShape().getNormal(intersection);

        for(Light light : lights){
            Vector3 shadowRayStartPosition = intersection.add(normal.scalarmultiplication(0.001));
            double shadowRayIntensity = 1;
            int shadowRayCount = 100;
            for(int x = 0; x < shadowRayCount; x++) {
                double xCoord = 0, yCoord = 0, zCoord = 0, d = 0;
                do {
                    xCoord = r.nextDouble() * 2.0 - 1.0;
                    yCoord = r.nextDouble() * 2.0 - 1.0;
                    zCoord = r.nextDouble() * 2.0 - 1.0;
                    d = xCoord * xCoord + yCoord * yCoord + zCoord * zCoord;
                } while (d > 1.0 && xCoord == 0 && yCoord == 0 && zCoord == 0);
                Vector3 offset = new Vector3(xCoord, yCoord, zCoord).normalize();
                Vector3 shadowRayDirection = light.getPosition().add(offset).normalize();
                Ray shadowRay = new Ray(shadowRayStartPosition, shadowRayDirection, ray.getShapes());
                shadowRay.shootRay();
                if(shadowRay.hasIntersected()) {
                    shadowRayIntensity -= 1./shadowRayCount;
                }
            }
            if(shadowRayIntensity == 0) { //alle Schattenstrahlen haben ein Obj auf dem Web zum Licht getroffen

            }
            else {
                double brightness = light.getBrightness();
                Vector3 lightColor = light.getRgb().removeGamma();

                Vector3 V = ray.getRayDirection().scalarmultiplication(-1);
                Vector3 L = light.getPosition().subtract(intersection).normalize();
                Vector3 H = V.add(L).scalarmultiplication(0.5).normalize();

                double D = (roughness * roughness) / (Math.PI * Math.pow(((normal.scalar(H) * normal.scalar(H)) * (roughness * roughness - 1) + 1), 2));

                Vector3 FNull = albedo.scalarmultiplication((1 - metalness) * 0.04 + metalness);
                Vector3 F = FNull.add(new Vector3(1,1,1).add(FNull.scalarmultiplication(-1)).scalarmultiplication(Math.pow((1 - normal.scalar(V)), 5)));

                double G = normal.scalar(V) / ((normal.scalar(V) * (1 - (roughness / 2)) + (roughness / 2))
                        * normal.scalar(L) / (normal.scalar(L) * (1 - (roughness / 2)) + (roughness / 2)));
                Vector3 ks = F.scalarmultiplication(D * G);
                double kd = (1-0.04) * (1 - metalness); // alternativ für 0.04 ks benutzen

                Vector3 output = lightColor.multiply(albedo.scalarmultiplication(kd).add(ks)).scalarmultiplication(brightness * (normal.scalar(L))).scalarmultiplication(shadowRayIntensity).addGamma();
                outputColor = outputColor.add(output);
            }
        }

        return outputColor;
    }
}
