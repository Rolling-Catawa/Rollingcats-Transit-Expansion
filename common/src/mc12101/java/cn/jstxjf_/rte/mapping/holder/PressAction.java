package cn.jstxjf_.rte.mapping.holder;

public interface PressAction extends net.minecraft.client.gui.components.Button.OnPress {

    public void onPress2(cn.jstxjf_.rte.mapping.holder.ButtonWidget p0);

    public default void onPress(net.minecraft.client.gui.components.Button p0) {
        this.onPress2(new cn.jstxjf_.rte.mapping.holder.ButtonWidget(p0));
    }
}
