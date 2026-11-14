package cn.jstxjf_.rte.mapping.holder;

public final class Camera extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.client.Camera> {

    public Camera(net.minecraft.client.Camera p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Camera cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Camera((net.minecraft.client.Camera) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.client.Camera;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public void reset() {
        ((net.minecraft.client.Camera) this.data).reset();
    }

    public float getPitch() {
        return ((net.minecraft.client.Camera) this.data).getXRot();
    }

    public float getYaw() {
        return ((net.minecraft.client.Camera) this.data).getYRot();
    }

    public boolean isThirdPerson() {
        return ((net.minecraft.client.Camera) this.data).isDetached();
    }

    public cn.jstxjf_.rte.mapping.holder.Entity getFocusedEntity() {
        return new cn.jstxjf_.rte.mapping.holder.Entity(((net.minecraft.client.Camera) this.data).getEntity());
    }

    public cn.jstxjf_.rte.mapping.holder.Vector3d getPos() {
        return new cn.jstxjf_.rte.mapping.holder.Vector3d(((net.minecraft.client.Camera) this.data).getPosition());
    }

    public void updateEyeHeight() {
        ((net.minecraft.client.Camera) this.data).tick();
    }

    public void update(cn.jstxjf_.rte.mapping.holder.BlockView p0, cn.jstxjf_.rte.mapping.holder.Entity p1, boolean p2, boolean p3, float p4) {
        ((net.minecraft.client.Camera) this.data).setup(((net.minecraft.world.level.BlockGetter) p0.data), ((net.minecraft.world.entity.Entity) p1.data), p2, p3, p4);
    }

    public boolean isReady() {
        return ((net.minecraft.client.Camera) this.data).isInitialized();
    }

    public Camera() {
        super(new net.minecraft.client.Camera());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockPos getBlockPos() {
        return new cn.jstxjf_.rte.mapping.holder.BlockPos(((net.minecraft.client.Camera) this.data).getBlockPosition());
    }
}
