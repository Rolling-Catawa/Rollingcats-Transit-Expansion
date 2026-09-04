package cn.jstxjf_.rte.gfx.shader;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.gfx.ContextCapability;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class PatchingResourceProvider implements ResourceProvider {

    private static final String SUFFIX_UNIFORM = "_rte_modelmat";
    private static final String SUFFIX_INSTANCED = "_rte_instmat";

    private final ResourceProvider source;
    private final boolean instanced;

    public PatchingResourceProvider(ResourceProvider source) {
        this(source, false);
    }

    public PatchingResourceProvider(ResourceProvider source, boolean instanced) {
        this.source = source;
        this.instanced = instanced;
    }

    private String suffix() {
        return instanced ? SUFFIX_INSTANCED : SUFFIX_UNIFORM;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation resourceLocation) {
        try {
            if (resourceLocation.getPath().contains(suffix()))
                resourceLocation = new ResourceLocation(resourceLocation.getNamespace(),
                        resourceLocation.getPath().replace(suffix(), ""));

            Optional<Resource> srcResource = source.getResource(resourceLocation);
            if (srcResource.isEmpty()) {
                return Optional.empty();
            }

            InputStream srcInputStream = srcResource.get().open();
            String returningContent;

            if (resourceLocation.getPath().endsWith(".json")) {
                String srcContent = IOUtils.toString(srcInputStream, StandardCharsets.UTF_8);
                JsonObject data = RTE.JSON_PARSER.parse(srcContent).getAsJsonObject();
                data.addProperty("vertex", data.get("vertex").getAsString() + suffix());

                JsonArray attribArray = data.get("attributes").getAsJsonArray();
                int dummyAttribCount = 6 - attribArray.size();
                for (int i = 0; i < dummyAttribCount; i++) {
                    attribArray.add("Dummy" + i);
                }
                attribArray.add("ModelMat");
                returningContent = data.toString();
                srcInputStream.close();
            } else if (resourceLocation.getPath().endsWith(".vsh")) {
                String srcContent = IOUtils.toString(srcInputStream, StandardCharsets.UTF_8);
                returningContent = patchVertexShaderSource(srcContent, instanced);
                srcInputStream.close();
            } else {
                return srcResource;
            }

            final InputStream newContentStream = new ByteArrayInputStream(returningContent.getBytes(StandardCharsets.UTF_8));
            return Optional.of(new Resource(srcResource.get().source(), () -> newContentStream));
        } catch (IOException e) {
            RTE.LOGGER.debug("PatchingResourceProvider error for {}", resourceLocation, e);
            return Optional.empty();
        }
    }

    public static String patchVertexShaderSource(String srcContent) {
        return patchVertexShaderSource(srcContent, false);
    }

    public static String patchVertexShaderSource(String srcContent, boolean instanced) {
        String[] contentParts = srcContent.split("void main");
        contentParts[0] = contentParts[0]
                .replace("uniform mat4 ModelViewMat;", "uniform mat4 ModelViewMat;\nin mat4 ModelMat;");
        if (ContextCapability.isGL4ES) {
            contentParts[0] = contentParts[0].replace("ivec2", "vec2");
        }
        final String modelPrefix = instanced ? "MODELMAT * " : "";
        final String normalPrefix = instanced ? "mat3(MODELMAT) * " : "";
        contentParts[1] = contentParts[1]
                .replaceAll("\\bPosition\\b", "(MODELVIEWMAT * " + modelPrefix + "vec4(Position, 1.0)).xyz")
                .replaceAll("\\bNormal\\b", "normalize(mat3(MODELVIEWMAT) * " + normalPrefix + "Normal)")
                .replace("ModelViewMat", "mat4(1.0)")
                .replace("MODELVIEWMAT", "ModelViewMat")
                .replace("MODELMAT", "ModelMat");
        return contentParts[0] + "void main" + contentParts[1];
    }
}
