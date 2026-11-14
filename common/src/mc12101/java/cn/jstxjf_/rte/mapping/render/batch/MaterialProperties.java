package cn.jstxjf_.rte.mapping.render.batch;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import org.lwjgl.opengl.GL33;
import cn.jstxjf_.rte.mapping.holder.Identifier;
import cn.jstxjf_.rte.mapping.holder.MinecraftClient;
import cn.jstxjf_.rte.mapping.holder.RenderLayer;
import cn.jstxjf_.rte.mapping.holder.VertexFormats;
import cn.jstxjf_.rte.mapping.mapper.OptimizedModel;
import cn.jstxjf_.rte.mapping.mapper.RenderLayerHelper;
import cn.jstxjf_.rte.mapping.render.vertex.VertexAttributeState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MaterialProperties {

	private Identifier texture;
	public final OptimizedModel.ShaderType shaderType;
	public final VertexAttributeState vertexAttributeState;
	public final boolean translucent;
	public final boolean writeDepthBuf;
	public final boolean cutoutHack;

	private static final Function<Identifier, RenderLayer> ENTITY_TRANSLUCENT_CULL = Util.memoize((texture) -> RenderLayerHelper.createTriangles(
			"entity_translucent_cull_triangles",
			VertexFormats.getPositionColorTextureOverlayLightNormalMapped(),
			256,
			true,
			true,
			RenderLayer.getEntityTranslucentCull(texture)
	));
	private static final BiFunction<Identifier, Boolean, RenderLayer> BEACON_BEAM = Util.memoize((texture, translucent) -> RenderLayerHelper.createTriangles(
			"beacon_beam_triangles",
			VertexFormats.getPositionColorTextureOverlayLightNormalMapped(),
			256,
			false,
			translucent,
			RenderLayer.getBeaconBeam(texture, translucent)
	));
	private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT = Util.memoize((texture) -> RenderLayerHelper.createTriangles(
			"entity_cutout_triangles",
			VertexFormats.getPositionColorTextureOverlayLightNormalMapped(),
			256,
			true,
			false,
			RenderLayer.getEntityCutout(texture)
	));

	public MaterialProperties(OptimizedModel.ShaderType shaderType, Identifier texture, @Nullable Integer color) {
		this.shaderType = shaderType;
		this.texture = texture;
		switch (shaderType) {
			default:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case TRANSLUCENT:
				translucent = true;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case CUTOUT_BRIGHT:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, 15 << 4 | 15 << 20);
				break;
			case TRANSLUCENT_BRIGHT:
				translucent = true;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, 15 << 4 | 15 << 20);
				break;
			case CUTOUT_GLOWING:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = true;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case TRANSLUCENT_GLOWING:
				translucent = true;
				writeDepthBuf = false;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
		}
	}

	public void setupCompositeState() {
		RenderSystem.setShaderTexture(0, texture.data);

		if (translucent || cutoutHack) {
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		} else {
			RenderSystem.disableBlend();
		}

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL33.GL_LEQUAL);
		RenderSystem.enableCull();
		MinecraftClient.getInstance().getGameRendererMapped().getLightmapTextureManager().enable();
		MinecraftClient.getInstance().getGameRendererMapped().getOverlayTexture().setupOverlayColor();
		RenderSystem.depthMask(writeDepthBuf);
	}

	public RenderLayer getBlazeRenderType() {
		switch (shaderType) {
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				return ENTITY_TRANSLUCENT_CULL.apply(texture);
			case CUTOUT_GLOWING:
			case TRANSLUCENT_GLOWING:
				return BEACON_BEAM.apply(texture, translucent);
			default:
				return ENTITY_CUTOUT.apply(texture);
		}
	}

	public Identifier getTexture() {
		return texture;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof MaterialProperties)) {
			return false;
		}
		final MaterialProperties materialProperties = (MaterialProperties) object;
		return shaderType == materialProperties.shaderType &&
				Objects.equals(texture, materialProperties.texture) &&
				Objects.equals(vertexAttributeState, materialProperties.vertexAttributeState) &&
				translucent == materialProperties.translucent &&
				writeDepthBuf == materialProperties.writeDepthBuf &&
				cutoutHack == materialProperties.cutoutHack;
	}

	@Override
	public int hashCode() {
		return Objects.hash(shaderType, texture, vertexAttributeState, translucent, writeDepthBuf, cutoutHack);
	}
}
