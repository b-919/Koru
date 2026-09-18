package io.anuke.koru.graphics.lsystems;

import arc.graphics.Color;
import java.util.HashMap;

public class EvolutionData{
	public IEvaluator eval;
	public int generations;
	public float swayscale, swayphase, swayspace, defaultSpace, thickness;
	public boolean changeSpace;
	public Color start, end;
	public HashMap<Character, String> defaultRules = new HashMap<>();
}


