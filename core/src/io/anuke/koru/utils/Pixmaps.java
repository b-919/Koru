package io.anuke.koru.utils;

import arc.graphics.Pixmap;
import arc.graphics.Color;

public class Pixmaps{
	public static Pixmap outline(Pixmap pixmap, Color color){
		//Stub
		return pixmap.copy();
	}

	public static Pixmap scale(Pixmap pixmap, int scale){
		Pixmap next = new Pixmap(pixmap.width * scale, pixmap.height * scale);
		for(int x = 0; x < pixmap.width; x++){
			for(int y = 0; y < pixmap.height; y++){
				int color = pixmap.get(x, y);
				for(int dx = 0; dx < scale; dx++){
					for(int dy = 0; dy < scale; dy++){
						next.set(x * scale + dx, y * scale + dy, color);
					}
				}
			}
		}
		return next;
	}
}


