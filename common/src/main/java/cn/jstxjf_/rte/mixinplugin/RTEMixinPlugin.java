package cn.jstxjf_.rte.mixinplugin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RTEMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        final String disabled = System.getProperty("rte.disableMixins", "");
        if (disabled.isBlank()) {
            return true;
        }
        final String simpleName = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        for (final String name : disabled.split(",")) {
            if (name.trim().equals(simpleName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        final List<String> mixins = new ArrayList<>();
        if (isClassPresent("com.oracle.truffle.api.TruffleLanguage")) {
            mixins.add("FQTruffleLanguage");
        }
        if (isClassPresent("com.oracle.truffle.runtime.hotspot.HotSpotTruffleRuntimeAccess")) {
            mixins.add("FQHotSpotTruffleRuntimeAccess");
        }
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isClassPresent(String name) {
        return RTEMixinPlugin.class.getClassLoader().getResource(name.replace('.', '/') + ".class") != null;
    }
}
