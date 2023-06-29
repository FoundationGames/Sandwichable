package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.advancement.CollectSandwichCriterion;
import io.github.foundationgames.sandwichable.advancement.CutItemCriterion;
import io.github.foundationgames.sandwichable.advancement.ToastItemCriterion;
import io.github.foundationgames.sandwichable.advancement.UseBottleCrateCriterion;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.blocks.entity.BottleCrateBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.DesalinatorBlockEntity;
import io.github.foundationgames.sandwichable.blocks.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.blocks.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.blocks.loot.CopyWorldBiomeLootFunction;
import io.github.foundationgames.sandwichable.common.CommonTags;
import io.github.foundationgames.sandwichable.compat.CroptopiaCompat;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.fluids.FluidsRegistry;
import io.github.foundationgames.sandwichable.items.ItemGroupQueue;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.KitchenKnifeItem;
import io.github.foundationgames.sandwichable.items.SandwichableGroupIconBuilder;
import io.github.foundationgames.sandwichable.items.spread.SpreadType;
import io.github.foundationgames.sandwichable.mixin.CriteriaAccess;
import io.github.foundationgames.sandwichable.recipe.SandwichableRecipes;
import io.github.foundationgames.sandwichable.util.AncientGrainType;
import io.github.foundationgames.sandwichable.util.ExtraDispenserBehaviorRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.villager.SandwichMakerProfession;
import io.github.foundationgames.sandwichable.worldgen.SandwichableWorldgen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Sandwichable implements ModInitializer {
    public static final ItemGroupQueue SANDWICHABLE_ITEMS = new ItemGroupQueue(Util.id("sandwichable"));
    public static final ItemGroup SANDWICHABLE_GROUP = FabricItemGroup.builder()
            .icon(SandwichableGroupIconBuilder::getIcon)
            .displayName(Text.translatable(net.minecraft.util.Util.createTranslationKey("itemGroup", SANDWICHABLE_ITEMS.id)))
            .entries(SANDWICHABLE_ITEMS)
            .build();

    public static final TagKey<Item> BREAD_SLICES = TagKey.of(RegistryKeys.ITEM, Util.id("bread_slices"));
    public static final TagKey<Item> BREAD_LOAVES = TagKey.of(RegistryKeys.ITEM, Util.id("bread_loaves"));
    public static final TagKey<Item> METAL_ITEMS = TagKey.of(RegistryKeys.ITEM, Util.id("metal_items"));
    public static final TagKey<Item> SMALL_FOODS = TagKey.of(RegistryKeys.ITEM, Util.id("small_foods"));
    public static final TagKey<Item> CUTTING_BOARDS = TagKey.of(RegistryKeys.ITEM, Util.id("cutting_boards"));
    public static final TagKey<Item> CHEESE_WHEELS = TagKey.of(RegistryKeys.ITEM, Util.id("cheese_wheels"));
    public static final TagKey<Block> SALT_PRODUCING_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Util.id("salt_producing_blocks"));
    public static final TagKey<Block> KNIFE_SHARPENING_SURFACES = TagKey.of(RegistryKeys.BLOCK, Util.id("knife_sharpening_surfaces"));

    public static final TagKey<Biome> SALT_WATER_BODIES = TagKey.of(RegistryKeys.BIOME, Util.id("salt_water_bodies"));
    public static final TagKey<Biome> NO_SHRUBS = TagKey.of(RegistryKeys.BIOME, Util.id("no_shrubs"));
    public static final TagKey<Biome> NO_SALT_POOLS = TagKey.of(RegistryKeys.BIOME, Util.id("no_salt_pools"));

    public static final CutItemCriterion CUT_ITEM = CriteriaAccess.sandwichable$register(new CutItemCriterion());
    public static final ToastItemCriterion TOAST_ITEM = CriteriaAccess.sandwichable$register(new ToastItemCriterion());
    public static final UseBottleCrateCriterion USE_BOTTLE_CRATE = CriteriaAccess.sandwichable$register(new UseBottleCrateCriterion());
    public static final CollectSandwichCriterion COLLECT_SANDWICH = CriteriaAccess.sandwichable$register(new CollectSandwichCriterion());

    public static final SoundEvent DESALINATOR_START = Registry.register(Registries.SOUND_EVENT, Util.id("desalinator_start"), SoundEvent.of(Util.id("desalinator_start")));
    public static final SoundEvent DESALINATOR_RUN = Registry.register(Registries.SOUND_EVENT, Util.id("desalinator_run"), SoundEvent.of(Util.id("desalinator_run")));
    public static final SoundEvent DESALINATOR_STOP = Registry.register(Registries.SOUND_EVENT, Util.id("desalinator_stop"), SoundEvent.of(Util.id("desalinator_stop")));

    public static final GameRules.Key<GameRules.IntRule> SANDWICH_SIZE_RULE = GameRuleRegistry.register("maxSandwichSize", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(-1, -1));
    public static final GameRules.Key<GameRules.BooleanRule> PICKLE_BRINE_SOURCE_CONVERSION_RULE = GameRuleRegistry.register("pickleBrineSourceConversion", GameRules.Category.UPDATES, GameRuleFactory.createBooleanRule(false));

    public static final Logger LOG = LogManager.getLogger("Sandwichable");

    public static final ScreenHandlerType<BottleCrateScreenHandler> BOTTLE_CRATE_HANDLER = ScreenHandlerRegistry.registerExtended(Util.id("bottle_crate_handler"), (syncId, playerInv, buf) -> {
        BlockEntity be = playerInv.player.getEntityWorld().getBlockEntity(buf.readBlockPos());
        if(be instanceof BottleCrateBlockEntity) return new BottleCrateScreenHandler(syncId, playerInv, (BottleCrateBlockEntity)be);
        return null;
    });

    public static final ScreenHandlerType<DesalinatorScreenHandler> DESALINATOR_HANDLER = ScreenHandlerRegistry.registerExtended(Util.id("desalinator_handler"), (syncId, playerInv, buf) -> {
        BlockEntity be = playerInv.player.getEntityWorld().getBlockEntity(buf.readBlockPos());
        if(be instanceof DesalinatorBlockEntity) return new DesalinatorScreenHandler(syncId, playerInv, (DesalinatorBlockEntity)be);
        return null;
    });

    public static final LootFunctionType COPY_WORLD_BIOME = Registry.register(Registries.LOOT_FUNCTION_TYPE, Util.id("copy_world_biome"), new LootFunctionType(new CopyWorldBiomeLootFunction.Serializer()));
    private static final Identifier ANCIENT_CITY_LOOT = new Identifier("chests/ancient_city");

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM_GROUP, SANDWICHABLE_ITEMS.id, SANDWICHABLE_GROUP);

        FluidsRegistry.init();
        BlocksRegistry.init();
        ItemsRegistry.init();
        EntitiesRegistry.init();
        SandwichMakerProfession.init();
        SandwichableRecipes.init();
        SpreadType.init();
        SandwichableWorldgen.init();
        AncientGrainType.init();

        ExtraDispenserBehaviorRegistry.initDefaults();

        ServerPlayNetworking.registerGlobalReceiver(Util.id("request_sandwich_table_cart_sync"), (server, player, handler, buf, responseSender) -> {
            int id = buf.readInt();
            Entity e = player.getEntityWorld().getEntityById(id);
            server.execute(() -> {
                if(e instanceof SandwichTableMinecartEntity) {
                    ((SandwichTableMinecartEntity)e).sync();
                }
            });
        });

        DispenserBehavior defaultBehavior = new ItemDispenserBehavior();
        DispenserBlock.registerBehavior(ItemsRegistry.PICKLE_BRINE_BUCKET, (pointer, stack) -> {
            BlockPos pos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            BucketItem bucket = (BucketItem)stack.getItem();
            World world = pointer.getWorld();
            if (bucket.placeFluid(null, world, pos, null)) {
                bucket.onEmptied(null, world, stack, pos);
                return new ItemStack(Items.BUCKET);
            } else {
                return defaultBehavior.dispense(pointer, stack);
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            BlockPos pos = hit.getBlockPos();
            if (world.getBlockState(pos).isIn(KNIFE_SHARPENING_SURFACES)) {
                ItemStack knife = player.getStackInHand(hand);
                SandwichableConfig.KitchenKnifeOption opt = Util.getConfig().getKnifeOption(knife.getItem());
                if (opt != null && KitchenKnifeItem.getSharpnessF(knife) < 1) {
                    Vec3d hPos = hit.getPos();
                    if (world.isClient()) {
                        for (int i = 0; i < 4; i++) {
                            world.addParticle(ParticleTypes.CRIT, hPos.x, hPos.y, hPos.z, (world.random.nextFloat() - 0.5) * 0.5, 0.1, (world.random.nextFloat() - 0.5) * 0.5);
                        }
                        return ActionResult.SUCCESS;
                    }
                    KitchenKnifeItem.setSharpness(knife, KitchenKnifeItem.getSharpness(knife) + 3);
                    world.playSound(null, hPos.x, hPos.y, hPos.z, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 0.7f, 1.5f + (world.random.nextFloat() * 0.2f));
                    return ActionResult.CONSUME;
                }
            }
            return ActionResult.PASS;
        });

        LootTableEvents.MODIFY.register((resources, loot, id, table, source) -> {
            if (source.isBuiltin() && ANCIENT_CITY_LOOT.equals(id)) {
                table.pool(LootPool.builder().with(
                        ItemEntry.builder(ItemsRegistry.ANCIENT_GRAIN_SEEDS)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 3)))
                        ).rolls(UniformLootNumberProvider.create(0, 2))
                );
            }
        });

        if(FabricLoader.getInstance().isModLoaded("croptopia")) {
            CroptopiaCompat.init();
        }

        CommonTags.init();
    }

    public static boolean isBread(ItemStack stack) {
        return stack.isIn(BREAD_SLICES) || stack.isIn(BREAD_LOAVES);
    }
}
