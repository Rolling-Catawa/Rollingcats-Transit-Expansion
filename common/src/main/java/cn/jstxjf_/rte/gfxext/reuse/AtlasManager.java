package cn.jstxjf_.rte.gfxext.reuse;

import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.loader.ObjModelLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import cn.jstxjf_.rte.compat.McCompat;

class ResourceUtil {
    public static ResourceLocation resolveRelativePath(ResourceLocation base, String path, String ext) {
        String basePath = base.getPath();
        int lastSlash = basePath.lastIndexOf('/');
        String folder = lastSlash >= 0 ? basePath.substring(0, lastSlash + 1) : "";
        String fullPath = folder + path;
        if (ext != null && !fullPath.endsWith(ext)) fullPath += ext;
        return McCompat.id(base.getNamespace(), fullPath);
    }
    
    public static String readResource(ResourceManager resourceManager, ResourceLocation location) throws IOException {
        return cn.jstxjf_.rte.gfxext.util.ResourceUtil.readResource(resourceManager, location);
    }
}

public class AtlasManager {

    public HashMap<ResourceLocation, AtlasSprite> sprites = new HashMap<>();

    public HashSet<ResourceLocation> noAtlasList = new HashSet<>();
    
    private static final JsonParser JSON_PARSER = new JsonParser();

    public void load(ResourceManager resourceManager, ResourceLocation atlasConf) throws IOException {
        JsonObject atlasConfObj = JSON_PARSER.parse(ResourceUtil.readResource(resourceManager, atlasConf)).getAsJsonObject();
        String basePath = atlasConfObj.get("basePath").getAsString();
        for (JsonElement sheetObj : atlasConfObj.get("sheets").getAsJsonArray()) {
            ResourceLocation sheetConf = ResourceUtil.resolveRelativePath(atlasConf, sheetObj.getAsString(), ".json");
            ResourceLocation sheetTexture = ResourceUtil.resolveRelativePath(atlasConf, sheetObj.getAsString(), ".png");
            JsonObject sheetConfObj = JSON_PARSER.parse(ResourceUtil.readResource(resourceManager, sheetConf)).getAsJsonObject();
            int sheetWidth = sheetConfObj.get("meta").getAsJsonObject().get("size").getAsJsonObject().get("w").getAsInt();
            int sheetHeight = sheetConfObj.get("meta").getAsJsonObject().get("size").getAsJsonObject().get("h").getAsInt();
            for (Map.Entry<String, JsonElement> entry : sheetConfObj.get("frames").getAsJsonObject().entrySet()) {
                ResourceLocation texture = ResourceUtil.resolveRelativePath(sheetConf, basePath + entry.getKey(), ".png");
                JsonObject spriteObj = entry.getValue().getAsJsonObject();
                sprites.put(texture, new AtlasSprite(
                        sheetTexture, sheetWidth, sheetHeight,
                        spriteObj.get("frame").getAsJsonObject().get("x").getAsInt(), spriteObj.get("frame").getAsJsonObject().get("y").getAsInt(),
                        spriteObj.get("frame").getAsJsonObject().get("w").getAsInt(), spriteObj.get("frame").getAsJsonObject().get("h").getAsInt(),
                        spriteObj.get("spriteSourceSize").getAsJsonObject().get("x").getAsInt(), spriteObj.get("spriteSourceSize").getAsJsonObject().get("y").getAsInt(),
                        spriteObj.get("spriteSourceSize").getAsJsonObject().get("w").getAsInt(), spriteObj.get("spriteSourceSize").getAsJsonObject().get("h").getAsInt(),
                        spriteObj.get("sourceSize").getAsJsonObject().get("w").getAsInt(), spriteObj.get("sourceSize").getAsJsonObject().get("h").getAsInt(),
                        spriteObj.get("rotated").getAsBoolean()
                ));
            }
        }
        for (JsonElement noAtlasObj : atlasConfObj.get("noAtlas").getAsJsonArray()) {
            ResourceLocation noAtlasTexture = ResourceUtil.resolveRelativePath(atlasConf, basePath + noAtlasObj.getAsString(), ".png");
            noAtlasList.add(noAtlasTexture);
        }
    }

    public void applyToMesh(RawMesh mesh) {
        if (mesh.materialProp.texture == null || noAtlasList.contains(mesh.materialProp.texture)) return;
        AtlasSprite sprite = sprites.getOrDefault(mesh.materialProp.texture, null);
        if (sprite != null) sprite.applyToMesh(mesh);
    }

    public void clear() {
        sprites.clear();
    }

}
