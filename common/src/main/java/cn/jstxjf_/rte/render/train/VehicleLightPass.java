package cn.jstxjf_.rte.render.train;

import cn.jstxjf_.rte.RTE;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.List;

public final class VehicleLightPass {

    public static final int MAX_LIGHTS = 12;

    public record Spot(float x, float y, float z, float range,
                       float dx, float dy, float dz, float cosOuter,
                       float r, float g, float b, float cosInner) {
    }

    private static final String VERTEX = """
        #version 150
        out vec2 texCoord;
        void main() {
            vec2 corner = vec2((gl_VertexID << 1) & 2, gl_VertexID & 2);
            texCoord = corner;
            gl_Position = vec4(corner * 2.0 - 1.0, 0.0, 1.0);
        }
        """;

    private static final String FRAGMENT = """
        #version 150
        uniform sampler2D SceneColor;
        uniform sampler2D SceneDepth;
        uniform mat4 InverseProjection;
        uniform mat4 InverseView;
        uniform int LightCount;
        uniform vec4 LightPosition[12];
        uniform vec4 LightDirection[12];
        uniform vec4 LightColor[12];
        uniform float SurfaceGain;
        uniform float SurfaceLift;
        uniform float Scattering;
        uniform float Anisotropy;
        in vec2 texCoord;
        out vec4 fragColor;

        const int STEPS = 16;
        const float NEAR_FADE_START = 0.25;
        const float NEAR_FADE_END = 1.2;
        const float PI = 3.14159265;

        float interleavedGradientNoise(vec2 pixel) {
            return fract(52.9829189 * fract(dot(pixel, vec2(0.06711056, 0.00583715))));
        }

        vec3 reconstruct(vec2 uv, float depth) {
            vec4 clip = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
            vec4 view = InverseProjection * clip;
            return (InverseView * vec4(view.xyz / view.w, 1.0)).xyz;
        }

        float coneFactor(vec3 towardsPoint, int i) {
            return smoothstep(LightDirection[i].w, LightColor[i].w, dot(towardsPoint, LightDirection[i].xyz));
        }

        float distanceFactor(float distance, float range) {
            float ratio = distance / range;
            float window = clamp(1.0 - ratio * ratio * ratio * ratio, 0.0, 1.0);
            return window * window / (1.0 + distance * distance * 0.04);
        }

        float phase(float cosTheta) {
            float g = Anisotropy;
            float denominator = 1.0 + g * g - 2.0 * g * cosTheta;
            return (1.0 - g * g) / (4.0 * PI * denominator * sqrt(denominator));
        }

        void main() {
            vec3 scene = texture(SceneColor, texCoord).rgb;
            float depth = texture(SceneDepth, texCoord).r;
            bool sky = depth >= 0.999999;
            vec3 position = reconstruct(texCoord, depth);
            vec3 rawNormal = cross(dFdx(position), dFdy(position));
            bool hasNormal = !sky && dot(rawNormal, rawNormal) > 1e-12;
            vec3 normal = hasNormal ? normalize(rawNormal) : vec3(0.0);
            if (dot(normal, position) > 0.0) {
                normal = -normal;
            }
            float viewDistance = sky ? 96.0 : length(position);
            vec3 viewDirection = normalize(position);
            float jitter = interleavedGradientNoise(gl_FragCoord.xy);

            vec3 surface = vec3(0.0);
            vec3 inscatter = vec3(0.0);
            for (int i = 0; i < LightCount; i++) {
                vec3 lightPosition = LightPosition[i].xyz;
                float range = LightPosition[i].w;
                if (hasNormal) {
                    vec3 toLight = lightPosition - position;
                    float distance = length(toLight);
                    if (distance < range && distance > 1e-4) {
                        vec3 l = toLight / distance;
                        surface += LightColor[i].rgb * coneFactor(-l, i) * distanceFactor(distance, range) * max(dot(normal, l), 0.0);
                    }
                }
                float along = dot(lightPosition, viewDirection);
                float offAxisSquared = dot(lightPosition, lightPosition) - along * along;
                if (offAxisSquared >= range * range) {
                    continue;
                }
                float halfChord = sqrt(range * range - offAxisSquared);
                float start = max(along - halfChord, 0.0);
                float end = min(along + halfChord, viewDistance);
                if (end <= start) {
                    continue;
                }
                float stepLength = (end - start) / float(STEPS);
                for (int k = 0; k < STEPS; k++) {
                    vec3 samplePoint = viewDirection * (start + (float(k) + jitter) * stepLength);
                    vec3 fromLight = samplePoint - lightPosition;
                    float distance = length(fromLight);
                    float nearFade = smoothstep(NEAR_FADE_START, NEAR_FADE_END, distance);
                    if (nearFade <= 0.0) {
                        continue;
                    }
                    vec3 travel = fromLight / distance;
                    float cone = coneFactor(travel, i);
                    if (cone <= 0.0) {
                        continue;
                    }
                    inscatter += LightColor[i].rgb * cone * nearFade * distanceFactor(distance, range) * phase(dot(travel, -viewDirection)) * stepLength;
                }
            }
            vec3 added = max(scene * surface * SurfaceGain + surface * SurfaceLift + inscatter * Scattering, vec3(0.0));
            if (any(isnan(added)) || any(isinf(added))) {
                added = vec3(0.0);
            }
            vec3 lit = scene + (vec3(1.0) - exp(-added * 1.2)) * (vec3(1.0) - scene * 0.5);
            fragColor = vec4(lit, 1.0);
        }
        """;

    private static int program = -1;
    private static int vertexArray;
    private static int copyTexture;
    private static int copyWidth;
    private static int copyHeight;
    private static boolean failed;
    private static Method irisPipelineManager;
    private static boolean irisAbsent;

    private VehicleLightPass() {
    }

    public static void render(List<Spot> spots, Matrix4f projection, Matrix4f viewRotation) {
        if (failed || spots.isEmpty()) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        final RenderTarget target = minecraft.getMainRenderTarget();
        final int width = target.width;
        final int height = target.height;
        final int depthTexture = depthTexture(target);
        if (depthTexture <= 0 || !ensureProgram()) {
            return;
        }

        final int previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        final int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        final int previousTexture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        final int previousTexture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        final int previousReadFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        final int previousDrawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        final int previousVertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        final boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        final boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        final boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        final int[] viewport = new int[4];
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        try {
            ensureCopyTexture(width, height);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, target.frameBufferId);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, copyTexture);
            GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);

            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target.frameBufferId);
            GL11.glViewport(0, 0, width, height);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDepthMask(false);
            GL20.glUseProgram(program);
            uploadUniforms(spots, projection, viewRotation);
            GL30.glBindVertexArray(vertexArray);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        } catch (Throwable e) {
            failed = true;
            RTE.LOGGER.warn("[VehicleLights] Lighting pass disabled: {}", e.toString());
        } finally {
            GL30.glBindVertexArray(previousVertexArray);
            GL20.glUseProgram(previousProgram);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture0);
            GL13.glActiveTexture(previousActiveTexture);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, previousReadFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, previousDrawFramebuffer);
            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            setEnabled(GL11.GL_BLEND, blend);
            setEnabled(GL11.GL_DEPTH_TEST, depthTest);
            setEnabled(GL11.GL_CULL_FACE, cull);
            GL11.glDepthMask(depthMask);
        }
    }

    private static void uploadUniforms(List<Spot> spots, Matrix4f projection, Matrix4f viewRotation) {
        final int count = Math.min(MAX_LIGHTS, spots.size());
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final FloatBuffer matrix = stack.mallocFloat(16);
            new Matrix4f(projection).invert().get(matrix);
            GL20.glUniformMatrix4fv(location("InverseProjection"), false, matrix);
            new Matrix4f(viewRotation).invert().get(matrix);
            GL20.glUniformMatrix4fv(location("InverseView"), false, matrix);
            final FloatBuffer positions = stack.mallocFloat(MAX_LIGHTS * 4);
            final FloatBuffer directions = stack.mallocFloat(MAX_LIGHTS * 4);
            final FloatBuffer colors = stack.mallocFloat(MAX_LIGHTS * 4);
            for (int i = 0; i < MAX_LIGHTS; i++) {
                final Spot spot = i < count ? spots.get(i) : null;
                positions.put(spot == null ? 0 : spot.x).put(spot == null ? 0 : spot.y).put(spot == null ? 0 : spot.z).put(spot == null ? 1 : spot.range);
                directions.put(spot == null ? 0 : spot.dx).put(spot == null ? 0 : spot.dy).put(spot == null ? 1 : spot.dz).put(spot == null ? 1 : spot.cosOuter);
                colors.put(spot == null ? 0 : spot.r).put(spot == null ? 0 : spot.g).put(spot == null ? 0 : spot.b).put(spot == null ? 1 : spot.cosInner);
            }
            positions.flip();
            directions.flip();
            colors.flip();
            GL20.glUniform4fv(location("LightPosition"), positions);
            GL20.glUniform4fv(location("LightDirection"), directions);
            GL20.glUniform4fv(location("LightColor"), colors);
        }
        GL20.glUniform1i(location("LightCount"), count);
        GL20.glUniform1i(location("SceneColor"), 0);
        GL20.glUniform1i(location("SceneDepth"), 1);
        GL20.glUniform1f(location("SurfaceGain"), 9.0F);
        GL20.glUniform1f(location("SurfaceLift"), 0.035F);
        GL20.glUniform1f(location("Scattering"), 0.25F);
        GL20.glUniform1f(location("Anisotropy"), 0.3F);
    }

    private static int location(String name) {
        return GL20.glGetUniformLocation(program, name);
    }

    private static boolean ensureProgram() {
        if (program > 0) {
            return true;
        }
        try {
            final int vertex = compile(GL20.GL_VERTEX_SHADER, VERTEX);
            final int fragment = compile(GL20.GL_FRAGMENT_SHADER, FRAGMENT);
            final int linked = GL20.glCreateProgram();
            GL20.glAttachShader(linked, vertex);
            GL20.glAttachShader(linked, fragment);
            GL20.glLinkProgram(linked);
            GL20.glDeleteShader(vertex);
            GL20.glDeleteShader(fragment);
            if (GL20.glGetProgrami(linked, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException(GL20.glGetProgramInfoLog(linked));
            }
            program = linked;
            final int previousVertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
            vertexArray = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(previousVertexArray);
            return true;
        } catch (Throwable e) {
            failed = true;
            RTE.LOGGER.warn("[VehicleLights] Lighting shader failed: {}", e.toString());
            return false;
        }
    }

    private static int compile(int type, String source) {
        final int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            final String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException(log);
        }
        return shader;
    }

    private static void ensureCopyTexture(int width, int height) {
        if (copyTexture != 0 && copyWidth == width && copyHeight == height) {
            return;
        }
        if (copyTexture == 0) {
            copyTexture = GL11.glGenTextures();
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, copyTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        copyWidth = width;
        copyHeight = height;
    }

    private static int depthTexture(RenderTarget target) {
        final int iris = irisDepthTexture();
        return iris > 0 ? iris : target.getDepthTextureId();
    }

    private static int irisDepthTexture() {
        if (irisAbsent) {
            return -1;
        }
        try {
            if (irisPipelineManager == null) {
                irisPipelineManager = Class.forName("net.irisshaders.iris.Iris").getMethod("getPipelineManager");
            }
            final Object manager = irisPipelineManager.invoke(null);
            final Object pipeline = manager.getClass().getMethod("getPipelineNullable").invoke(manager);
            if (pipeline == null) {
                return -1;
            }
            final java.lang.reflect.Field field = pipeline.getClass().getDeclaredField("renderTargets");
            field.setAccessible(true);
            final Object renderTargets = field.get(pipeline);
            return renderTargets == null ? -1 : (int) renderTargets.getClass().getMethod("getDepthTexture").invoke(renderTargets);
        } catch (ClassNotFoundException e) {
            irisAbsent = true;
            return -1;
        } catch (Throwable e) {
            return -1;
        }
    }

    private static void setEnabled(int capability, boolean enabled) {
        if (enabled) {
            GL11.glEnable(capability);
        } else {
            GL11.glDisable(capability);
        }
    }
}
