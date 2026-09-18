package io.anuke.koru.entities.types;

import io.anuke.koru.graphics.Draw;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;

import io.anuke.koru.entities.Entities;
import io.anuke.koru.entities.Entity;
import io.anuke.koru.graphics.Drawf;
import io.anuke.koru.items.ItemStack;
import io.anuke.koru.network.Net;
import io.anuke.koru.network.SyncType;
import io.anuke.koru.network.syncing.Interpolator;

public class ItemDrop extends Entity{

	{
		hasVelocity = true;
		drag = 0.1f;

		syncType = SyncType.position;
		interpolator = new Interpolator();
	}

	@Override
	public void update(){
		super.update();

		//magnetic pickup, server-side only
		if(!Net.server()) return;

		Entities.instance.getNearby(pos().x, pos().y, 30,
				other -> other.inventory != null && !other.inventory.full(),
				(other) -> {
					float height = 3f;
					float dst = other.pos().dst(pos().x, pos().y - height);

					if(dst > 2f){
						velocity.add(Tmp.v1.set(other.pos().x - pos().x,
								other.pos().y - (pos().y - height)).nor().scl(0.2f * Time.delta));
					}

					if(dst < 2f){
						other.inventory.addItem(stack);
						other.inventory.sendUpdate(other);
						Entities.remove(this);
						// TODO Effects.effect(Fx.itempickup, pos().x, pos().y - 0.5f); once Effects subsystem ported
					}
				});
	}

	@Override
	public void draw(){
		if(Net.server()) return;

		String itemname = stack.item.name();
		String region = Drawf.hasRegion(itemname + "chunk") ? itemname + "chunk" : itemname + "item";

		Draw.z(pos().y);

		Draw.rect(region, pos().x, pos().y - 2);

		Drawf.shadow(this, 6, 0);
	}

	public static void create(float x, float y, ItemStack[] stacks){
		int stepsize = 5;
		float range = 5;

		for(ItemStack stack : stacks){

			int amount = stack.amount;
			while(amount >= 0){
				ItemDrop item = new ItemDrop();
				item.stack = new ItemStack(stack.item, amount > stepsize ? stepsize : amount);
				item.pos().set(x + Mathf.random(-range, range), y + Mathf.random(-range, range));
				amount -= stepsize;

				item.add();
				Net.sendEntity(item);
			}
		}
	}
}
