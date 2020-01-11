package main.shader;

import main.Light;
import main.Ray;
import main.util.Vector3;

import java.util.ArrayList;

public class Material {
    private Vector3 albedo;
    private double roughness;
    private double metalness;
    private double refractionIndex;

    private boolean isTransparent;
    private boolean isReflective;


    public Material(Vector3 material, double refractionIndex) {
        this.albedo = material.removeGamma();
        this.roughness = 0.01;
        this.metalness = 0.9;
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
    public Vector3 getReflectivity(){return albedo.scalarmultiplication(((1 - metalness) * 0.04) + metalness);}
    public void setAlbedo(Vector3 albedo){ this.albedo = albedo;}


    private Vector3 calculateColorOnRayHit(Ray ray, ArrayList<Light> lights, Vector3 reflectedColor, Vector3 localColor){

        Vector3 outputColor = new Vector3(0,0,0);
        Vector3 intersection = ray.getIntersectionPoint();
        Vector3 normal = ray.getNormal();

        for(Light light : lights){
            double brightness = light.getBrightness();
            Vector3 lightColor = light.getRgb().removeGamma();

            Vector3 V = ray.getRayDirection().scalarmultiplication(-1);
            Vector3 L = light.getPosition().subtract(intersection).normalize();
            Vector3 H = V.add(L).scalarmultiplication(0.5).normalize();

            double D = (roughness * roughness) / (Math.PI * Math.pow(((normal.scalar(H) * normal.scalar(H)) * (roughness * roughness - 1) + 1), 2));

            Vector3 F;
            if(isTransparent){
                double reflectivity = ray.getReflectionRate();
                F = new Vector3(reflectivity, reflectivity, reflectivity);
            }
            else{
                //Vector3 FNull = albedo.scalarmultiplication(((1 - metalness) * 0.04) + metalness);
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

            Vector3 diffus = albedo;
            Vector3 reflect = new Vector3(0,0,0);

            if(localColor != null){
                diffus = localColor.dotproduct(kd);
            }
            else{
                diffus = diffus.dotproduct(kd);
            }

            if(reflectedColor != null){
                reflect = reflectedColor.dotproduct(F);
            }
            else{
                reflect = reflect.dotproduct(F);
            }

            Vector3 lighting = ks.add(reflect).add(diffus);
            // lichtfarbe + lichtintensität + NdotL * "lighting"
            Vector3 output;
            if(isTransparent){
                output = lightColor.scalarmultiplication(brightness).scalarmultiplication(0.95).dotproduct(lighting);
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
