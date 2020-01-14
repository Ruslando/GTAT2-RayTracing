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
    private double refractionIndex;

    private boolean isTransparent;
    private boolean isReflective;


    public Material(double refractionIndex) {
        this.albedo = new Vector3(1,1,1).removeGamma();
        this.roughness = 0.01;
        this.metalness = 0;
        this.refractionIndex = refractionIndex;

        isTransparent = true;
        isReflective = true;

    }

    public Material(Vector3 material, double roughness, double metalness){
        this.albedo = material.removeGamma();
        this.roughness = roughness;
        this.metalness = metalness;

        isTransparent = false;
        isReflective = metalness > 0;
    }

    public Vector3 getAlbedo() { return this.albedo; }
    public double getRoughness() { return this.roughness; }
    public double getMetalness() { return this.metalness;}
    public double getRefractionIndex() {return this.refractionIndex;}
    public boolean isTransparent(){return this.isTransparent;}
    public boolean isReflective(){return this.isReflective;}
    public void setAlbedo(Vector3 albedo){ this.albedo = albedo;}


    private Vector3 calculateColorOnRayHit(Ray ray, ArrayList<Light> lights, Vector3 reflectedColor, Vector3 localColor){

        Random r = new Random();

        Vector3 outputColor = new Vector3(0,0,0);
        Vector3 intersection = ray.getIntersectionPoint();
        Vector3 normal = ray.getNormal();

        for(Light light : lights){
            double brightness = light.getBrightness();
            Vector3 lightColor = light.getColorGamma();
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

            Vector3 F;
            double reflectivity = ray.getReflectionRate();

            if(isTransparent){
                F = new Vector3(reflectivity, reflectivity, reflectivity);
            }
            else{
                Vector3 FNull = albedo.scalarmultiplication(metalness).add((1 - metalness) * 0.04);
                F = FNull.add(new Vector3(1,1,1).subtract(FNull).scalarmultiplication(Math.pow((1 - normal.scalar(V)), 5)));
            }

            double G = (normal.scalar(V) / ((normal.scalar(V) * (1 - (roughness / 2)) + (roughness / 2)))
                    * (normal.scalar(L) / (normal.scalar(L) * (1 - (roughness / 2)) + (roughness / 2))));

                // Amount of albedo reflected light aka DFG
                Vector3 ks = F.scalarmultiplication(D * G);
                // Amount of diffused light
                Vector3 kd;
                if(isTransparent){
                    kd = new Vector3(1,1,1).subtract(F);
                }
                else{
                    kd = new Vector3(1,1,1).subtract(ks).scalarmultiplication(1 - metalness);
                }

                double nl = (normal.scalar(L));
                // diffus; localcol * (1-F) bzw. kd + spiegelung: F * reflectedColor + glanzlicht: D*F*G bzw. ks

                if(localColor == null){
                    localColor = albedo;
                }
                if(reflectedColor == null){
                    reflectedColor = new Vector3(0.7,0.7,0.7);
                }

                Vector3 diffus = localColor.dotproduct(kd);
                Vector3 reflect = reflectedColor.dotproduct(F);

                Vector3 lighting = ks.add(reflect).add(diffus);
                // lichtfarbe + lichtintensität + NdotL * "lighting"
                Vector3 output;
                if(isTransparent){
                    //output = lightColor.scalarmultiplication(brightness).dotproduct(lighting);
                    output = lighting.scalarmultiplication(255).removeGamma();
                }
                else{
                    output = lightColor.scalarmultiplication(brightness).scalarmultiplication(nl).dotproduct(lighting);
                }
                outputColor = outputColor.add(output);

        }

        return outputColor.clampMin(0).addGamma().clamp(0, 255);
    }

    public Vector3 getLocalColor(Ray ray, ArrayList<Light> lights, Vector3 reflectedColor, Vector3 localColor){
        return calculateColorOnRayHit(ray, lights, reflectedColor, localColor).scalarmultiplication(1/255.0);
    }


}
