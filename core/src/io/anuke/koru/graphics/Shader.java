package io.anuke.koru.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.g2d.TextureRegion;

public abstract class Shader extends arc.graphics.gl.Shader {
    public TextureRegion region;

    public Shader(String frag, String vert) {
        super(Core.files.internal("shaders/" + vert + ".vertex"), Core.files.internal("shaders/" + frag + ".fragment"));
    }

    public abstract void apply();

    @Override
    public void bind() {
        apply();
    }
}
