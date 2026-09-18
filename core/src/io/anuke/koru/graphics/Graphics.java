package io.anuke.koru.graphics;

import arc.Core;
import arc.graphics.gl.Shader;

public class Graphics {
    public static void addSurface(String name, int scale, int priority) {
        //stub for surface management
    }

    public static void shader(Shader shader) {
        arc.graphics.g2d.Draw.shader(shader);
    }

    public static void shader() {
        arc.graphics.g2d.Draw.shader();
    }

    public static void begin() {
        //in Arc, Draw.draw() or similar might be used, but usually it's automatic
    }

    public static void end() {
        //Draw.flush()
        arc.graphics.g2d.Draw.flush();
    }

    public static arc.math.geom.Vec2 mouseWorld() {
        return arc.Core.camera.unproject(arc.Core.input.mouseX(), arc.Core.input.mouseY());
    }

    public static void surface(String name) {
        //stub for surface management
    }

    public static void flushSurface() {
        //stub for surface flushing
    }
}
