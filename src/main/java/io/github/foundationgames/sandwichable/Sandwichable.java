package io.github.foundationgames.sandwichable;

import io.github.foundationgames.sandwichable.block.BlocksRegistry;
import io.github.foundationgames.sandwichable.block.entity.*;
import io.github.foundationgames.sandwichable.block.entity.container.BottleCrateScreenHandler;
import io.github.foundationgames.sandwichable.block.entity.container.DesalinatorScreenHandler;
import io.github.foundationgames.sandwichable.common.CommonTags;
import io.github.foundationgames.sandwichable.compat.CompatModuleManager;
import io.github.foundationgames.sandwichable.compat.mod.CroptopiaCompat;
import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import io.github.foundationgames.sandwichable.fluid.FluidsRegistry;
import io.github.foundationgames.sandwichable.item.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.SandwichableGroupIconBuilder;
import io.github.foundationgames.sandwichable.item.spread.SpreadType;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipeSerializer;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipe;
import io.github.foundationgames.sandwichable.recipe.ToastingRecipeSerializer;
import io.github.foundationgames.sandwichable.util.ExtraDispenserBehaviorRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import io.github.foundationgames.sandwichable.villager.SandwichMakerProfession;
import net.devtech.arrp.api.RRPCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Sandwichable implements ModInitializer {

    public static final ItemGroup SANDWICHABLE_ITEMS = FabricItemGroupBuilder.build(Util.id("sandwichable"), SandwichableGroupIconBuilder::getIcon);

    public static final Tag<Item> BREAD_SLICES = TagRegistry.item(Util.id("bread_slices"));
    public static final Tag<Item> BREAD_LOAVES = TagRegistry.item(Util.id("bread_loaves"));
    public static final Tag<Item> METAL_ITEMS = TagRegistry.item(Util.id("metal_items"));
    public static final Tag<Item> SMALL_FOODS = TagRegistry.item(Util.id("small_foods"));
    public static final Tag<Block> SALT_PRODUCING_BLOCKS = TagRegistry.block(Util.id("salt_producing_blocks"));

    public static final SoundEvent DESALINATOR_START = Registry.register(Registry.SOUND_EVENT, Util.id("desalinator_start"), new SoundEvent(Util.id("desalinator_start")));
    public static final SoundEvent DESALINATOR_RUN = Registry.register(Registry.SOUND_EVENT, Util.id("desalinator_run"), new SoundEvent(Util.id("desalinator_run")));
    public static final SoundEvent DESALINATOR_STOP = Registry.register(Registry.SOUND_EVENT, Util.id("desalinator_stop"), new SoundEvent(Util.id("desalinator_stop")));

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

    @Override
    public void onInitialize() {
        FluidsRegistry.init();
        BlocksRegistry.init();
        ItemsRegistry.init();
        EntitiesRegistry.init();
        SandwichMakerProfession.init();
        SpreadType.init();

        RRPCallback.EVENT.register(a -> a.add(CompatModuleManager.DATA));

        try {
            CompatModuleManager.init();
        } catch (IOException e) {
            LOG.error("ERROR parsing compatibility modules!");
            e.printStackTrace();
        }

        BlocksRegistry.initBlockEntities();

        Registry.register(Registry.RECIPE_SERIALIZER, CuttingRecipeSerializer.ID, CuttingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(CuttingRecipe.Type.ID), CuttingRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, ToastingRecipeSerializer.ID, ToastingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, Util.id(ToastingRecipe.Type.ID), ToastingRecipe.Type.INSTANCE);

        ExtraDispenserBehaviorRegistry.initDefaults();

        ServerSidePacketRegistry.INSTANCE.register(Util.id("request_sandwich_table_cart_sync"), (ctx, buf) -> {
            int id = buf.readInt();
            Entity e = ctx.getPlayer().getEntityWorld().getEntityById(id);
            ctx.getTaskQueue().execute(() -> {
                if(e instanceof SandwichTableMinecartEntity) {
                    ((SandwichTableMinecartEntity)e).sync();
                }
            });
        });

        if(FabricLoader.getInstance().isModLoaded("croptopia")) {
            CroptopiaCompat.init();
        }

        CommonTags.init();

        SandwichableEarly.onEarlyInitialization();
    }

    public static boolean isBread(ItemConvertible item) {
        return BREAD_SLICES.contains(item.asItem()) || BREAD_LOAVES.contains(item.asItem());
    }
}
