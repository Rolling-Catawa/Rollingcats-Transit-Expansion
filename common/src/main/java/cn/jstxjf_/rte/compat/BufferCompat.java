package cn.jstxjf_.rte.compat;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class BufferCompat {

    private BufferCompat() {
    }

    public static BufferBuilder begin(VertexFormat.Mode mode, VertexFormat format) {
        //#if MC>=12101
        return Tesselator.getInstance().begin(mode, format);
        //#else
        final BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(mode, format);
        return builder;
        //#endif
    }

    public static void draw(BufferBuilder builder) {
        //#if MC>=12101
        final com.mojang.blaze3d.vertex.MeshData meshData = builder.build();
        if (meshData != null) {
            BufferUploader.drawWithShader(meshData);
        }
        //#else
        BufferUploader.drawWithShader(builder.end());
        //#endif
    }
}
