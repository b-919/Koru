package io.anuke.koru.graphics.lsystems;

import arc.graphics.*;
import arc.struct.*;

public class EvolutionData{
    public IEvaluator eval;
    public int generations;
    public float swayscale, swayphase, swayspace, defaultSpace, thickness;
    public boolean changeSpace;
    public Color start, end;
    public IntMap<String> defaultRules = new IntMap<>();
}


