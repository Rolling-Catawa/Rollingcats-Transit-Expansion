package cn.jstxjf_.rte.data;

import cn.jstxjf_.rte.RTEClient;
import cn.jstxjf_.rte.gfx.math.Vector3f;
import cn.jstxjf_.rte.gfx.model.Model;
import cn.jstxjf_.rte.gfx.vertex.VertAttrType;
import cn.jstxjf_.rte.gfxext.model.RawMesh;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.model.Vertex;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import cn.jstxjf_.rte.scripting.ScriptHolderBase;

import java.io.Closeable;
import java.io.IOException;

public class RailModelProperties {

    public MutableComponent name;
    public String key;
    public String group;
    public String path;

    public ScriptHolderBase script;

    public RawModel rawModel;
    public Model uploadedModel;

    public Long boundingBox;
    public float repeatInterval;

    public float yOffset;

    public RailModelProperties(String key, MutableComponent name, RawModel rawModel, float repeatInterval, float yOffset, ScriptHolderBase script, String group) {
        this.name = name;
        this.key = key;
        this.group = group;
        this.path = group + "/" + key;

        this.yOffset = yOffset;
        this.script = script;

        if (rawModel == null) {
            this.rawModel = null;
            this.uploadedModel = null;
            this.boundingBox = 0L;
            this.repeatInterval = repeatInterval;
            return;
        }

        rawModel.clearAttrState(VertAttrType.COLOR);
        rawModel.applyRotation(new Vector3f(0.577f, 0.577f, 0.577f), (float)Math.toRadians(1));
        this.rawModel = rawModel;
        uploadedModel = RTEClient.modelManager.uploadModel(rawModel);

        float yMin = 0f, yMax = 0f;
        for (RawMesh mesh : rawModel.meshList.values()) {
            for (Vertex vertex : mesh.vertices) {
                yMin = Math.min(yMin, vertex.position.y() + yOffset);
                yMax = Math.max(yMax, vertex.position.y() + yOffset);
            }
        }
        boundingBox = ((long)Float.floatToIntBits(yMin) << 32) | (Float.floatToIntBits(yMax) & 0xFFFFFFFFL);

        this.repeatInterval = repeatInterval;
    }
}
