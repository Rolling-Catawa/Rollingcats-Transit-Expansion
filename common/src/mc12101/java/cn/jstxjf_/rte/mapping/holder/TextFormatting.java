package cn.jstxjf_.rte.mapping.holder;

public enum TextFormatting {
    BLACK(net.minecraft.ChatFormatting.BLACK),
    DARK_BLUE(net.minecraft.ChatFormatting.DARK_BLUE),
    DARK_GREEN(net.minecraft.ChatFormatting.DARK_GREEN),
    DARK_AQUA(net.minecraft.ChatFormatting.DARK_AQUA),
    DARK_RED(net.minecraft.ChatFormatting.DARK_RED),
    DARK_PURPLE(net.minecraft.ChatFormatting.DARK_PURPLE),
    GOLD(net.minecraft.ChatFormatting.GOLD),
    GRAY(net.minecraft.ChatFormatting.GRAY),
    DARK_GRAY(net.minecraft.ChatFormatting.DARK_GRAY),
    BLUE(net.minecraft.ChatFormatting.BLUE),
    GREEN(net.minecraft.ChatFormatting.GREEN),
    AQUA(net.minecraft.ChatFormatting.AQUA),
    RED(net.minecraft.ChatFormatting.RED),
    LIGHT_PURPLE(net.minecraft.ChatFormatting.LIGHT_PURPLE),
    YELLOW(net.minecraft.ChatFormatting.YELLOW),
    WHITE(net.minecraft.ChatFormatting.WHITE),
    OBFUSCATED(net.minecraft.ChatFormatting.OBFUSCATED),
    BOLD(net.minecraft.ChatFormatting.BOLD),
    STRIKETHROUGH(net.minecraft.ChatFormatting.STRIKETHROUGH),
    UNDERLINE(net.minecraft.ChatFormatting.UNDERLINE),
    ITALIC(net.minecraft.ChatFormatting.ITALIC),
    RESET(net.minecraft.ChatFormatting.RESET);

    public final net.minecraft.ChatFormatting data;

    TextFormatting(net.minecraft.ChatFormatting data) {
        this.data = data;
    }

    public static TextFormatting convert(net.minecraft.ChatFormatting data) {
        return data == null ? null : values()[data.ordinal()];
    }

    public final boolean equals(cn.jstxjf_.rte.mapping.holder.TextFormatting p0) {
        return this == p0;
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting byColorIndex(int p0) {
        final var v0 = net.minecraft.ChatFormatting.getById(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(v0));
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting byCode(char p0) {
        final var v0 = net.minecraft.ChatFormatting.getByCode(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(v0));
    }

    public boolean isModifier() {
        return this.data.isFormat();
    }

    public static java.lang.String strip(java.lang.String p0) {
        final var v0 = net.minecraft.ChatFormatting.stripFormatting(p0);
        return (v0 == null ? null : v0);
    }

    public boolean isColor() {
        return this.data.isColor();
    }

    public java.lang.Integer getColorValue() {
        final var v0 = this.data.getColor();
        return (v0 == null ? null : v0);
    }

    public java.lang.String getName() {
        return this.data.getName();
    }

    public int getColorIndex() {
        return this.data.getId();
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting byName(java.lang.String p0) {
        final var v0 = net.minecraft.ChatFormatting.getByName(p0);
        return (v0 == null ? null : cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(v0));
    }

    public static java.util.Collection<java.lang.String> getNames(boolean p0, boolean p1) {
        return net.minecraft.ChatFormatting.getNames(p0, p1);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getRedMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.RED);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkPurpleMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_PURPLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getStrikethroughMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.STRIKETHROUGH);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkGreenMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_GREEN);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getYellowMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.YELLOW);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkGrayMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getAquaMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.AQUA);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getWhiteMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.WHITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getItalicMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.ITALIC);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkBlueMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_BLUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getGreenMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.GREEN);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkRedMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_RED);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getBlackMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.BLACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getBoldMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.BOLD);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getLightPurpleMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.LIGHT_PURPLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getGrayMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getGoldMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.GOLD);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getObfuscatedMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.OBFUSCATED);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getBlueMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.BLUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getResetMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.RESET);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getDarkAquaMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.DARK_AQUA);
    }

    public static cn.jstxjf_.rte.mapping.holder.TextFormatting getUnderlineMapped() {
        return cn.jstxjf_.rte.mapping.holder.TextFormatting.convert(net.minecraft.ChatFormatting.UNDERLINE);
    }
}
