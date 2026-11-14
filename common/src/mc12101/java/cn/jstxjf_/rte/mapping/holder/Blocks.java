package cn.jstxjf_.rte.mapping.holder;

public final class Blocks extends cn.jstxjf_.rte.mapping.tool.HolderBase<net.minecraft.world.level.block.Blocks> {

    public Blocks(net.minecraft.world.level.block.Blocks p0) {
        super(p0);
    }

    public static cn.jstxjf_.rte.mapping.holder.Blocks cast(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return new Blocks((net.minecraft.world.level.block.Blocks) p0.data);
    }

    public static boolean isInstance(cn.jstxjf_.rte.mapping.tool.HolderBase<?> p0) {
        return p0 != null && p0.data instanceof net.minecraft.world.level.block.Blocks;
    }

    public boolean equals(java.lang.Object p0) {
        return p0 instanceof cn.jstxjf_.rte.mapping.tool.HolderBase && data.equals(((cn.jstxjf_.rte.mapping.tool.HolderBase<?>) p0).data);
    }

    public static void refreshShapeCache() {
        net.minecraft.world.level.block.Blocks.rebuildCache();
    }

    public Blocks() {
        super(new net.minecraft.world.level.block.Blocks());
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulSandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_SAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPackedIceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PACKED_ICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTripwireHookMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TRIPWIRE_HOOK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPoppyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POPPY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadHornCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_HORN_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getComposterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COMPOSTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedBambooMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_BAMBOO);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHoneyBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HONEY_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedDarkOakWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDragonEggMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DRAGON_EGG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBookshelfMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BOOKSHELF);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothQuartzStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_QUARTZ_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBrainCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BRAIN_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAnvilMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANVIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRepeaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REPEATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getActivatorRailMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACTIVATOR_RAIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBubbleCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BUBBLE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrackedPolishedBlackstoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedDarkOakLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_DARK_OAK_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFletchingTableMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FLETCHING_TABLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBeehiveMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BEEHIVE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBasaltMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BASALT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpawnerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPAWNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFireCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FIRE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedLilyOfTheValleyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_LILY_OF_THE_VALLEY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getShroomlightMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SHROOMLIGHT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChainCommandBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHAIN_COMMAND_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBedrockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BEDROCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherPortalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_PORTAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRailMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RAIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedFungusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_FUNGUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPodzolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PODZOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedWartBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_WART_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBubbleColumnMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BUBBLE_COLUMN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getVineMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.VINE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedSpruceSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_SPRUCE_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadFireCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_FIRE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedAlliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_ALLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedNyliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_NYLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedCrimsonStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getClayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CLAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTubeCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TUBE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGoldOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GOLD_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedOakWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDragonWallHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DRAGON_WALL_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedCrimsonHyphaeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_CRIMSON_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getComparatorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COMPARATOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrainCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRAIN_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothRedSandstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCoalOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COAL_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrassBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRASS_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWitherSkeletonSkullMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WITHER_SKELETON_SKULL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedSandstoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SANDSTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadFireCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_FIRE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedAcaciaWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCakeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CAKE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothQuartzSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_QUARTZ_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCactusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CACTUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrewingStandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BREWING_STAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedGraniteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_GRANITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedMushroomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_MUSHROOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSlimeBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SLIME_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedBirchWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedGraniteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_GRANITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedSandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetheriteBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownMushroomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getQuartzStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.QUARTZ_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDriedKelpBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DRIED_KELP_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTripwireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TRIPWIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRepeatingCommandBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REPEATING_COMMAND_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmokerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOKER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMelonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MELON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSeaLanternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SEA_LANTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLavaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LAVA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDiamondBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIAMOND_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedNetherBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_NETHER_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothStoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_STONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothStoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_STONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getVoidAirMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.VOID_AIR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLanternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LANTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledRedSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_RED_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadTubeCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_TUBE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherSproutsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_SPROUTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkPrismarineMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_PRISMARINE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBrainCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLilacMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LILAC);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedSandstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SANDSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyCobblestoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWitherSkeletonWallSkullMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WITHER_SKELETON_WALL_SKULL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBubbleCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BUBBLE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBarrierMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BARRIER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrainCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRAIN_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSkeletonWallSkullMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SKELETON_WALL_SKULL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFarmlandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FARMLAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDioriteWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIORITE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagmaBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGMA_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedAzureBluetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_AZURE_BLUET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChorusFlowerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHORUS_FLOWER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulFireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_FIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulLanternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_LANTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedRedTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_RED_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChorusPlantMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHORUS_PLANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedSpruceLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEnchantingTableMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ENCHANTING_TABLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedNetherBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_NETHER_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWetSpongeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WET_SPONGE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStructureBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRUCTURE_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPistonHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PISTON_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAndesiteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANDESITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMyceliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MYCELIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkPrismarineSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_PRISMARINE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherBrickFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_BRICK_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCartographyTableMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CARTOGRAPHY_TABLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAzureBluetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.AZURE_BLUET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getObsidianMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OBSIDIAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFireCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FIRE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlastFurnaceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLAST_FURNACE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBeetrootsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BEETROOTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpurSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPUR_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCutRedSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CUT_RED_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPistonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PISTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBrainCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BRAIN_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWitherRoseMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WITHER_ROSE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTubeCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TUBE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHornCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HORN_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSugarCaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SUGAR_CANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrainCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRAIN_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSnowMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SNOW);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getQuartzBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.QUARTZ_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getObserverMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OBSERVER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBubbleCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BUBBLE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyCobblestoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDaylightDetectorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DAYLIGHT_DETECTOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getQuartzPillarMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.QUARTZ_PILLAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getQuartzSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.QUARTZ_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBubbleCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledPolishedBlackstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_POLISHED_BLACKSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedAndesiteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedCobblestoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_COBBLESTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDioriteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIORITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCornflowerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CORNFLOWER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCraftingTableMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedAndesiteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJukeboxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUKEBOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonHyphaeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyStoneBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGildedBlackstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GILDED_BLACKSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTubeCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TUBE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCobblestoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COBBLESTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTurtleEggMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TURTLE_EGG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCommandBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COMMAND_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothRedSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_RED_SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGraniteWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRANITE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getZombieHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ZOMBIE_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedBirchLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_BIRCH_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueOrchidMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_ORCHID);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJunglePlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndStoneBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_STONE_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLapisBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LAPIS_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrackedStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRACKED_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTubeCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TUBE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWaterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WATER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMelonStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MELON_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSandstoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SANDSTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTrappedChestMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TRAPPED_CHEST);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJunglePressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadTubeCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_TUBE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChippedAnvilMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHIPPED_ANVIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDiamondOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIAMOND_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCarrotsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CARROTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedBrownMushroomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_BROWN_MUSHROOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpongeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPONGE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCreeperWallHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CREEPER_WALL_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCutSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CUT_SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulCampfireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_CAMPFIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDetectorRailMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DETECTOR_RAIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAncientDebrisMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANCIENT_DEBRIS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDioriteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIORITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGlowstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GLOWSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCoarseDirtMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COARSE_DIRT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneWireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_WIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGraniteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRANITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSandMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SAND);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCobblestoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COBBLESTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCryingObsidianMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRYING_OBSIDIAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAlliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ALLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLecternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LECTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHayBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HAY_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLilyOfTheValleyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LILY_OF_THE_VALLEY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherWartMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_WART);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedOrangeTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_ORANGE_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedRedMushroomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_RED_MUSHROOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedSpruceWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_SPRUCE_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHopperMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HOPPER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedBlueOrchidMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_BLUE_ORCHID);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPotatoesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTATOES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPumpkinStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PUMPKIN_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulSoilMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_SOIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightWeightedPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneLampMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_LAMP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIronBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.IRON_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedOakLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedPinkTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_PINK_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherGoldOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_GOLD_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedDeadBushMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_DEAD_BUSH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getQuartzBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.QUARTZ_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStonePressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLeverMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LEVER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBubbleCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BUBBLE_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTallSeagrassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TALL_SEAGRASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedGraniteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_GRANITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedCrackedStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_CRACKED_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPlayerHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PLAYER_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndRodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_ROD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndStoneBrickWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_STONE_BRICK_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSkeletonSkullMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SKELETON_SKULL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothRedSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_RED_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEnderChestMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ENDER_CHEST);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFernMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSeaPickleMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SEA_PICKLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLodestoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LODESTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSoulWallTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SOUL_WALL_TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMovingPistonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOVING_PISTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIronDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.IRON_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedStoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_STONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBarrelMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BARREL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAndesiteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANDESITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedNetherBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_NETHER_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWeepingVinesPlantMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WEEPING_VINES_PLANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGraniteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRANITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrackedNetherBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRACKED_NETHER_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedNetherBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_NETHER_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACKSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCarvedPumpkinMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CARVED_PUMPKIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadHornCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_HORN_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadHornCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_HORN_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedCrimsonFungusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_CRIMSON_FUNGUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFireCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FIRE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSunflowerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SUNFLOWER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCampfireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CAMPFIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedWhiteTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_WHITE_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHornCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HORN_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedDioriteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_DIORITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedChiseledStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_CHISELED_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIronTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.IRON_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getKelpPlantMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.KELP_PLANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEmeraldOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.EMERALD_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonFungusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_FUNGUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadFireCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_FIRE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWheatMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHEAT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherWartBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_WART_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCobblestoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COBBLESTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPumpkinMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PUMPKIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothQuartzMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_QUARTZ);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStickyPistonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STICKY_PISTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonRootsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_ROOTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedPoppyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_POPPY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyStoneBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDropperMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DROPPER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonNyliumMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_NYLIUM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothSandstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_SANDSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmoothSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMOOTH_SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAttachedPumpkinStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ATTACHED_PUMPKIN_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedDandelionMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_DANDELION);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSandstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SANDSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyStoneBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_STONE_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFireMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FIRE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAndesiteWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANDESITE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSprucePlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDragonHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DRAGON_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSmithingTableMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SMITHING_TABLE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDispenserMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DISPENSER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedCornflowerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_CORNFLOWER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherrackMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHERRACK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIronOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.IRON_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstoneBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGravelMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAVEL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndStoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_STONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLapisOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LAPIS_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCyanStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CYAN_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedTulipMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_TULIP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getConduitMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CONDUIT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDamagedAnvilMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DAMAGED_ANVIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBambooMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BAMBOO);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCocoaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COCOA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDandelionMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DANDELION);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBubbleCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BUBBLE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherQuartzOreMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLoomMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LOOM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCreeperHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CREEPER_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getZombieWallHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ZOMBIE_WALL_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTargetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TARGET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndPortalMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_PORTAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPlayerWallHeadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PLAYER_WALL_HEAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDioriteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIORITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackstoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACKSTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPrismarineWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PRISMARINE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedRootsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_ROOTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpurPillarMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPUR_PILLAR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCoalBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledNetherBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_NETHER_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChainMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHAIN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFireCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FIRE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWallTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WALL_TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBasaltMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BASALT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLadderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LADDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedMushroomBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_MUSHROOM_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpurStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPUR_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPetrifiedOakSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PETRIFIED_OAK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBushMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BUSH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTallGrassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TALL_GRASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGraniteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRANITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBeeNestMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BEE_NEST);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAirMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.AIR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedAcaciaLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACKSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakDoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_DOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLilyPadMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LILY_PAD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChestMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHEST);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getInfestedMossyStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.INFESTED_MOSSY_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedstoneWallTorchMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.REDSTONE_WALL_TORCH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackstoneStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACKSTONE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTwistingVinesPlantMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TWISTING_VINES_PLANT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBellMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BELL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTntMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TNT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrainCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BRAIN_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonFenceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_FENCE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedDioriteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_DIORITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSweetBerryBushMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SWEET_BERRY_BUSH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSnowBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SNOW_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSeagrassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SEAGRASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedOxeyeDaisyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_OXEYE_DAISY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightGrayStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_GRAY_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFlowerPotMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FLOWER_POT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndPortalFrameMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_PORTAL_FRAME);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadTubeCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_TUBE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedDioriteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_DIORITE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrayBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRAY_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBoneBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BONE_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDirtMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DIRT);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWeepingVinesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WEEPING_VINES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCutRedSandstoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CUT_RED_SANDSTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndStoneBrickSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_STONE_BRICK_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_PLANKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPoweredRailMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POWERED_RAIL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGrindstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GRINDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyCobblestoneWallMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE_WALL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCutSandstoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CUT_SANDSTONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpurBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPUR_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJigsawMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JIGSAW);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledStoneBricksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_STONE_BRICKS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaTrapdoorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_TRAPDOOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLargeFernMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LARGE_FERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlackBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLACK_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBirchPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BIRCH_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJackOLanternMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JACK_O_LANTERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFurnaceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FURNACE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getScaffoldingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SCAFFOLDING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueIceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_ICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkStainedGlassMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_STAINED_GLASS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCaveAirMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CAVE_AIR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeStainedGlassPaneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_STAINED_GLASS_PANE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getKelpMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.KELP);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRespawnAnchorMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.RESPAWN_ANCHOR);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedJungleSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_JUNGLE_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHornCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HORN_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueShulkerBoxMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_SHULKER_BOX);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBubbleCoralMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BUBBLE_CORAL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedCactusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_CACTUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMagentaGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MAGENTA_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownMushroomBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_MUSHROOM_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadTubeCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_TUBE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedOakSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_OAK_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEmeraldBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.EMERALD_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCauldronMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CAULDRON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedAndesiteStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_ANDESITE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBrownWallBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BROWN_WALL_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedBirchSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_BIRCH_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadFireCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_FIRE_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getTwistingVinesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.TWISTING_VINES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkPrismarineStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_PRISMARINE_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBambooSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BAMBOO_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMossyCobblestoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MOSSY_COBBLESTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getMushroomStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.MUSHROOM_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBlueTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BLUE_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleButtonMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_BUTTON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOrangeCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ORANGE_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStructureVoidMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRUCTURE_VOID);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getFrostedIceMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.FROSTED_ICE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadHornCoralWallFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_HORN_CORAL_WALL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedWarpedRootsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_WARPED_ROOTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHornCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HORN_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkBannerMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_BANNER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPolishedBlackstonePressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGreenWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GREEN_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStonecutterMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONECUTTER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedAcaciaSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_ACACIA_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedDarkOakSaplingMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_DARK_OAK_SAPLING);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getChiseledQuartzBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CHISELED_QUARTZ_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLimeCarpetMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIME_CARPET);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedWarpedFungusMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_WARPED_FUNGUS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getEndGatewayMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.END_GATEWAY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getRoseBushMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ROSE_BUSH);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStoneMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STONE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getYellowWoolMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.YELLOW_WOOL);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getIronBarsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.IRON_BARS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteConcreteMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_CONCRETE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPurpleGlazedTerracottaMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PURPLE_GLAZED_TERRACOTTA);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBeaconMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BEACON);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHoneycombBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HONEYCOMB_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedWarpedStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOxeyeDaisyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OXEYE_DAISY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPeonyMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PEONY);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDeadBrainCoralBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DEAD_BRAIN_CORAL_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedJungleWoodMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_WOOD);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getOakLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.OAK_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakWallSignMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_WALL_SIGN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedWitherRoseMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_WITHER_ROSE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNetherBrickStairsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NETHER_BRICK_STAIRS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCobwebMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COBWEB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedFernMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_FERN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getBubbleCoralFanMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.BUBBLE_CORAL_FAN);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWhiteBedMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WHITE_BED);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSpruceSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getWarpedHyphaeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.WARPED_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getJungleLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.JUNGLE_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getNoteBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.NOTE_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAttachedMelonStemMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ATTACHED_MELON_STEM);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedJungleLogMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_JUNGLE_LOG);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAndesiteSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ANDESITE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getStrippedWarpedHyphaeMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.STRIPPED_WARPED_HYPHAE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getLightBlueConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.LIGHT_BLUE_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getHeavyWeightedPressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPinkConcretePowderMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.PINK_CONCRETE_POWDER);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCobblestoneSlabMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.COBBLESTONE_SLAB);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getAcaciaLeavesMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.ACACIA_LEAVES);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getSprucePressurePlateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.SPRUCE_PRESSURE_PLATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getDarkOakFenceGateMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.DARK_OAK_FENCE_GATE);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getPottedCrimsonRootsMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.POTTED_CRIMSON_ROOTS);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getGoldBlockMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.GOLD_BLOCK);
    }

    public static cn.jstxjf_.rte.mapping.holder.Block getCrimsonPlanksMapped() {
        return new cn.jstxjf_.rte.mapping.holder.Block(net.minecraft.world.level.block.Blocks.CRIMSON_PLANKS);
    }
}
