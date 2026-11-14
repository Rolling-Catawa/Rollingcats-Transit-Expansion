package cn.jstxjf_.rte.mapping.holder;

public final class MapColor extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.material.MapColor> {

    public MapColor(net.minecraft.world.level.material.MapColor p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new MapColor((net.minecraft.world.level.material.MapColor) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.material.MapColor;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaLightBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_LIGHT_BLUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25706Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WARPED_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaOrangeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_ORANGE);
    }

    public int getIdMapped() {
        return ((net.minecraft.world.level.material.MapColor) this.data).id;
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getLightBlueGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.CLAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getBlackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_BLACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getStoneGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.STONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getEmeraldGreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.EMERALD);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaGreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_GREEN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getPalePurpleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.ICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25707Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WARPED_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaLimeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_LIGHT_GREEN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getDiamondBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.DIAMOND);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getWaterBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaYellowMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_YELLOW);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getBrightRedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.FIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getYellowMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getMagentaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_MAGENTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getCyanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_CYAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getOakTanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaLightGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_LIGHT_GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getOffWhiteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.QUARTZ);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getLightBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_LIGHT_BLUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getLightGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_LIGHT_GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_BLUE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getWhiteGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getDirtBrownMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.DIRT);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25704Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.CRIMSON_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25702Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.CRIMSON_NYLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getGoldMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.GOLD);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_GRAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_BLUE);
    }

    public int getColorMapped() {
        return ((net.minecraft.world.level.material.MapColor) this.data).col;
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaWhiteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_WHITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getLapisBlueMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.LAPIS);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getIronGrayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.METAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getOrangeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_ORANGE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getBrownMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_BROWN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getPinkMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_PINK);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaBlackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_BLACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getDarkRedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.NETHER);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaPinkMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_PINK);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getPaleGreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.GRASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getSpruceBrownMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.PODZOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getPurpleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_PURPLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getGreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_GREEN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25708Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WARPED_WART_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaBrownMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_BROWN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaPurpleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_PURPLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getWhiteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.SNOW);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getDarkGreenMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.PLANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25705Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.WARPED_NYLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaRedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_RED);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaCyanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_CYAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getField25703Mapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.CRIMSON_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getClearMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.NONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getRedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_RED);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getTerracottaMagentaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_MAGENTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getPaleYellowMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.SAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.MapColor getLimeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.MapColor(net.minecraft.world.level.material.MapColor.COLOR_LIGHT_GREEN);
    }
}
