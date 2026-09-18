package io.anuke.koru.entities;

import io.anuke.koru.graphics.Draw;

public class ProjectileTypes{
	public static final ProjectileType

	bolt = new ProjectileType(){
		@Override
		public void draw(Entity spark){
			Draw.alpha(spark.ifract());
			Draw.rect("bolt", spark.pos().x, spark.pos().y, spark.velocity().angle() - 45);
			Draw.color();
		}
	},
	slash = new ProjectileType(){
		{
			lifetime = 12;
			speed = 2.4f;
			hitsize = 18f;
		}

		@Override
		public void draw(Entity spark){
			Draw.alpha(spark.ifract());
			Draw.rect("slash", spark.pos().x, spark.pos().y, spark.velocity().angle() - 45);
			Draw.color();
		}
	};
}


