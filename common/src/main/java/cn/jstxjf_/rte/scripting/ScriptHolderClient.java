package cn.jstxjf_.rte.scripting;

import cn.jstxjf_.rte.RTE;
import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.ClientConfig;
import cn.jstxjf_.rte.data.ConfigResponder;
import cn.jstxjf_.rte.data.ShapeSerializer;
import cn.jstxjf_.rte.scripting.compat.ClientDataCompat;
import cn.jstxjf_.rte.scripting.compat.RailCollisionScriptApi;
import cn.jstxjf_.rte.scripting.util.*;
import cn.jstxjf_.rte.scripting.util.client.*;
import cn.jstxjf_.rte.gfx.math.Matrices;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.integration.RawMeshBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.mtr.mod.block.IBlock;
import org.mtr.mod.client.IDrawing;
import org.mtr.mod.client.MinecraftClientData;
import org.mtr.mapping.mapper.MinecraftClientHelper;

public class ScriptHolderClient extends ScriptHolderBase {
    public ScriptHolderClient() {
        super("client");
    }

    public ScriptHolderClient(cn.jstxjf_.rte.scripting.engine.ScriptEngineType engineType) {
        super("client", engineType);
    }

    protected void appendImporter() {
        super.appendImporter();

        inject(ScriptResourceUtilClient.class, "Resources");
        inject(GraphicsTexture.class, "GraphicsTexture");
        inject(SoundHelper.class, "SoundHelper");
        inject(ParticleHelper.class, "ParticleHelper");
        inject(TickableSound.class, "TickableSound");
        inject(ConfigResponder.class, "ConfigResponder");
        inject(ClientConfig.class, "ClientConfig");
        inject(MinecraftClientUtil.class, "MinecraftClient");

        inject(AbstractDrawCalls.DrawCall.class, "DrawCall");
        inject(AbstractDrawCalls.ClusterDrawCall.class, "ClusterDrawCall");
        inject(AbstractDrawCalls.WorldDrawCall.class, "WorldDrawCall");

        eval("var ModelManager = Java.type('" + RTEClient.class.getName() + "').modelManager;");
        inject(RawModel.class, "RawModel");
        inject(RawMesh.class, "RawMesh");
        inject(RawMeshBuilder.class, "RawMeshBuilder");
        inject(ModelCluster.class, "ModelCluster");
        inject(DynamicModelHolder.class, "DynamicModelHolder");

        inject(ClientDataCompat.class, "MTRClientData");
        inject(RailCollisionScriptApi.class, "RailCollision");
        inject(cn.jstxjf_.rte.scripting.compat.TrainCollisionScriptApi.class, "TrainCollision");
        inject(MinecraftClientData.class, "MTRClientData4");
        inject(IBlock.class, "IBlock");
        inject(MinecraftClientHelper.class, "UtilitiesClient");
        inject(IDrawing.class, "IDrawing");

        try {
            String[] classesToLoad = {
                    "util.AddParticleHelper",
                    "particle.MadParticleOption",
                    "particle.SpriteFrom",
                    "command.inheritable.InheritableBoolean",
                    "particle.ParticleRenderTypes",
                    "particle.ChangeMode"
            };
            for (String classToLoad : classesToLoad) {
                Class<?> classToLoadClass = Class.forName("cn.ussshenzhou.madparticle." + classToLoad);
                inject(classToLoadClass, classToLoad.substring(classToLoad.lastIndexOf(".") + 1));
            }
            eval("var foundMadParticle = true;");
        } catch (ClassNotFoundException ignored) {
            eval("var foundMadParticle = false;");
        }
    }
}
