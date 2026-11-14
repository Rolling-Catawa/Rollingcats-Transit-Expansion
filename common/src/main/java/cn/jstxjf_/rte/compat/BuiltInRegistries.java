package cn.jstxjf_.rte.compat;

//#if MC<11903
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class BuiltInRegistries {

    public static final DefaultedRegistry<Block> BLOCK = Registry.BLOCK;
    public static final DefaultedRegistry<Item> ITEM = Registry.ITEM;
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = Registry.PARTICLE_TYPE;

    private BuiltInRegistries() {
    }
}
//#endif
