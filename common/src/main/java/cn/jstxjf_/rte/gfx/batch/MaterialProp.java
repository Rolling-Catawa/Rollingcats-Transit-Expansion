package cn.jstxjf_.rte.gfx.batch;

import cn.jstxjf_.rte.gfx.shader.BlazeRenderType;
import cn.jstxjf_.rte.gfx.vertex.VertAttrState;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL33;
import cn.jstxjf_.rte.gfx.math.Matrix4f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;
import cn.jstxjf_.rte.compat.McCompat;

public class MaterialProp {

    public String shaderName = "";
    public ResourceLocation texture;

    public VertAttrState attrState = new VertAttrState();

    public boolean translucent = false;
    public boolean writeDepthBuf = true;

    public boolean cutoutHack = false;

    public boolean cull = true;

    public int sheetElementsU = 0;
    public int sheetElementsV = 0;

    public MaterialProp() {

    }
    public MaterialProp(String shaderName) {
        this.shaderName = shaderName;
        checkShaderName();
    }

    public MaterialProp(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        String content = new String(dis.readNBytes(len), StandardCharsets.UTF_8);
        JsonObject mtlObj = JsonParser.parseString(content).getAsJsonObject();
        this.shaderName = mtlObj.get("shaderName").getAsString();
        this.texture = mtlObj.get("texture").isJsonNull() ? null : McCompat.id(mtlObj.get("texture").getAsString());
        this.attrState.color = mtlObj.get("color").isJsonNull() ? null : mtlObj.get("color").getAsInt();
        this.attrState.lightmapUV = mtlObj.get("lightmapUV").isJsonNull() ? null : mtlObj.get("lightmapUV").getAsInt();
        this.translucent = mtlObj.has("translucent") && mtlObj.get("translucent").getAsBoolean();
        this.writeDepthBuf = mtlObj.has("writeDepthBuf") && mtlObj.get("writeDepthBuf").getAsBoolean();
        boolean isBillboard = mtlObj.has("billboard") && mtlObj.get("billboard").getAsBoolean();
        if (isBillboard) attrState.setMatixProcess(VertAttrState.BILLBOARD);
        this.cutoutHack = mtlObj.has("cutoutHack") && mtlObj.get("cutoutHack").getAsBoolean();
        checkShaderName();
    }

    private void checkShaderName() {
        if (shaderName == null) shaderName = "";
    }

    public static final ResourceLocation WHITE_TEXTURE_LOCATION = McCompat.id("minecraft:textures/misc/white.png");

    public void setupCompositeState() {
        if (texture != null) {
            RenderSystem.setShaderTexture(0, texture);
        } else {
            RenderSystem.setShaderTexture(0, WHITE_TEXTURE_LOCATION);
        }

        if (translucent || cutoutHack) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        } else {
            RenderSystem.disableBlend();
        }
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL33.GL_LEQUAL);
        if (cull) {
            RenderSystem.enableCull();
        } else {
            RenderSystem.disableCull();
        }
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
        RenderSystem.depthMask(writeDepthBuf);
    }

    public RenderType getBlazeRenderType() {
        RenderType result;
        ResourceLocation textureToUse = texture == null ? WHITE_TEXTURE_LOCATION : texture;
        checkShaderName();
        switch (shaderName) {
            case "rendertype_entity_cutout":
                result = cull
                    ? BlazeRenderType.entityCutout(textureToUse)
                    : BlazeRenderType.entityCutoutNoCull(textureToUse);
                break;
            case "rendertype_entity_translucent_cull":
                result = BlazeRenderType.entityTranslucentCull(textureToUse);
                break;
            case "rendertype_beacon_beam":
                result = BlazeRenderType.beaconBeam(textureToUse, translucent);
                break;
            default:
                result = BlazeRenderType.entityCutout(textureToUse);
                break;
        }
        return result;
    }

    public MaterialProp copy() {
        MaterialProp result = new MaterialProp();
        result.copyFrom(this);
        return result;
    }

    public void copyFrom(MaterialProp other) {
        this.shaderName = other.shaderName;
        this.texture = other.texture;
        this.attrState = other.attrState.copy();
        this.translucent = other.translucent;
        this.writeDepthBuf = other.writeDepthBuf;
        this.cutoutHack = other.cutoutHack;
        this.cull = other.cull;
        this.sheetElementsU = other.sheetElementsU;
        this.sheetElementsV = other.sheetElementsV;
    }

    public void serializeTo(DataOutputStream dos) throws IOException {
        JsonObject mtlObj = new JsonObject();
        mtlObj.addProperty("version", 2);
        mtlObj.addProperty("shaderName", shaderName);
        if (texture == null) {
            mtlObj.add("texture", new JsonNull());
        } else {
            mtlObj.addProperty("texture", texture.toString());
        }
        if (this.attrState.color == null) {
            mtlObj.add("color", new JsonNull());
        } else {
            mtlObj.addProperty("color", this.attrState.color);
        }
        if (this.attrState.lightmapUV == null) {
            mtlObj.add("lightmapUV", new JsonNull());
        } else {
            mtlObj.addProperty("lightmapUV", this.attrState.lightmapUV);
        }
        mtlObj.addProperty("translucent", this.translucent);
        mtlObj.addProperty("writeDepthBuf", this.writeDepthBuf);
        mtlObj.addProperty("billboard", this.attrState.useMatixProcess());
        mtlObj.addProperty("cutoutHack", this.cutoutHack);
        String content = mtlObj.toString();
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        dos.writeInt(contentBytes.length);
        dos.write(contentBytes);
    }

    public boolean useMatixProcess() {
        return attrState.useMatixProcess();
    }

    public void setMatixProcess(Function<Matrix4f, Matrix4f> matrixProces) {
        attrState.setMatixProcess(matrixProces);
    }
    
    @Override
    public String toString() {
        return String.format("{%s: %s%s}",
                texture == null ? "null" : texture.toString(), translucent ? " T-" : "", shaderName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialProp that = (MaterialProp) o;
        return translucent == that.translucent && writeDepthBuf == that.writeDepthBuf
                && cutoutHack == that.cutoutHack && cull == that.cull
                && sheetElementsU == that.sheetElementsU && sheetElementsV == that.sheetElementsV
                && Objects.equals(shaderName, that.shaderName) && Objects.equals(texture, that.texture)
                && Objects.equals(attrState, that.attrState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shaderName, texture, attrState, translucent, writeDepthBuf,
                cutoutHack, cull, sheetElementsU, sheetElementsV);
    }
}
