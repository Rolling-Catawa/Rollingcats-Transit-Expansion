package cn.jstxjf_.rte.gfxext.multipart.animated;

import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.multipart.MultipartContainer;
import cn.jstxjf_.rte.gfxext.multipart.animated.script.FunctionScript;
import cn.jstxjf_.rte.gfxext.reuse.AtlasManager;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfxext.util.Logging;
import cn.jstxjf_.rte.gfxext.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Locale;

public class AnimatedLoader {

    public static MultipartContainer loadModel(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager, ResourceLocation objLocation) throws IOException {
        final AnimatedLoader loader = new AnimatedLoader();
        loader.load(resourceManager, modelManager, atlasManager, objLocation, new Vector3f(0, 0, 0));
        final StaticPart staticPart = new StaticPart(loader.staticModel, modelManager);
        loader.buildingContainer.parts.add(staticPart);
        return loader.buildingContainer;
    }

    private final MultipartContainer buildingContainer = new MultipartContainer();
    private final RawModel staticModel = new RawModel();

    private void load(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager, ResourceLocation objLocation, Vector3f translation) throws IOException {
        final String rawModelData = ResourceUtil.readResource(resourceManager, objLocation);
        final String[] rawModelLines = rawModelData.split("[\\r\\n]+");
        String section = "";

        AnimatedPart buildingPart = new AnimatedPart();
        Vector3f includeTranslation = new Vector3f(0, 0, 0);
        for (String line : rawModelLines) {
            if (line.contains(";")) {
                line = line.split(";", 2)[0];
            }
            final String trimLine = line.trim();
            if (StringUtils.isEmpty(trimLine)) {
                continue;
            }

            try {
                if (trimLine.startsWith("[") && trimLine.endsWith("]")) {
                    if (section.equals("object")) {
                        finishPart(buildingPart, modelManager, translation);
                        buildingPart = new AnimatedPart();
                    }
                    section = trimLine.substring(1, trimLine.length() - 1).trim().replace(" ", "").toLowerCase(Locale.ROOT);
                    if (section.equals("include")) {
                        includeTranslation = new Vector3f(0, 0, 0);
                    }
                    continue;
                }

                if (section.equals("object")) {
                    parseObjectLine(resourceManager, modelManager, atlasManager, objLocation, buildingPart, trimLine);
                } else if (section.equals("include")) {
                    includeTranslation = parseIncludeLine(resourceManager, modelManager, atlasManager, objLocation, translation, includeTranslation, trimLine);
                } else {
                    Logging.LOGGER.warn("Unsupported ANIMATED section: {}", section);
                }
            } catch (Exception ex) {
                Logging.LOGGER.error("Failed loading ANIMATED model {}, line \"{}\"", objLocation, line, ex);
            }
        }

        if (section.equals("object")) {
            finishPart(buildingPart, modelManager, translation);
        }

        staticModel.distinct();
    }

    private void finishPart(AnimatedPart buildingPart, ModelManager modelManager, Vector3f translation) {
        if (buildingPart.isStatic()) {
            buildingPart.bakeToStaticModel(staticModel, translation);
        } else if (buildingPart.rawStates != null && buildingPart.rawStates.length > 0) {
            buildingPart.uploadStates(modelManager, translation);
            buildingContainer.parts.add(buildingPart);
        }
    }

    private void parseObjectLine(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager, ResourceLocation objLocation, AnimatedPart buildingPart, String trimLine) throws IOException {
        if (!trimLine.contains("=")) {
            return;
        }

        final String[] tokens = trimLine.split("=", 2);
        if (tokens.length != 2) {
            return;
        }

        final String key = tokens[0].trim().toLowerCase(Locale.ROOT);
        final String value = tokens[1].trim().toLowerCase(Locale.ROOT);
        if (StringUtils.isEmpty(value)) {
            return;
        }

        switch (key) {
            case "position":
                buildingPart.externTranslation = parseVectorValue(value);
                break;
            case "states":
                final String[] states = value.split(",");
                buildingPart.rawStates = new RawModel[states.length];
                for (int i = 0; i < states.length; i++) {
                    final String currentState = states[i].trim().toLowerCase(Locale.ROOT);
                    if (StringUtils.isEmpty(currentState)) {
                        continue;
                    }
                    final ResourceLocation currentStateLocation = ResourceUtil.resolveRelativePath(objLocation, currentState, null);
                    final String currentStateExt = FilenameUtils.getExtension(currentState);
                    if (currentStateExt.equals("obj") || currentStateExt.equals("csv") || currentStateExt.equals("nmb")) {
                        buildingPart.rawStates[i] = modelManager.loadRawModel(resourceManager, currentStateLocation, atlasManager);
                    } else {
                        Logging.LOGGER.warn("Unsupported model format in ANIMATED: {}", currentState);
                    }
                }
                break;
            case "statefunction":
                buildingPart.stateFunction = new FunctionScript(value);
                break;
            case "translatexdirection":
                buildingPart.translateXDirection = parseVectorValue(value);
                break;
            case "translateydirection":
                buildingPart.translateYDirection = parseVectorValue(value);
                break;
            case "translatezdirection":
                buildingPart.translateZDirection = parseVectorValue(value);
                break;
            case "translatexfunction":
                buildingPart.translateXFunction = new FunctionScript(value);
                break;
            case "translateyfunction":
                buildingPart.translateYFunction = new FunctionScript(value);
                break;
            case "translatezfunction":
                buildingPart.translateZFunction = new FunctionScript(value);
                break;
            case "rotatexdirection":
                buildingPart.rotateXDirection = parseVectorValue(value);
                break;
            case "rotateydirection":
                buildingPart.rotateYDirection = parseVectorValue(value);
                break;
            case "rotatezdirection":
                buildingPart.rotateZDirection = parseVectorValue(value);
                break;
            case "rotatexfunction":
                buildingPart.rotateXFunction = new FunctionScript(value);
                break;
            case "rotateyfunction":
                buildingPart.rotateYFunction = new FunctionScript(value);
                break;
            case "rotatezfunction":
                buildingPart.rotateZFunction = new FunctionScript(value);
                break;
            case "rotatexdamping":
            case "rotateydamping":
            case "rotatezdamping":
                Logging.LOGGER.warn("ANIMATED command that isn't currently supported: {}", key);
                break;
            case "textureshiftxdirection":
            case "textureshiftydirection":
            case "textureshiftxfunction":
            case "textureshiftyfunction":
            case "trackfollowerfunction":
            case "textureoverride":
                break;
            case "refreshrate":
                buildingPart.refreshRateMillis = (int) (Float.parseFloat(value) * 1000F);
                break;
            default:
                Logging.LOGGER.warn("Unknown ANIMATED command in Object: {}", tokens[0]);
                break;
        }
    }

    private Vector3f parseIncludeLine(ResourceManager resourceManager, ModelManager modelManager, AtlasManager atlasManager, ResourceLocation objLocation, Vector3f translation, Vector3f includeTranslation, String trimLine) throws IOException {
        if (trimLine.contains("=")) {
            final String[] tokens = trimLine.split("=", 2);
            if (tokens.length != 2) {
                return includeTranslation;
            }

            final String key = tokens[0].trim().toLowerCase(Locale.ROOT);
            final String value = tokens[1].trim().toLowerCase(Locale.ROOT).replace("\\", "/");
            if (StringUtils.isEmpty(value)) {
                return includeTranslation;
            }

            if (key.equals("position")) {
                return parseVectorValue(value);
            }

            Logging.LOGGER.warn("Unknown ANIMATED command in Include: {}", tokens[0]);
            return includeTranslation;
        }

        final ResourceLocation currentStateLocation = ResourceUtil.resolveRelativePath(objLocation, trimLine, null);
        final String currentStateExt = FilenameUtils.getExtension(trimLine);
        if (currentStateExt.equals("obj") || currentStateExt.equals("csv") || currentStateExt.equals("nmb")) {
            final RawModel model = modelManager.loadRawModel(resourceManager, currentStateLocation, atlasManager).copy();
            model.sourceLocation = null;
            model.applyTranslation(translation.x() + includeTranslation.x(), translation.y() + includeTranslation.y(), translation.z() + includeTranslation.z());
            staticModel.append(model);
        } else if (currentStateExt.equals("animated")) {
            load(resourceManager, modelManager, atlasManager, currentStateLocation, new Vector3f(translation.x() + includeTranslation.x(), translation.y() + includeTranslation.y(), translation.z() + includeTranslation.z()));
        } else {
            Logging.LOGGER.warn("Unsupported model format in ANIMATED: {}", trimLine);
        }
        return includeTranslation;
    }

    private static Vector3f parseVectorValue(String value) {
        final String[] tokens = value.split(",");
        if (tokens.length != 3) {
            return new Vector3f(0, 0, 0);
        }
        return new Vector3f(Float.parseFloat(tokens[0].trim()), Float.parseFloat(tokens[1].trim()), Float.parseFloat(tokens[2].trim()));
    }
}
