package io.github.foundationgames.sandwichable.blocks.entity;

import com.google.common.collect.Maps;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.items.CheeseCultureItem;
import io.github.foundationgames.sandwichable.items.CheeseItem;
import io.github.foundationgames.sandwichable.items.CheeseType;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.util.CheeseRegistry;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

public class BasinBlockEntity extends BlockEntity implements SidedInventory, SyncedBlockEntity {
    private int fermentProgress = 0;
    public static final int fermentTime = 3600;
    private BasinContent content = BasinContent.AIR;

    private final Random rng = new Random();

    public BasinBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.BASIN_BLOCKENTITY, pos, state);
    }

    public static Map<CheeseType, Item> cheeseTypeToItem() {
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        fermentProgress = nbt.getInt("fermentProgress");
        content = CheeseRegistry.INSTANCE.basinContentFromString(nbt.getString("basinContent") == null ? "air" : nbt.getString("basinContent"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("fermentProgress", fermentProgress);
        nbt.putString("basinContent", content == null ? "air" : content.toString());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return this.getPacket();
    }

    public ActionResult onBlockUse(PlayerEntity player, Hand hand) {
        ItemStack playerStack = player.getStackInHand(hand);
        if(content.getContentType() == BasinContentType.CHEESE) {
            ItemEntity cheese = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(cheeseTypeToItem().get(content.getCheeseType()), 1));
            world.spawnEntity(cheese);
            content = BasinContent.AIR;
            update();
            return ActionResult.SUCCESS;
        }
        if(content == BasinContent.MILK && playerStack.getItem() instanceof CheeseCultureItem) {
            ItemStack s = addCheeseCulture(playerStack);
            if(!player.isCreative()) player.setStackInHand(hand, s);
            update();
            return ActionResult.SUCCESS;
        }
        if(content == BasinContent.AIR && playerStack.getItem() instanceof CheeseItem && !((CheeseItem)playerStack.getItem()).isSlice()) {
            insertCheese(playerStack.copy());
            if(!player.isCreative()) { playerStack.decrement(1); }
            update();
            return ActionResult.SUCCESS;
        }
        if((playerStack.getItem() == Items.MILK_BUCKET || playerStack.getItem() == ItemsRegistry.FERMENTING_MILK_BUCKET) && content == BasinContent.AIR) {
            ItemStack result = insertMilk(playerStack);
            if(result.equals(playerStack)) return ActionResult.PASS;
            if(!player.isCreative()) { player.setStackInHand(hand, new ItemStack(Items.BUCKET, 1)); }
            update();
            return ActionResult.SUCCESS;
        }
        if(playerStack.getItem() == Items.BUCKET && this.getContent().getContentType().isLiquid) {
            ItemStack result = extractMilk();
            update();
            if(!result.isEmpty()) {
                playerStack.decrement(1);
                player.giveItemStack(result);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        update();
        return ActionResult.PASS;
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
        update();
        markDirty();
    }

    public void finishFermenting() {
        if(content.getContentType() == BasinContentType.FERMENTING_MILK) {
            fermentProgress = 0;
            content = CheeseRegistry.INSTANCE.cheeseFromCheeseType(content.getCheeseType());
        }
        update();
        markDirty();
    }

    public ItemStack insertMilk(ItemStack milk) {
        ItemStack r = milk;
        if(milk.getItem() == Items.MILK_BUCKET) {
            content = BasinContent.MILK;
            r = new ItemStack(Items.BUCKET);
        }
        else if(milk.getItem() == ItemsRegistry.FERMENTING_MILK_BUCKET) {
            if(milk.getNbt() != null && milk.getNbt().getCompound("bucketData") != null) {
                NbtCompound tag = milk.getNbt().getCompound("bucketData");
                content = CheeseRegistry.INSTANCE.basinContentFromString(tag.getString("basinContent"));
                fermentProgress = tag.getInt("fermentProgressActual");
            }
            r = new ItemStack(Items.BUCKET);
        }
        update();
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.8F, 1.0F);
        return r;
    }

    public ItemStack extractMilk() {
        if(content == BasinContent.MILK) {
            emptyBasin();
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            update();
            return new ItemStack(Items.MILK_BUCKET);
        } else if(content.getContentType() == BasinContentType.FERMENTING_MILK) {
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 0.8F, 1.0F);
            ItemStack stack = new ItemStack(ItemsRegistry.FERMENTING_MILK_BUCKET);
            NbtCompound tag = new NbtCompound();
            tag.putInt("fermentProgressActual", fermentProgress);
            float a = (float)fermentProgress/fermentTime; a *= 100;
            int x = Math.round(a);
            tag.putInt("percentFermented", x);
            tag.putString("basinContent", content.toString());
            stack.getOrCreateNbt().put("bucketData", tag);
            emptyBasin();
            update();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack addCheeseCulture(ItemStack stack) {
        if(content.getContentType() == BasinContentType.MILK) {
            if(stack.getItem() instanceof CheeseCultureItem) {
                CheeseCultureItem culture = (CheeseCultureItem)stack.getItem();
                this.startFermenting(culture.getCheeseType());
                createCheeseParticle(this.world, this.pos, this.rng, 8, content.getCheeseType().getParticleColorRGB());
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 0.82F, 1.0F);
                world.playSound(null, pos, SoundEvents.ITEM_HONEY_BOTTLE_DRINK, SoundCategory.BLOCKS, 1.0F, 1.5F);
                return culture.deplete(stack, 1);
            }
        }
        return stack;
    }

    public void insertCheese(ItemStack cheese) {
        if(cheese.getItem() instanceof CheeseItem && !((CheeseItem)cheese.getItem()).isSlice()) {
            content = CheeseRegistry.INSTANCE.cheeseFromCheeseType(((CheeseItem)cheese.getItem()).getCheeseType());
            cheese.decrement(1);
        }
        update();
    }

    public void emptyBasin() {
        content = BasinContent.AIR;
        fermentProgress = 0;
        update();
    }

    public static void tick(World world, BlockPos pos, BlockState state, BasinBlockEntity self) {
        if(self.content != null) {
            if (self.content.getContentType() == BasinContentType.FERMENTING_MILK) {
                self.fermentProgress++;
            }
        }
        if(self.fermentProgress >= fermentTime) {
            self.finishFermenting();
        }
    }

    public void update() {
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
        Util.sync(this);
        markDirty();
    }

    public static void createCheeseParticle(World world, BlockPos pos, Random random, int count, float[] color) {
        for (int i = 0; i < count; i++) {
            double ox = ((double) random.nextInt(10) / 16);
            double oz = ((double) random.nextInt(10) / 16);
            world.addParticle(new DustParticleEffect(new Vec3f(color[0], color[1], color[2]), 1.0F), pos.getX() + ox + 0.2, pos.getY() + 0.4, pos.getZ() + oz + 0.2, 0.0D, 0.09D, 0.0D);
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return isEmpty() && (stack.getItem() instanceof CheeseItem && !((CheeseItem)stack.getItem()).isSlice());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.getContent() == BasinContent.AIR;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getContent().getContentType() == BasinContentType.CHEESE ? new ItemStack(cheeseTypeToItem().get(this.content.getCheeseType())) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack r = getStack(slot);
        emptyBasin();
        update();
        return r;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(stack.getItem() instanceof CheeseItem && !((CheeseItem)stack.getItem()).isSlice()) {
            insertCheese(stack);
        }
        update();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        emptyBasin();
        update();
    }
}
