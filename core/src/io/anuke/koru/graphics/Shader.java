package io.anuke.koru.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.g2d.*;

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
