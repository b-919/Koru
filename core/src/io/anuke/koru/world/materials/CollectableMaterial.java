package io.anuke.koru.world.materials;

import io.anuke.koru.entities.*;
import io.anuke.koru.world.*;

/** A material that is collected on interaction */
public abstract class CollectableMaterial extends Material{

    protected CollectableMaterial(String name, MaterialLayer type) {
        super(name, type);
        interactable = true;
    }

    @Override
    public void onInteract(Tile tile, Entity entity){
        entity.inventory.addItems(getDrops());
        entity.inventory.sendUpdate(entity);

        tile.setWall(Materials.air);
    }
}
