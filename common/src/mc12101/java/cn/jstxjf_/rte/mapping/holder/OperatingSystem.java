package cn.jstxjf_.rte.mapping.holder;

public enum OperatingSystem {
    LINUX(net.minecraft.Util.OS.LINUX),
    SOLARIS(net.minecraft.Util.OS.SOLARIS),
    WINDOWS(net.minecraft.Util.OS.WINDOWS),
    OSX(net.minecraft.Util.OS.OSX),
    UNKNOWN(net.minecraft.Util.OS.UNKNOWN);

    public final net.minecraft.Util.OS data;

    OperatingSystem(net.minecraft.Util.OS data) {
        this.data = data;
    }

    public static OperatingSystem convert(net.minecraft.Util.OS data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.OperatingSystem p0) {
        return this == p0;
    }

    public void open(java.net.URI p0) {
        this.data.openUri(p0);
    }

    public void open(java.lang.String p0) {
        this.data.openUri(p0);
    }

    public void open(java.io.File p0) {
        this.data.openFile(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getWindowsMapped() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.OS.WINDOWS);
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getUnknownMapped() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.OS.UNKNOWN);
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getSolarisMapped() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.OS.SOLARIS);
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getLinuxMapped() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.OS.LINUX);
    }

    public static cn.jstxjf_.rte.mapping.holder.OperatingSystem getOsxMapped() {
        return cn.jstxjf_.rte.mapping.holder.OperatingSystem.convert(net.minecraft.Util.OS.OSX);
    }
}
