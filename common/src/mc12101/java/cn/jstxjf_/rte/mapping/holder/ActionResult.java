package cn.jstxjf_.rte.mapping.holder;

public enum ActionResult {
    SUCCESS(net.minecraft.world.InteractionResult.SUCCESS),
    CONSUME(net.minecraft.world.InteractionResult.CONSUME),
    CONSUME_PARTIAL(net.minecraft.world.InteractionResult.CONSUME_PARTIAL),
    PASS(net.minecraft.world.InteractionResult.PASS),
    FAIL(net.minecraft.world.InteractionResult.FAIL);

    public final net.minecraft.world.InteractionResult data;

    ActionResult(net.minecraft.world.InteractionResult data) {
        this.data = data;
    }

    public static ActionResult convert(net.minecraft.world.InteractionResult data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.ActionResult p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.ActionResult success(boolean p0) {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(net.minecraft.world.InteractionResult.sidedSuccess(p0));
    }

    public static cn.jstxjf_.rte.mapping.holder.ActionResult getConsumeMapped() {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(net.minecraft.world.InteractionResult.CONSUME);
    }

    public static cn.jstxjf_.rte.mapping.holder.ActionResult getFailMapped() {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(net.minecraft.world.InteractionResult.FAIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.ActionResult getPassMapped() {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(net.minecraft.world.InteractionResult.PASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.ActionResult getSuccessMapped() {
        return cn.jstxjf_.rte.mapping.holder.ActionResult.convert(net.minecraft.world.InteractionResult.SUCCESS);
    }
}
