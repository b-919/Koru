package io.anuke.koru.ui.mobile;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.util.*;

public class Joystick extends Element{
    private static final float deadzone = 0.15f;
    private final Color baseColor = new Color(0.08f, 0.08f, 0.08f, 0.38f);
    private final Color ringColor = new Color(1f, 1f, 1f, 0.35f);
    private final Color knobColor = new Color(1f, 1f, 1f, 0.55f);
    public final Vec2 vector = new Vec2();
    public boolean active;
    private int activePointer = -1;

    public Joystick(){
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                if(activePointer != -1) return true;

                activePointer = pointer;
                active = true;
                updateVector(x, y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                if(pointer == activePointer){
                    updateVector(x, y);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                if(pointer == activePointer){
                    reset();
                }
            }
        });
    }

    private void updateVector(float x, float y){
        float radius = Math.min(width, height) / 2f - 12f;
        float cx = width / 2f, cy = height / 2f;
        vector.set((x - cx) / radius, (y - cy) / radius);
        if(vector.len() < deadzone){
            vector.setZero();
        }else{
            vector.limit(1f);
        }
    }

    public void reset(){
        active = false;
        activePointer = -1;
        vector.setZero();
    }

    @Override
    public void draw(){
        float cx = x + width / 2f, cy = y + height / 2f;
        float radius = Math.min(width, height) / 2f;

        Draw.color(baseColor.r, baseColor.g, baseColor.b, baseColor.a * parentAlpha);
        Fill.circle(cx, cy, radius);

        Draw.color(ringColor.r, ringColor.g, ringColor.b, ringColor.a * parentAlpha);
        Fill.circle(cx, cy, radius * 0.78f);

        Draw.color(baseColor.r, baseColor.g, baseColor.b, baseColor.a * parentAlpha);
        Fill.circle(cx, cy, radius * 0.72f);

        float knobRadius = radius * 0.32f;
        float travel = radius - knobRadius - 6f;

        Draw.color(knobColor.r, knobColor.g, knobColor.b, knobColor.a * parentAlpha);
        Fill.circle(cx + vector.x * travel, cy + vector.y * travel, knobRadius);

        Draw.color();
    }
}
