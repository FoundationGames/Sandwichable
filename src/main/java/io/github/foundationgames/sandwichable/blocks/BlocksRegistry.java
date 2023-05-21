package io.github.foundationgames.sandwichable.blocks;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.entity.*;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import io.github.foundationgames.sandwichable.items.InfoTooltipBlockItem;
import io.github.foundationgames.sandwichable.items.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public final class BlocksRegistry {

    public static final Block SANDWICH_TABLE = new SandwichTableBlock(FabricBlockSettings.copy(Blocks.CRAFTING_TABLE));
    public static final Block LETTUCE = new LettuceCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block TOMATOES = new TomatoCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block ONIONS = new OnionCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block CUCUMBERS = new CucumberCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block ANCIENT_GRAIN = new AncientGrainBlock(FabricBlockSettings.copy(Blocks.WHEAT).offsetType(AbstractBlock.OffsetType.XZ));

    public static final Block SANDWICH = new SandwichBlock(FabricBlockSettings.copy(Blocks.CAKE).nonOpaque());

    public static final Block OAK_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block BIRCH_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block SPRUCE_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block JUNGLE_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block ACACIA_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block DARK_OAK_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block MANGROVE_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block CRIMSON_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block WARPED_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));

    public static final Block ANDESITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.ANDESITE));
    public static final Block DIORITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.DIORITE));
    public static final Block GRANITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.GRANITE));
    public static final Block BASALT_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.POLISHED_BASALT));
    public static final Block BLACKSTONE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.POLISHED_BLACKSTONE));
    public static final Block DEEPSLATE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.POLISHED_DEEPSLATE));
    public static final Block COPPER_BASIN = new OxidizableBasinBlock(Oxidizable.OxidationLevel.UNAFFECTED, FabricBlockSettings.copy(Blocks.CUT_COPPER));
    public static final Block EXPOSED_COPPER_BASIN = new OxidizableBasinBlock(Oxidizable.OxidationLevel.EXPOSED, FabricBlockSettings.copy(Blocks.EXPOSED_CUT_COPPER));
    public static final Block WEATHERED_COPPER_BASIN = new OxidizableBasinBlock(Oxidizable.OxidationLevel.WEATHERED, FabricBlockSettings.copy(Blocks.WEATHERED_CUT_COPPER));
    public static final Block OXIDIZED_COPPER_BASIN = new OxidizableBasinBlock(Oxidizable.OxidationLevel.OXIDIZED, FabricBlockSettings.copy(Blocks.OXIDIZED_CUT_COPPER));
    public static final Block WAXED_COPPER_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.WAXED_CUT_COPPER));
    public static final Block WAXED_EXPOSED_COPPER_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.WAXED_EXPOSED_CUT_COPPER));
    public static final Block WAXED_WEATHERED_COPPER_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.WAXED_WEATHERED_CUT_COPPER));
    public static final Block WAXED_OXIDIZED_COPPER_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.WAXED_OXIDIZED_CUT_COPPER));

    public static final Block TOASTER = new ToasterBlock(FabricBlockSettings.copy(Blocks.STONECUTTER));

    public static final Block DESALINATOR = new DesalinatorBlock(FabricBlockSettings.copy(Blocks.STONECUTTER).luminance(state -> state.get(DesalinatorBlock.ON) ? 13 : 4));
    public static final Block SALTY_SAND = new SandBlock(14406560, FabricBlockSettings.copy(Blocks.SAND));

    public static final Block SALTY_STONE = new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.STONE_GRAY).strength(1.7f, 6.5f).sounds(BlockSoundGroup.STONE));
    public static final Block SALTY_ROCKS = new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.STONE_GRAY).strength(1.7f, 6.5f).sounds(BlockSoundGroup.STONE));

    public static final Block PICKLE_JAR = new PickleJarBlock(FabricBlockSettings.copy(Blocks.GLASS_PANE));

    public static final Block SHRUB = new ShrubBlock(FabricBlockSettings.copy(Blocks.DEAD_BUSH));

    public static final Block POTTED_SHRUB = new PottedShrubBlock(SHRUB, FabricBlockSettings.copy(Blocks.POTTED_DEAD_BUSH));

    public static final Block BOTTLE_CRATE = new BottleCrateBlock(FabricBlockSettings.copy(Blocks.BARREL));

    public static class SaltyAirBlock extends AirBlock { public SaltyAirBlock(Settings settings) { super(settings); } }
    public static final Block SALTY_AIR = new SaltyAirBlock(FabricBlockSettings.copy(Blocks.CAVE_AIR));

    public static class PickleBrineFluidBlock extends net.minecraft.block.FluidBlock {
        protected PickleBrineFluidBlock(FlowableFluid fluid, Settings settings) { super(fluid, settings); }
        @Override public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) { return true; }
    }
    public static final Block PICKLE_BRINE = new PickleBrineFluidBlock(FluidsRegistry.PICKLE_BRINE, FabricBlockSettings.copy(Blocks.WATER));

    public static final BiMap<Block, Block> OXIDIZABLES = ImmutableBiMap.<Block, Block>builder().put(COPPER_BASIN, EXPOSED_COPPER_BASIN).put(EXPOSED_COPPER_BASIN, WEATHERED_COPPER_BASIN).put(WEATHERED_COPPER_BASIN, OXIDIZED_COPPER_BASIN).build();
    public static final BiMap<Block, Block> WAXABLES = ImmutableBiMap.<Block, Block>builder().put(COPPER_BASIN, WAXED_COPPER_BASIN).put(EXPOSED_COPPER_BASIN, WAXED_EXPOSED_COPPER_BASIN).put(WEATHERED_COPPER_BASIN, WAXED_WEATHERED_COPPER_BASIN).put(OXIDIZED_COPPER_BASIN, WAXED_OXIDIZED_COPPER_BASIN).build();

    public static BlockEntityType<SandwichTableBlockEntity> SANDWICHTABLE_BLOCKENTITY;
    public static BlockEntityType<SandwichBlockEntity> SANDWICH_BLOCKENTITY;
    public static BlockEntityType<CuttingBoardBlockEntity> CUTTINGBOARD_BLOCKENTITY;
    public static BlockEntityType<ToasterBlockEntity> TOASTER_BLOCKENTITY;
    public static BlockEntityType<BasinBlockEntity> BASIN_BLOCKENTITY;
    public static BlockEntityType<PickleJarBlockEntity> PICKLEJAR_BLOCKENTITY;
    public static BlockEntityType<DesalinatorBlockEntity> DESALINATOR_BLOCKENTITY;
    public static BlockEntityType<BottleCrateBlockEntity> BOTTLECRATE_BLOCKENTITY;

    public static void init() {
        registerBlock(SANDWICH_TABLE, "sandwich_table", Sandwichable.SANDWICHABLE_ITEMS);

        registerBlock(SANDWICH, "sandwich");

        registerBlock(TOASTER, "toaster", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(ANDESITE_BASIN, "andesite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DIORITE_BASIN, "diorite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(GRANITE_BASIN, "granite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BASALT_BASIN, "basalt_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BLACKSTONE_BASIN, "blackstone_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DEEPSLATE_BASIN, "deepslate_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(COPPER_BASIN, "copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(EXPOSED_COPPER_BASIN, "exposed_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WEATHERED_COPPER_BASIN, "weathered_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(OXIDIZED_COPPER_BASIN, "oxidized_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WAXED_COPPER_BASIN, "waxed_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WAXED_EXPOSED_COPPER_BASIN, "waxed_exposed_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WAXED_WEATHERED_COPPER_BASIN, "waxed_weathered_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WAXED_OXIDIZED_COPPER_BASIN, "waxed_oxidized_copper_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(OAK_CUTTING_BOARD, "oak_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BIRCH_CUTTING_BOARD, "birch_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(SPRUCE_CUTTING_BOARD, "spruce_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(JUNGLE_CUTTING_BOARD, "jungle_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(ACACIA_CUTTING_BOARD, "acacia_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DARK_OAK_CUTTING_BOARD, "dark_oak_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(MANGROVE_CUTTING_BOARD, "mangrove_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        FuelRegistry.INSTANCE.add(OAK_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(BIRCH_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(SPRUCE_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(JUNGLE_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(ACACIA_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(DARK_OAK_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(MANGROVE_CUTTING_BOARD, 320);
        registerBlock(CRIMSON_CUTTING_BOARD, "crimson_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(WARPED_CUTTING_BOARD, "warped_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(SHRUB, "shrub", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BOTTLE_CRATE, "bottle_crate", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(POTTED_SHRUB, "potted_shrub");
        registerBlock(PICKLE_JAR, "pickle_jar");

        registerBlock(SALTY_AIR, "salty_air");

        registerBlock(SALTY_SAND, "salty_sand", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(SALTY_STONE, "salty_stone", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(SALTY_ROCKS, "salty_rocks", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DESALINATOR, "desalinator", Sandwichable.SANDWICHABLE_ITEMS);

        registerBlock(LETTUCE, "lettuce");
        registerBlock(TOMATOES, "tomatoes");
        registerBlock(CUCUMBERS, "cucumbers");
        registerBlock(ONIONS, "onions");
        registerBlock(ANCIENT_GRAIN, "ancient_grain");

        registerBlock(PICKLE_BRINE, "pickle_brine");

        SANDWICHTABLE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("sandwich_table_ent"), FabricBlockEntityTypeBuilder.create(SandwichTableBlockEntity::new, SANDWICH_TABLE).build(null));
        CUTTINGBOARD_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("cutting_board_ent"), FabricBlockEntityTypeBuilder.create(CuttingBoardBlockEntity::new,
                OAK_CUTTING_BOARD, BIRCH_CUTTING_BOARD, SPRUCE_CUTTING_BOARD, JUNGLE_CUTTING_BOARD, ACACIA_CUTTING_BOARD, DARK_OAK_CUTTING_BOARD, MANGROVE_CUTTING_BOARD, CRIMSON_CUTTING_BOARD, WARPED_CUTTING_BOARD
        ).build(null));
        SANDWICH_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("sandwich_ent"), FabricBlockEntityTypeBuilder.create(SandwichBlockEntity::new, SANDWICH).build(null));
        TOASTER_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("toaster_ent"), FabricBlockEntityTypeBuilder.create(ToasterBlockEntity::new, TOASTER).build(null));
        BASIN_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("basin_ent"), FabricBlockEntityTypeBuilder.create(BasinBlockEntity::new,
                ANDESITE_BASIN, GRANITE_BASIN, DIORITE_BASIN, BASALT_BASIN, BLACKSTONE_BASIN, DEEPSLATE_BASIN, COPPER_BASIN, EXPOSED_COPPER_BASIN, WEATHERED_COPPER_BASIN, OXIDIZED_COPPER_BASIN, WAXED_COPPER_BASIN, WAXED_EXPOSED_COPPER_BASIN, WAXED_WEATHERED_COPPER_BASIN, WAXED_OXIDIZED_COPPER_BASIN
        ).build(null));
        PICKLEJAR_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("pickle_jar_ent"), FabricBlockEntityTypeBuilder.create(PickleJarBlockEntity::new, PICKLE_JAR).build(null));
        DESALINATOR_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("desalinator_ent"), FabricBlockEntityTypeBuilder.create(DesalinatorBlockEntity::new, DESALINATOR).build(null));
        BOTTLECRATE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("bottle_crate_ent"), FabricBlockEntityTypeBuilder.create(BottleCrateBlockEntity::new, BOTTLE_CRATE).build(null));

        OXIDIZABLES.forEach(OxidizableBlocksRegistry::registerOxidizableBlockPair);
        WAXABLES.forEach(OxidizableBlocksRegistry::registerWaxableBlockPair);
    }

    public static void registerBlock(Block block, String name, ItemGroup group) {
        registerBlock(block, name);
        Registry.register(Registry.ITEM, Util.id(name), new InfoTooltipBlockItem(block, new Item.Settings().group(group)));
    }

    public static void registerSandwich(Block block, String name) {
        Registry.register(Registry.BLOCK, Util.id(name), block);
        Registry.register(Registry.ITEM, Util.id(name), new SandwichBlockItem(block));
    }

    public static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, Util.id(name), block);
    }
}
