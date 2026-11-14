package cn.jstxjf_.rte.mapping.holder;

public enum Hand {
    MAIN_HAND(net.minecraft.world.InteractionHand.MAIN_HAND),
    OFF_HAND(net.minecraft.world.InteractionHand.OFF_HAND);

    public final net.minecraft.world.InteractionHand data;

    Hand(net.minecraft.world.InteractionHand data) {
        this.data = data;
    }

    public static Hand convert(net.minecraft.world.InteractionHand data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.Hand p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.Hand getMainHandMapped() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Hand getOffHandMapped() {
        return cn.jstxjf_.rte.mapping.holder.Hand.convert(net.minecraft.world.InteractionHand.OFF_HAND);
    }
}
