package cn.jstxjf_.rte.mixin;

import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = OptimizedModel.ObjModel.class, remap = false)
public interface ObjModelAccessor {
    @Accessor("rawMeshes")
    List<RawMesh> rte$getRawMeshes();

    @Accessor("rawModel")
    RawModel rte$getRawModel();

    @Invoker("<init>")
    static OptimizedModel.ObjModel rte$create(List<RawMesh> rawMeshes, boolean flipTextureV, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        throw new AssertionError();
    }
}
