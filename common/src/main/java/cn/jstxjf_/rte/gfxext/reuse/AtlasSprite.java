package cn.jstxjf_.rte.gfxext.reuse;

import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.Vertex;
import cn.jstxjf_.rte.gfxext.util.Logging;
import net.minecraft.resources.ResourceLocation;

public class AtlasSprite {

    public ResourceLocation sheet;
    public int sheetWidth, sheetHeight;
    public int frameX, frameY, frameWidth, frameHeight;
    public int spriteX, spriteY, spriteWidth, spriteHeight;
    public int sourceWidth, sourceHeight;
    public boolean rotated;

    public void applyToMesh(RawMesh mesh) {
        boolean uvBleeding = false;
        for (Vertex vertex : mesh.vertices) {
            final float spriteU = mapRange(vertex.u, (float)spriteX / sourceWidth, (float)(spriteX + spriteWidth) / sourceWidth, 0, 1);
            final float spriteV = mapRange(vertex.v, (float)spriteY / sourceHeight, (float)(spriteY + spriteHeight) / sourceHeight, 0, 1);
            if (spriteU < 0 || spriteU > 1 || spriteV < 0 || spriteV > 1) uvBleeding = true;
            if (rotated) {
                vertex.u = (frameX + (1 - spriteV) * frameHeight) / sheetWidth;
                vertex.v = (frameY + spriteU * frameWidth) / sheetHeight;
            } else {
                vertex.u = (frameX + spriteU * frameWidth) / sheetWidth;
                vertex.v = (frameY + spriteV * frameHeight) / sheetHeight;
            }
        }
        if (uvBleeding) {
            Logging.LOGGER.warn("UV bleeding into adjacent sprite in " + mesh.materialProp.texture);
        }
        mesh.materialProp.texture = sheet;
    }

    private static float mapRange(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public AtlasSprite(ResourceLocation sheet,
                       int sheetWidth, int sheetHeight,
                       int frameX, int frameY, int frameWidth, int frameHeight,
                       int spriteX, int spriteY, int spriteWidth, int spriteHeight,
                       int sourceWidth, int sourceHeight,
                       boolean rotated) {
        this.sheet = sheet;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
        this.frameX = frameX;
        this.frameY = frameY;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.rotated = rotated;
    }

}
