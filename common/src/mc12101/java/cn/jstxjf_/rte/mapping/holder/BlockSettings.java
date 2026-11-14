package cn.jstxjf_.rte.mapping.holder;

public final class BlockSettings extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.state.BlockBehaviour.Properties> {

    public BlockSettings(net.minecraft.world.level.block.state.BlockBehaviour.Properties p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.BlockSettings cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new BlockSettings((net.minecraft.world.level.block.state.BlockBehaviour.Properties) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.state.BlockBehaviour.Properties;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings jumpVelocityMultiplier(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).jumpFactor(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings strength(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).strength(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings strength(float p0, float p1) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).strength(p0, p1));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings dropsNothing() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).noLootTable());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings velocityMultiplier(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).speedFactor(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings breakInstantly() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).instabreak());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings slipperiness(float p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).friction(p0));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings nonOpaque() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).noOcclusion());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings air() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).air());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings ticksRandomly() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).randomTicks());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings dropsLike(cn.jstxjf_.rte.mapping.holder.Block p0) {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).dropsLike(((net.minecraft.world.level.block.Block) p0.data)));
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings dynamicBounds() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).dynamicShape());
    }

    public cn.jstxjf_.rte.mapping.holder.BlockSettings noCollision() {
        return new cn.jstxjf_.rte.mapping.holder.BlockSettings(((net.minecraft.world.level.block.state.BlockBehaviour.Properties) this.data).noCollission());
    }
}
