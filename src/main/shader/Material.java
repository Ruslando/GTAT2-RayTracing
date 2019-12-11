package main.shader;

import main.Light;
import main.Ray;
import main.util.Vector3;

import java.util.ArrayList;

import static java.lang.Double.NaN;

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
    public Vector3 getFNull() { return albedo.scalarmultiplication((1 - metalness) * 0.04 + metalness); }
    public double getTransmission() { return 1 - getFNull().getX(); }

    public Vector3 getOutputColor(Ray ray, ArrayList<Light> lights, int reflectionDepth, int refractionDepth){
        if(reflectionDepth > 4) return new Vector3(0,0,0);
        if(refractionDepth > 7) return new Vector3(0,0,0);
        Vector3 outputColor = new Vector3(0,0,0);
        Vector3 intersection = ray.getIntersectionPoint();
        Vector3 normal = ray.getShape().getNormal(intersection);
        Vector3 reflectedColor = null;
        Vector3 refractedColor = null;

        double Fs = 0;
        double Fp = 0;
        double Fr = 0;
        double Ft = 0;

        if(getTransmission() != 0) {
            double i1 = ray.getCurrentTransmission();
            double i2 = getTransmission();
            double i = i1/i2;
            double a = ray.getRayDirection().scalar(normal);
            if(1-i*i*(1-a*a) < 0) {
                // Totalreflexion
                Fr = 1;
                Ft = 0;
            }
            else {
                double b = Math.sqrt((1-i*i*(1-a*a)));
                Fs = Math.pow((i1*a-i2*b)/(i1*a+i2*b), 2);
                Fp = Math.pow((i2*a-i1*b)/(i2*a+i1*b),2);
                Fr = (Fs + Fp)/2.0;
                Ft = 1 - Fr;
                Vector3 refractionRayDirection = ray.getRayDirection().scalarmultiplication(i).add(i*a-b).multiply(normal);
                Vector3 refractionRayStartPosition = intersection.add(normal.scalarmultiplication(0.001));
                Ray refractionRay = new Ray(refractionRayStartPosition, refractionRayDirection, ray.getShapes());
                refractionRay.shootRay();
                if(refractionRay.hasIntersected()) {
                    refractedColor = refractionRay.getShape().getMaterial().getOutputColor(refractionRay, lights, reflectionDepth, refractionDepth++);
                }
            }
        }

        if(metalness != 0) // reflektierend
        {
            Vector3 reflectionRayDirection =
                    ray.getRayDirection().subtract(normal.scalarmultiplication(2).multiply(normal.multiply(ray.getRayDirection()))).normalize();
            Vector3 reflectionRayStartPosition = intersection; //kein offset
            Ray reflectionRay = new Ray(reflectionRayStartPosition, reflectionRayDirection, ray.getShapes());
            reflectionRay.shootRay();
            if(reflectionRay.hasIntersected() && reflectionRay.getShape() != ray.getShape()) {
                Vector3 reflected = reflectionRay.getShape().getMaterial().getOutputColor(reflectionRay, lights, reflectionDepth++, refractionDepth);
                reflectedColor = getFNull().scalarmultiplication(-1).add(1).multiply(albedo).add(getFNull().multiply(reflected));
            }
            else {

            }
        }


        for(Light light : lights){
            Vector3 shadowRayStartPosition = intersection.add(normal.scalarmultiplication(0.001));
            Vector3 shadowRayDirection = light.getPosition().subtract(intersection.add(normal.scalarmultiplication(0.1))).normalize();
            Ray shadowRay = new Ray(shadowRayStartPosition, shadowRayDirection, ray.getShapes());
            shadowRay.shootRay();
            if(shadowRay.hasIntersected()) {

            }
            else {
                double brightness = light.getBrightness();
                Vector3 lightColor = light.getRgb().removeGamma();

                Vector3 V = ray.getRayDirection().scalarmultiplication(-1);
                Vector3 L = light.getPosition().subtract(intersection).normalize();
                Vector3 H = V.add(L).scalarmultiplication(0.5).normalize();

                double D = (roughness * roughness) / (Math.PI * Math.pow(((normal.scalar(H) * normal.scalar(H)) * (roughness * roughness - 1) + 1), 2));

                if(Fr == 0 && Ft == 0) {
                    Vector3 FNull = albedoToUse.scalarmultiplication((1 - metalness) * 0.04 + metalness);
                    Vector3 F = FNull.add(new Vector3(1,1,1).add(FNull.scalarmultiplication(-1)).scalarmultiplication(Math.pow((1 - normal.scalar(V)), 5)));
                    Fr = F;
                }



                double G = normal.scalar(V) / ((normal.scalar(V) * (1 - (roughness / 2)) + (roughness / 2))
                        * normal.scalar(L) / (normal.scalar(L) * (1 - (roughness / 2)) + (roughness / 2)));
                Vector3 ks = F.scalarmultiplication(D * G);
                double kd = (1-0.04) * (1 - metalness); // alternativ für 0.04 ks benutzen

                Vector3 output = lightColor.multiply(albedoToUse.scalarmultiplication(kd).add(ks)).scalarmultiplication(brightness * (normal.scalar(L))).addGamma();
                Vector3 newoutput = lightColor.scalarmultiplication(brightness).scalarmultiplication(normal.scalar(L));
                Vector3 newoutput2 = D * F * G + F *
                outputColor = outputColor.add(output);
            }
        }

        return outputColor;
    }
}
