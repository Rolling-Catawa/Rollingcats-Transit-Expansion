package cn.jstxjf_.rte.mapping.holder;

public enum LightType {
    SKY(net.minecraft.world.level.LightLayer.SKY),
    BLOCK(net.minecraft.world.level.LightLayer.BLOCK);

    public final net.minecraft.world.level.LightLayer data;

    LightType(net.minecraft.world.level.LightLayer data) {
        this.data = data;
    }

    public static LightType convert(net.minecraft.world.level.LightLayer data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.LightType p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.LightType getBlockMapped() {
        return cn.jstxjf_.rte.mapping.holder.LightType.convert(net.minecraft.world.level.LightLayer.BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.LightType getSkyMapped() {
        return cn.jstxjf_.rte.mapping.holder.LightType.convert(net.minecraft.world.level.LightLayer.SKY);
    }
}
