package main;

import main.util.Vector3;

public class Light {
    private Vector3 position;
    private double brightness;
    private Vector3 rgb;
    private boolean quadraticDecay;

    public Light(Vector3 position, double brightness, Vector3 rgb, boolean quadraticDecay) {
        this.position = position;
        this.brightness = brightness;
        this.rgb = rgb;
        this.quadraticDecay = quadraticDecay;
    }

    public Vector3 getPosition() {
        return position;
    }

    public double getBrightness() {
        return brightness;
    }

    public Vector3 getRgb() {
        return rgb;
    }

    public boolean getQuadraticDecay() {
        return this.quadraticDecay;
    }
}
