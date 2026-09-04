package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.scripting.util.WrappedEntity;

@SuppressWarnings("unused")
public final class ScriptUseEvent {

    public final String box;
    public final int car;
    public final double x;
    public final double y;
    public final double z;
    public final double distance;
    public final double dirX;
    public final double dirY;
    public final double dirZ;
    public final WrappedEntity player;
    public final String item;
    public final String offhandItem;
    public final boolean sneaking;

    public ScriptUseEvent(String box, int car,
                           double x, double y, double z, double distance,
                           double dirX, double dirY, double dirZ,
                           WrappedEntity player, String item, String offhandItem, boolean sneaking) {
        this.box = box;
        this.car = car;
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
        this.dirX = dirX;
        this.dirY = dirY;
        this.dirZ = dirZ;
        this.player = player;
        this.item = item;
        this.offhandItem = offhandItem;
        this.sneaking = sneaking;
    }

    public String getBox() { return box; }
    public int getCar() { return car; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getDistance() { return distance; }
    public WrappedEntity getPlayer() { return player; }
    public String getItem() { return item; }
    public String getOffhandItem() { return offhandItem; }
    public boolean isSneaking() { return sneaking; }
}
