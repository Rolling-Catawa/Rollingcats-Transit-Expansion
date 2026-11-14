package cn.jstxjf_.rte.gfxext.multipart.animated;

import cn.jstxjf_.rte.gfx.math.Matrix4f;

import java.util.HashMap;

public class AnimatedPartStates {

    public HashMap<Long, Double> funcResults = new HashMap<>();
    public HashMap<Long, Long> partUpdateTimes = new HashMap<>();
    public HashMap<Long, Matrix4f> partTransforms = new HashMap<>();
    public HashMap<Long, Integer> partStates = new HashMap<>();

    private static Long funcIdMax = -1L;
    private static Long partIdMax = -1L;

    public static Long getNewFuncId() {
        return ++funcIdMax;
    }

    public static Long getNewPartId() {
        return ++partIdMax;
    }

}
