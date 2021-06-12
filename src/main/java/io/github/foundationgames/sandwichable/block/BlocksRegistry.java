package io.github.foundationgames.sandwichable.block;

import com.google.common.collect.Lists;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.block.entity.*;
import io.github.foundationgames.sandwichable.fluid.FluidsRegistry;
import io.github.foundationgames.sandwichable.item.InfoTooltipBlockItem;
import io.github.foundationgames.sandwichable.item.SandwichBlockItem;
import io.github.foundationgames.sandwichable.util.Util;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

import java.util.List;

public final class BlocksRegistry {

    public static final Block SANDWICH_TABLE = new SandwichTableBlock(FabricBlockSettings.copy(Blocks.CRAFTING_TABLE));
    public static final Block LETTUCE = new LettuceCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block TOMATOES = new TomatoCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block ONIONS = new OnionCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));
    public static final Block CUCUMBERS = new CucumberCropBlock(FabricBlockSettings.copy(Blocks.WHEAT));

    public static final Block SANDWICH = new SandwichBlock(FabricBlockSettings.copy(Blocks.CAKE).nonOpaque());

    public static final Block OAK_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block BIRCH_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block SPRUCE_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block JUNGLE_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block ACACIA_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block DARK_OAK_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block CRIMSON_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));
    public static final Block WARPED_CUTTING_BOARD = new CuttingBoardBlock(FabricBlockSettings.copy(Blocks.OAK_PRESSURE_PLATE));

    public static final Block ANDESITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.ANDESITE));
    public static final Block DIORITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.DIORITE));
    public static final Block GRANITE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.GRANITE));
    public static final Block BASALT_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.POLISHED_BASALT));
    public static final Block BLACKSTONE_BASIN = new BasinBlock(FabricBlockSettings.copy(Blocks.POLISHED_BLACKSTONE));

    public static final Block TOASTER = new ToasterBlock(FabricBlockSettings.copy(Blocks.STONECUTTER));

    public static final Block DESALINATOR = new DesalinatorBlock(FabricBlockSettings.copy(Blocks.STONECUTTER).luminance(state -> state.get(DesalinatorBlock.ON) ? 13 : 4));
    public static final Block SALTY_SAND = new SandBlock(14406560, FabricBlockSettings.copy(Blocks.SAND));

    public static final Block SALTY_STONE = new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.PALE_GREEN).strength(1.7f, 6.5f).sounds(BlockSoundGroup.STONE));
    public static final Block SALTY_ROCKS = new Block(AbstractBlock.Settings.of(Material.ORGANIC_PRODUCT, MapColor.PALE_GREEN).strength(1.7f, 6.5f).sounds(BlockSoundGroup.STONE));

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

    // TODO: being able to place the patchouli book
    /*public static final Block SANDWICH_BOOK = new HorizontalFacingBlock(FabricBlockSettings.copy(Blocks.TRIPWIRE).sounds(BlockSoundGroup.WOOD)) {
        VoxelShape SHAPE = createCuboidShape(1.5, 0, 1.5, 14.5, 3, 14.5);

        @Override
        public BlockState getPlacementState(ItemPlacementContext ctx) { return getDefaultState().with(FACING, ctx.getPlayerLookDirection()); }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING); }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            return SHAPE;
        }
    };*/

    public static BlockEntityType<SandwichTableBlockEntity> SANDWICHTABLE_BLOCKENTITY;
    public static BlockEntityType<SandwichBlockEntity> SANDWICH_BLOCKENTITY;
    public static BlockEntityType<CuttingBoardBlockEntity> CUTTINGBOARD_BLOCKENTITY;
    public static BlockEntityType<ToasterBlockEntity> TOASTER_BLOCKENTITY;
    public static BlockEntityType<BasinBlockEntity> BASIN_BLOCKENTITY;
    public static BlockEntityType<PickleJarBlockEntity> PICKLEJAR_BLOCKENTITY;
    public static BlockEntityType<DesalinatorBlockEntity> DESALINATOR_BLOCKENTITY;
    public static BlockEntityType<BottleCrateBlockEntity> BOTTLECRATE_BLOCKENTITY;

    public static final List<Block> BASINS = Lists.newArrayList(ANDESITE_BASIN, GRANITE_BASIN, DIORITE_BASIN, BASALT_BASIN, BLACKSTONE_BASIN);
    public static final List<Block> CUTTING_BOARDS = Lists.newArrayList(OAK_CUTTING_BOARD, BIRCH_CUTTING_BOARD, SPRUCE_CUTTING_BOARD, JUNGLE_CUTTING_BOARD, ACACIA_CUTTING_BOARD, DARK_OAK_CUTTING_BOARD, CRIMSON_CUTTING_BOARD, WARPED_CUTTING_BOARD);

    public static void init() {
        registerBlock(SANDWICH_TABLE, "sandwich_table", Sandwichable.SANDWICHABLE_ITEMS);

        registerBlock(SANDWICH, "sandwich");

        registerBlock(TOASTER, "toaster", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(ANDESITE_BASIN, "andesite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DIORITE_BASIN, "diorite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(GRANITE_BASIN, "granite_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BASALT_BASIN, "basalt_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BLACKSTONE_BASIN, "blackstone_basin", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(OAK_CUTTING_BOARD, "oak_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(BIRCH_CUTTING_BOARD, "birch_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(SPRUCE_CUTTING_BOARD, "spruce_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(JUNGLE_CUTTING_BOARD, "jungle_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(ACACIA_CUTTING_BOARD, "acacia_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        registerBlock(DARK_OAK_CUTTING_BOARD, "dark_oak_cutting_board", Sandwichable.SANDWICHABLE_ITEMS);
        FuelRegistry.INSTANCE.add(OAK_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(BIRCH_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(SPRUCE_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(JUNGLE_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(ACACIA_CUTTING_BOARD, 320);
        FuelRegistry.INSTANCE.add(DARK_OAK_CUTTING_BOARD, 320);
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

        registerBlock(PICKLE_BRINE, "pickle_brine");

    }

    public static void initBlockEntities() {
        SANDWICHTABLE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("sandwich_table_ent"), FabricBlockEntityTypeBuilder.create(SandwichTableBlockEntity::new, SANDWICH_TABLE).build(null));
        CUTTINGBOARD_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("cutting_board_ent"), FabricBlockEntityTypeBuilder.create(CuttingBoardBlockEntity::new, CUTTING_BOARDS.toArray(new Block[] {})).build(null));
        SANDWICH_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("sandwich_ent"), FabricBlockEntityTypeBuilder.create(SandwichBlockEntity::new, SANDWICH).build(null));
        TOASTER_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("toaster_ent"), FabricBlockEntityTypeBuilder.create(ToasterBlockEntity::new, TOASTER).build(null));
        BASIN_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("basin_ent"), FabricBlockEntityTypeBuilder.create(BasinBlockEntity::new, BASINS.toArray(new Block[] {})).build(null));
        PICKLEJAR_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("pickle_jar_ent"), FabricBlockEntityTypeBuilder.create(PickleJarBlockEntity::new, PICKLE_JAR).build(null));
        DESALINATOR_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("desalinator_ent"), FabricBlockEntityTypeBuilder.create(DesalinatorBlockEntity::new, DESALINATOR).build(null));
        BOTTLECRATE_BLOCKENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, Util.id("bottle_crate_ent"), FabricBlockEntityTypeBuilder.create(BottleCrateBlockEntity::new, BOTTLE_CRATE).build(null));

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
