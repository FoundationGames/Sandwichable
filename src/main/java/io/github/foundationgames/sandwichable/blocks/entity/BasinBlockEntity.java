package io.github.foundationgames.sandwichable.blocks.entity;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.CheeseCultureItem;
import io.github.foundationgames.sandwichable.items.CheeseItem;
import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.CheeseRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class BasinBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    private int fermentProgress = 0;
    public static final int fermentTime = 3600;
    private BasinContent content = BasinContent.AIR;

    private Random rng = new Random();

    public BasinBlockEntity() {
        super(BlocksRegistry.BASIN_BLOCKENTITY);
    }

    private static Map<CheeseType, Item> cheeseTypeToItem() {
        Map<CheeseType, Item> map = Maps.newHashMap();
        map.put(CheeseType.REGULAR, ItemsRegistry.CHEESE_WHEEL_REGULAR);
        map.put(CheeseType.CREAMY, ItemsRegistry.CHEESE_WHEEL_CREAMY);
        map.put(CheeseType.INTOXICATING, ItemsRegistry.CHEESE_WHEEL_INTOXICATING);
        map.put(CheeseType.SOUR, ItemsRegistry.CHEESE_WHEEL_SOUR);
        map.put(CheeseType.CANDESCENT, ItemsRegistry.CHEESE_WHEEL_CANDESCENT);
        map.put(CheeseType.WARPED_BLEU, ItemsRegistry.CHEESE_WHEEL_WARPED_BLEU);
        return map;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        fermentProgress = tag.getInt("fermentProgress");
        content = CheeseRegistry.INSTANCE.basinContentFromString(tag.getString("basinContent") == null ? "air" : tag.getString("basinContent"));
//      Handle update from v1.0.1 to v1.0.2 {
            if(tag.getBoolean("hasMilk")) { content = BasinContent.MILK; }
            if(tag.getBoolean("fermenting")) { content = BasinContent.FERMENTING_MILK_REGULAR; }
            if(tag.getBoolean("hasCheese")) { content = BasinContent.CHEESE_REGULAR; }
//      }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("fermentProgress", fermentProgress);
        tag.putString("basinContent", content == null ? "air" : content.toString());
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(world.getBlockState(pos), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    public ActionResult onBlockUse(PlayerEntity player, Hand hand) {
        ItemStack playerStack = player.getStackInHand(hand);
        if(content.getContentType() == BasinContentType.CHEESE) {
            ItemEntity cheese = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(cheeseTypeToItem().get(content.getCheeseType()), 1));
            world.spawnEntity(cheese);
            content = BasinContent.AIR;
            return ActionResult.SUCCESS;
        }
        if(content == BasinContent.MILK && playerStack.getItem() instanceof CheeseCultureItem) {
            this.startFermenting(((CheeseCultureItem)playerStack.getItem()).getCheeseType());
            this.createCheeseParticle(this.world, this.pos, this.rng, 8, content.getCheeseType().getParticleColorRGB());
            world.playSound(null, pos, SoundEvents.ITEM_HONEY_BOTTLE_DRINK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if(!player.isCreative()) {
                playerStack.decrement(1);
                player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE, 1));
            }
            return ActionResult.SUCCESS;
        }
        if(content == BasinContent.AIR && playerStack.getItem() instanceof CheeseItem) {
            content = CheeseRegistry.INSTANCE.cheeseFromCheeseType(((CheeseItem)playerStack.getItem()).getCheeseType());
            if(!player.isCreative()) { playerStack.decrement(1); }
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(Items.MILK_BUCKET) && content == BasinContent.AIR) {
            if(!player.isCreative()) { player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1)); }
            content = BasinContent.MILK;
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(ItemsRegistry.FERMENTING_MILK_BUCKET) && content == BasinContent.AIR) {
            if(!player.isCreative()) { player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1)); }
            if(playerStack.getTag() != null) {
                if(playerStack.getTag().getCompound("bucketData") != null) {
                    CompoundTag tag = playerStack.getTag().getCompound("bucketData");
                    content = CheeseRegistry.INSTANCE.basinContentFromString(tag.getString("basinContent"));
                    fermentProgress = tag.getInt("fermentProgressActual");
                }
            } else {
                return ActionResult.PASS;
            }
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(Items.BUCKET) && content == BasinContent.MILK) {
            playerStack.decrement(1);
            player.giveItemStack(new ItemStack(Items.MILK_BUCKET, 1));
            content = BasinContent.AIR;
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem().equals(Items.BUCKET) && content.getContentType() == BasinContentType.FERMENTING_MILK) {
            ItemStack stack = new ItemStack(ItemsRegistry.FERMENTING_MILK_BUCKET, 1);
            CompoundTag tag = new CompoundTag();
            tag.putInt("fermentProgressActual", fermentProgress);
            float a = (float)fermentProgress/fermentTime; a *= 100;
            int x = Math.round(a);
            tag.putInt("percentFermented", x);
            tag.putString("basinContent", content.toString());
            stack.getOrCreateTag().put("bucketData", tag);
            playerStack.decrement(1);
            player.giveItemStack(stack);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            content = BasinContent.AIR;
            fermentProgress = 0;
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public int getComparatorOutput() {
        float f = (float)this.fermentProgress / BasinBlockEntity.fermentTime;
        return (int)(f*15);
    }
    public int getFermentProgress() {
        return fermentProgress;
    }
    public BasinContent getContent() {
        return content;
    }

    public void startFermenting(CheeseType type) {
        if(content == BasinContent.MILK) {
            content = CheeseRegistry.INSTANCE.fermentingMilkFromCheeseType(type);
            fermentProgress = 0;
        }
    }
    public void finishFermenting() {
        if(content.getContentType() == BasinContentType.FERMENTING_MILK) {
            fermentProgress = 0;
            content = CheeseRegistry.INSTANCE.cheeseFromCheeseType(content.getCheeseType());
        }
    }

    private int tickN = 0;

    @Override
    public void tick() {
        if(content != null) {
            if (content.getContentType() == BasinContentType.FERMENTING_MILK) {
                fermentProgress++;
            }
            if (content.getContentType() == BasinContentType.CHEESE && tickN % 15 == 0) {
                createCheeseParticle(this.world, this.pos, this.rng, 1, content.getCheeseType().getParticleColorRGB());
            }
        }
        if(fermentProgress >= fermentTime) {
            finishFermenting();
        }
        if(tickN < 60) { tickN++; } else if(tickN == 60) { tickN = 0; }
    }

    private static void createCheeseParticle(World world, BlockPos pos, Random random, int count, float[] color) {
        for (int i = 0; i < count; i++) {
            double ox = ((double) random.nextInt(10) / 16);
            double oz = ((double) random.nextInt(10) / 16);
            world.addParticle(new DustParticleEffect(color[0], color[1], color[2], 1.0F), pos.getX() + ox + 0.2, pos.getY() + 0.4, pos.getZ() + oz + 0.2, 0.0D, 0.09D, 0.0D);
        }
    }
}
