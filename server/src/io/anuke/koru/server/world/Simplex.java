package io.anuke.koru.server.world;

public class Simplex{
    private long seed;

    public void setSeed(long seed){
        this.seed = seed;
    }

    public double octaveNoise2D(double octaves, double persistence, double scale, double x, double y){
        double total = 0.0;
        double frequency = scale;
        double amplitude = 1.0;
        double max = 0.0;

        for(int i = 0; i < octaves; i++){
            total += (arc.util.noise.Simplex.raw2d((int)seed, x * frequency, y * frequency) + 1.0) / 2.0 * amplitude;
            frequency *= 2.0;
            max += amplitude;
            amplitude *= persistence;
        }

        return total / max;
    }
}