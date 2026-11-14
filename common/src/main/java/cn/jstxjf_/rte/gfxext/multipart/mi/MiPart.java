package cn.jstxjf_.rte.gfxext.multipart.mi;

import cn.jstxjf_.rte.gfxext.model.ModelCluster;
import cn.jstxjf_.rte.gfxext.model.RawModel;
import cn.jstxjf_.rte.gfxext.multipart.MultipartUpdateProp;
import cn.jstxjf_.rte.gfxext.multipart.PartBase;
import cn.jstxjf_.rte.gfxext.reuse.ModelManager;
import cn.jstxjf_.rte.gfx.math.Matrix4f;
import cn.jstxjf_.rte.gfx.math.Vector3f;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MiPart extends PartBase {

    private ModelCluster model;

    public String name;

    public Vector3f internalOffset = new Vector3f(0, 0, 0);
    public Vector3f externalOffset = new Vector3f(0, 0, 0);

    public FloatSpline translateX = new FloatSpline();
    public FloatSpline translateY = new FloatSpline();
    public FloatSpline translateZ = new FloatSpline();
    public FloatSpline rotateX = new FloatSpline();
    public FloatSpline rotateY = new FloatSpline();
    public FloatSpline rotateZ = new FloatSpline();

    private Matrix4f lastTransform;
    private boolean isVisible = true;

    public void setModel(RawModel rawModel, ModelManager modelManager) {
        model = rawModel == null ? null : modelManager.uploadVertArrays(rawModel);
    }

    @Override
    public void update(MultipartUpdateProp prop) {
        final Matrix4f result = new Matrix4f();
        final float time = prop.miKeyframeTime;

        result.translate(externalOffset.x(), externalOffset.y(), externalOffset.z());
        result.translate(translateX.getValue(time), translateY.getValue(time), translateZ.getValue(time));

        if (parent != null) {
            result.multiply(parent.getTransform(prop));
        }

        result.translate(internalOffset.x(), internalOffset.y(), internalOffset.z());
        result.rotateX(rotateX.getValue(time));
        result.rotateY(rotateY.getValue(time));
        result.rotateZ(rotateZ.getValue(time));

        lastTransform = result;
        isVisible = prop.miHiddenParts == null || (!prop.miHiddenParts.contains(name) && (parent == null || ((MiPart) parent).isVisible));
    }

    @Override
    public ModelCluster getModel(MultipartUpdateProp prop) {
        return isVisible ? model : null;
    }

    @Override
    public Matrix4f getTransform(MultipartUpdateProp prop) {
        return lastTransform;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    public static class FloatSpline {

        public final TreeMap<Float, Float> spline = new TreeMap<>();

        public float getValue(float key) {
            if (spline.isEmpty()) {
                return 0;
            }

            final Map.Entry<Float, Float> floorEntry = spline.floorEntry(key);
            final Map.Entry<Float, Float> ceilingEntry = spline.ceilingEntry(key);
            if (floorEntry == null) {
                return ceilingEntry.getValue();
            } else if (ceilingEntry == null) {
                return floorEntry.getValue();
            } else if (Objects.equals(floorEntry.getKey(), ceilingEntry.getKey())) {
                return floorEntry.getValue();
            } else {
                return floorEntry.getValue() + (ceilingEntry.getValue() - floorEntry.getValue()) * ((key - floorEntry.getKey()) / (ceilingEntry.getKey() - floorEntry.getKey()));
            }
        }
    }
}
