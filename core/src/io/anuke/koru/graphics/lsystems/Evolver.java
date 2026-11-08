package io.anuke.koru.graphics.lsystems;

import arc.struct.*;

public class Evolver{
    public LSystemData[] evolve(EvolutionData data, int variants){
        LSystemData[] result = new LSystemData[variants];
        for(int i = 0; i < variants; i++){
            result[i] = new LSystemData("FX", new IntMap<>(data.defaultRules), data.swayscale, data.swayspace, data.swayphase, data.defaultSpace, data.thickness, 4f, 3, data.start, data.end);
        }
        return result;
    }
}


