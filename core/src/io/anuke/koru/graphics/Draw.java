package io.anuke.koru.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;

public class Draw{

	public static void z(float z){
		arc.graphics.g2d.Draw.z(z);
	}

	public static void alpha(float a){
		arc.graphics.g2d.Draw.alpha(a);
	}

	public static void color(){
		arc.graphics.g2d.Draw.color();
	}

	public static void color(float r, float g, float b){
		arc.graphics.g2d.Draw.color(r, g, b);
	}

	public static void color(float r, float g, float b, float a){
		arc.graphics.g2d.Draw.color(r, g, b, a);
	}

	public static void color(Color c){
		arc.graphics.g2d.Draw.color(c);
	}

	public static void color(Color c1, Color c2, float f){
		arc.graphics.g2d.Draw.color(c1, c2, f);
	}

	public static void rect(String name, float x, float y){
		arc.graphics.g2d.Draw.rect(region(name), x, y);
	}

	public static void rect(String name, float x, float y, float rot){
		arc.graphics.g2d.Draw.rect(region(name), x, y, rot);
	}

	public static void rect(String name, float x, float y, float w, float h){
		arc.graphics.g2d.Draw.rect(region(name), x, y, w, h);
	}

	public static void rect(String name, float x, float y, float rot, float w, float h){
		arc.graphics.g2d.Draw.rect(region(name), x, y, w, h, rot);
	}

	public static void rect(TextureRegion region, float x, float y){
		arc.graphics.g2d.Draw.rect(region, x, y);
	}

	public static void rect(TextureRegion region, float x, float y, float w, float h){
		arc.graphics.g2d.Draw.rect(region, x, y, w, h);
	}

	public static void grect(String name, float x, float y){
		arc.graphics.g2d.Draw.rect(region(name), x, y);
	}

	public static void grect(String name, float x, float y, float w, float h){
		arc.graphics.g2d.Draw.rect(region(name), x, y, w, h);
	}

	public static void crect(String name, float x, float y){
		arc.graphics.g2d.Draw.rect(region(name), x, y);
	}

	public static TextureRegion region(String name){
		return arc.Core.atlas == null ? null : arc.Core.atlas.getRegionMap().get(name);
	}

	public static boolean hasRegion(String name){
		return arc.Core.atlas.has(name);
	}

	public static void thickness(float t){
		arc.graphics.g2d.Lines.stroke(t);
	}

	public static void line(float x1, float y1, float x2, float y2){
		arc.graphics.g2d.Lines.line(x1, y1, x2, y2);
	}

	public static void reset(){
		arc.graphics.g2d.Draw.reset();
	}
}


