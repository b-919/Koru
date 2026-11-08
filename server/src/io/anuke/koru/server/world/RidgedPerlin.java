package io.anuke.koru.server.world;

public class RidgedPerlin{
    private final int seed, octaves;

    public RidgedPerlin(int seed, int octaves, float lacunarity){
        this.seed = seed;
        this.octaves = octaves;
    }

    public float getValue(int x, int y, float frequency){
        return arc.util.noise.Ridged.noise2d(seed, x, y, octaves, 0.5, frequency);
    }
}