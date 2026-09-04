package cn.jstxjf_.rte.gfxext.multipart.mi;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.multipart.MultipartContainer;
import cn.jstxjf_.rte.gfxext.multipart.PartBase;
import cn.jstxjf_.rte.gfxext.reuse.AtlasManager;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MiLoader {

    public static MultipartContainer loadModel(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager, ResourceLocation objLocation) throws IOException {
        final JsonObject configData = RTE.JSON_PARSER.parse(ResourceUtil.readResource(resourceManager, objLocation)).getAsJsonObject();
        final JsonObject miData = RTE.JSON_PARSER.parse(ResourceUtil.readResource(resourceManager, ResourceUtil.resolveRelativePath(objLocation, configData.get("miProject").getAsString(), ""))).getAsJsonObject();
        final float timelineFps = configData.get("timelineFps").getAsFloat();

        final HashMap<String, MiPart> idToParts = new HashMap<>();
        final HashMap<String, MiPart> nameToParts = new HashMap<>();
        for (JsonElement part : miData.get("timelines").getAsJsonArray()) {
            final JsonObject partObject = part.getAsJsonObject();
            final MiPart miPart = new MiPart();
            for (Map.Entry<String, JsonElement> keyFrame : partObject.get("keyframes").getAsJsonObject().entrySet()) {
                final float time = Float.parseFloat(keyFrame.getKey()) / timelineFps;
                final JsonObject keyFrameObject = keyFrame.getValue().getAsJsonObject();
                miPart.translateX.spline.put(time, frameValue(keyFrameObject, "POS_X") / 16);
                miPart.translateZ.spline.put(time, frameValue(keyFrameObject, "POS_Y") / 16);
                miPart.translateY.spline.put(time, frameValue(keyFrameObject, "POS_Z") / 16);
                final float deg2Rad = (float) (Math.PI / 180F);
                miPart.rotateX.spline.put(time, frameValue(keyFrameObject, "ROT_X") * deg2Rad);
                miPart.rotateZ.spline.put(time, frameValue(keyFrameObject, "ROT_Y") * deg2Rad);
                miPart.rotateY.spline.put(time, frameValue(keyFrameObject, "ROT_Z") * deg2Rad);
            }
            miPart.name = partObject.get("name").getAsString();
            idToParts.put(partObject.get("id").getAsString(), miPart);
        }

        for (JsonElement part : miData.get("timelines").getAsJsonArray()) {
            final JsonObject partObject = part.getAsJsonObject();
            final MiPart miPart = idToParts.get(partObject.get("id").getAsString());
            final String parent = partObject.get("parent").getAsString();
            if (!StringUtils.isEmpty(parent) && !parent.equals("root")) {
                miPart.parent = idToParts.get(parent);
            }
        }

        final MultipartContainer container = new MultipartContainer();
        container.parts.addAll(idToParts.values());
        container.topologicalSort();
        for (PartBase part : container.parts) {
            final MiPart miPart = (MiPart) part;
            if (!StringUtils.isEmpty(miPart.name)) {
                if (miPart.parent != null && !StringUtils.isEmpty(((MiPart) miPart.parent).name)) {
                    miPart.name = ((MiPart) miPart.parent).name + "." + miPart.name;
                }
                nameToParts.put(miPart.name, miPart);

                if (configData.get("models").getAsJsonObject().has(miPart.name)) {
                    final JsonObject modelObject = configData.get("models").getAsJsonObject().get(miPart.name).getAsJsonObject();
                    final Vector3f offset = modelObject.has("offset") ? parseVectorValue(modelObject.get("offset").getAsString()) : new Vector3f(0, 0, 0);
                    final Vector3f translation = modelObject.has("position") ? parseVectorValue(modelObject.get("position").getAsString()) : new Vector3f(0, 0, 0);
                    final RawModel model;
                    if (modelObject.has("model")) {
                        model = modelManager.loadRawModel(resourceManager, ResourceUtil.resolveRelativePath(objLocation, modelObject.get("model").getAsString(), ""), atlasManager);
                        final Vector3f pivot = modelObject.has("pivot") ? parseVectorValue(modelObject.get("pivot").getAsString()) : new Vector3f(0, 0, 0);
                        model.applyTranslation(-pivot.x(), -pivot.y(), -pivot.z());
                        offset.add(pivot);
                    } else {
                        model = null;
                    }
                    miPart.setModel(model, modelManager);
                    miPart.internalOffset = offset;
                    miPart.externalOffset = translation;
                }
            }
        }

        for (JsonElement copy : configData.get("copyKeyframes").getAsJsonArray()) {
            final JsonObject copyObject = copy.getAsJsonObject();
            final Vector3f sourceSpan = parseVectorValue(copyObject.get("src").getAsString());
            final float destinationBegin = copyObject.get("dest").getAsFloat();
            final Vector3f mirror = parseVectorValue(copyObject.get("mirror").getAsString());
            final boolean mirrorX = mirror.x() != 0;
            final JsonObject modelReplace = copyObject.get("modelReplace").getAsJsonObject();
            for (Map.Entry<String, MiPart> entry : idToParts.entrySet()) {
                final MiPart sourcePart = entry.getValue();
                final MiPart destinationPart = modelReplace.has(sourcePart.name) ? nameToParts.get(modelReplace.get(sourcePart.name).getAsString()) : sourcePart;
                copyKeyFrames(sourcePart.translateX, destinationPart.translateX, sourceSpan, destinationBegin, mirrorX ? -1 : 1, 0);
                copyKeyFrames(sourcePart.translateY, destinationPart.translateY, sourceSpan, destinationBegin, 1, 0);
                copyKeyFrames(sourcePart.translateZ, destinationPart.translateZ, sourceSpan, destinationBegin, 1, 0);
                copyKeyFrames(sourcePart.rotateX, destinationPart.rotateX, sourceSpan, destinationBegin, 1, 0);
                copyKeyFrames(sourcePart.rotateY, destinationPart.rotateY, sourceSpan, destinationBegin, mirrorX ? -1 : 1, 0);
                copyKeyFrames(sourcePart.rotateZ, destinationPart.rotateZ, sourceSpan, destinationBegin, mirrorX ? -1 : 1, 0);
            }
        }

        return container;
    }

    private static Vector3f parseVectorValue(String value) {
        final String[] tokens = value.split(",");
        return new Vector3f(
            tokens.length >= 1 ? Float.parseFloat(tokens[0].trim()) : 0,
            tokens.length >= 2 ? Float.parseFloat(tokens[1].trim()) : 0,
            tokens.length >= 3 ? Float.parseFloat(tokens[2].trim()) : 0
        );
    }

    private static float frameValue(JsonObject keyFrameObject, String element) {
        return keyFrameObject.has(element) ? keyFrameObject.get(element).getAsFloat() : 0F;
    }

    private static void copyKeyFrames(MiPart.FloatSpline sourceSpline, MiPart.FloatSpline destinationSpline, Vector3f sourceSpan, float destinationBegin, float mul, float add) {
        final HashMap<Float, Float> tempMap = new HashMap<>();
        for (Map.Entry<Float, Float> entry : sourceSpline.spline.entrySet()) {
            if (entry.getKey() < sourceSpan.x()) {
                continue;
            }
            if (entry.getKey() > sourceSpan.y()) {
                break;
            }
            tempMap.put(entry.getKey() - sourceSpan.x() + destinationBegin, entry.getValue() * mul + add);
        }
        destinationSpline.spline.putAll(tempMap);
    }
}
