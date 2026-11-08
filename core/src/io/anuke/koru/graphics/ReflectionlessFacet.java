package io.anuke.koru.graphics;

import arc.func.Cons;

public class ReflectionlessFacet extends BaseFacet{

	public ReflectionlessFacet(float layer, Cons<BaseFacet> draw){
		super(layer, Sorter.object, draw);
	}
}
