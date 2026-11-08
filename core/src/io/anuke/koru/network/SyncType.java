package io.anuke.koru.network;

import io.anuke.koru.entities.*;
import io.anuke.koru.network.syncing.*;
import io.anuke.koru.traits.DirectionTrait.*;

public enum SyncType{
    position{
        public SyncData write(Entity entity){
            return new SyncData(entity, entity.pos.x, entity.pos.y);
        }

        public void read(SyncData data, Entity entity){
            if(entity.interpolator != null){
                entity.interpolator.push(entity, data.x(), data.y());
            }else{
                entity.pos.set(data.x(), data.y());
            }
        }
    },
    physics{
        public SyncData write(Entity entity){
            return new SyncData(entity, entity.pos.x, entity.pos.y);
        }

        public void read(SyncData data, Entity entity){
            if(entity.interpolator != null){
                entity.interpolator.push(entity, data.x(), data.y());
            }else{
                entity.pos.set(data.x(), data.y());
            }
        }
    },
    player{
        public SyncData write(Entity entity){
            return new SyncData(entity,
                    entity.pos.x, entity.pos.y,
                    entity.input == null ? 0 : entity.input.mouseangle,
                    entity.direction);
        }

        public void read(SyncData data, Entity entity){
            entity.direction = data.get(3);
            float x = data.get(0);
            float y = data.get(1);

            entity.walking = entity.pos.dst(x, y) > 0.05f;

            if(entity.input != null){
                entity.input.mouseangle = data.get(2);

            }
            if(entity.interpolator != null){
                entity.interpolator.push(entity, x, y);
            }else{
                entity.pos.set(x, y);
            }
        }
    };

    public abstract SyncData write(Entity entity);

    public abstract void read(SyncData buffer, Entity entity);

}


