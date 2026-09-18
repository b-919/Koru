package io.anuke.koru.graphics;

import arc.graphics.g2d.TextureRegion;
import arc.graphics.g2d.Draw;
import arc.graphics.Color;

public class SpriteFacet extends Facet{
	public TextureRegion sprite;
	public Color color = new Color(Color.white);

	public SpriteFacet(String region){
		this.sprite = io.anuke.koru.graphics.Draw.region(region);
	}

	public SpriteFacet(TextureRegion region){
		this.sprite = region;
	}

	public SpriteFacet(){
	}

	public SpriteFacet set(float x, float y, boolean center, boolean block){
		this.x = x;
		this.y = y;
		if(center){
			this.x -= sprite.width/2f;
			this.y -= sprite.height/2f;
		}
		if(block){
			this.layer = y;
		}
		return this;
	}

	public SpriteFacet region(TextureRegion region){
		this.sprite = region;
		return this;
	}

	public SpriteFacet add(float x, float y){
		this.x += x;
		this.y += y;
		return this;
	}

	public SpriteFacet centerX(){
		this.x -= sprite.width/2f;
		return this;
	}

	public SpriteFacet centerY(){
		this.y -= sprite.height/2f;
		return this;
	}

	public SpriteFacet center(){
		centerX();
		centerY();
		return this;
	}

	public SpriteFacet scale(float x, float y){
		// Stub for scaling, Arc Draw.rect handles width/height
		return this;
	}

	@Override
	public void draw(){
		Draw.color(color);
		Draw.rect(sprite, x + sprite.width/2f, y + sprite.height/2f);
	}

	@Override
	public void reset(){
		super.reset();
		sprite = null;
	}
}


