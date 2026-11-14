package cn.jstxjf_.rte.scripting.util;

import cn.jstxjf_.rte.gfx.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class WrappedEntity {
    public Entity entity;

    public WrappedEntity(Entity entity) {
        this.entity = entity;
    }

    public double getX() { return entity.getX(); }
    public double getY() { return entity.getY(); }
    public double getZ() { return entity.getZ(); }

    public Vector3f getLookAngle() {
        net.minecraft.world.phys.Vec3 v = entity.getLookAngle();
        return new Vector3f((float) v.x, (float) v.y, (float) v.z);
    }

    public Vector3f getPosition() {
        return new Vector3f((float) getX(), (float) getY(), (float) getZ());
    }

    public boolean isShiftKeyDown() {
        return entity.isShiftKeyDown();
    }

    public String getNBT() {
        CompoundTag tag = new CompoundTag();
        entity.saveWithoutId(tag);
        return (new JsonStringTagVisitor()).visit(tag);
    }
}
