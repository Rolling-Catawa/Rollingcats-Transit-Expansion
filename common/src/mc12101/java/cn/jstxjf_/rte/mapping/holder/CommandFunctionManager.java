package cn.jstxjf_.rte.mapping.holder;

public final class CommandFunctionManager extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.server.ServerFunctionManager> {

    public CommandFunctionManager(net.minecraft.server.ServerFunctionManager p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.CommandFunctionManager cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new CommandFunctionManager((net.minecraft.server.ServerFunctionManager) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.server.ServerFunctionManager;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public CommandFunctionManager(cn.jstxjf_.rte.mapping.holder.MinecraftServer p0, net.minecraft.server.ServerFunctionLibrary p1) {
        super(new net.minecraft.server.ServerFunctionManager(((net.minecraft.server.MinecraftServer) p0.data), p1));
    }

    public void tick() {
        ((net.minecraft.server.ServerFunctionManager) this.data).tick();
    }
}
