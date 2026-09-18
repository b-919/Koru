package io.anuke.koru.graphics.lsystems;

import arc.graphics.Color;
import java.util.HashMap;

public class LSystemData{
	public String axiom;
	public HashMap<Character, String> rules;
	public float swayscl, swayspace, swayphase, space, thickness, len;
	public int iterations;
	public Color start, end;

	public LSystemData(String axiom, HashMap<Character, String> rules, float swayscl, float swayspace, float swayphase, float space, float thickness, float len, int iterations, Color start, Color end){
		this.axiom = axiom;
		this.rules = rules;
		this.swayscl = swayscl;
		this.swayspace = swayspace;
		this.swayphase = swayphase;
		this.space = space;
		this.thickness = thickness;
		this.len = len;
		this.iterations = iterations;
		this.start = start;
		this.end = end;
	}
}


