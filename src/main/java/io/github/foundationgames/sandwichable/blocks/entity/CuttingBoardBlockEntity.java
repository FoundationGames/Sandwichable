package io.github.foundationgames.sandwichable.blocks.entity;

import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuttingBoardBlockEntity extends BlockEntity implements BlockEntityClientSerializable, SidedInventory {

    private ItemStack item = ItemStack.EMPTY;
    private ItemStack knife = ItemStack.EMPTY;

    public CuttingBoardBlockEntity() {
        super(BlocksRegistry.CUTTINGBOARD_BLOCKENTITY);
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getKnife() {
        return knife;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int cut = 0;
        Hand knifeHand = null;
        Hand itemHand = hand;
        SandwichableConfig cfg = AutoConfig.getConfigHolder(SandwichableConfig.class).getConfig();
        for(SandwichableConfig.ItemIntPair p : cfg.itemOptions.knives) {
            Identifier id = Identifier.tryParse(p.itemId);
            if(id != null) {
                Item item = Registry.ITEM.get(id);
                if(player.getStackInHand(Hand.MAIN_HAND).getItem() == item || player.getStackInHand(Hand.OFF_HAND).getItem() == item) {
                    cut = p.value;
                    knifeHand = player.getStackInHand(Hand.MAIN_HAND).getItem() == item ? Hand.MAIN_HAND : Hand.OFF_HAND;
                    itemHand = knifeHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
                }
            }
        }
        ItemStack iStack = player.getStackInHand(itemHand);
        boolean hasKnife = knifeHand != null;
        boolean hasItem = !iStack.isEmpty();
        if(hasItem) {
            if(getItem().getCount() < getItem().getMaxCount() && (!hasKnife || getItem().getCount() < cut) && (getItem().isEmpty() || getItem().isItemEqual(iStack))) {
                if(!getItem().isEmpty()) getItem().setCount(getItem().getCount() + 1);
                else if(player.isCreative()) {
                    item = iStack.copy();
                    item.setCount(1);
                } else item = iStack.split(1);
                if(itemHand == Hand.OFF_HAND) player.swingHand(Hand.OFF_HAND);
                return ActionResult.success(itemHand == Hand.MAIN_HAND && world.isClient());
            }
        }
        if(hasKnife && !item.isEmpty() && knife.isEmpty()) {
            cut = Math.min(cut, getItem().getCount());
            SimpleInventory inv = new SimpleInventory(getItem());
            Optional<CuttingRecipe> match = world.getRecipeManager().getFirstMatch(CuttingRecipe.Type.INSTANCE, inv, world);

            if (match.isPresent()) {
                final ItemStack output = match.get().getOutput().copy();
                int nc = output.getCount() * cut;
                int maxCount = output.getItem().getMaxCount();
                for(int i = 0; i < Math.ceil((float)nc / maxCount); i++) {
                    ItemStack result = output.copy();
                    result.setCount(Math.min(maxCount, (nc - (i * maxCount)) % (maxCount)));
                    world.spawnEntity(new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, result));
                }
                getItem().decrement(cut);
                //world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, blockEntity.getItem()), pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, 0.0D, 0.0D, 0.0D);
                world.playSound(player, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 0.7f, 0.8f);
            }
            if(knifeHand == Hand.OFF_HAND) player.swingHand(knifeHand);
            return ActionResult.success(knifeHand != Hand.OFF_HAND && world.isClient());
        }
        if(hasKnife && knife.isEmpty()) {
            this.knife = player.getStackInHand(knifeHand).split(1);
            player.getStackInHand(knifeHand).decrement(1);
            return ActionResult.success(world.isClient());
        }
        if(!getItem().isEmpty() && knife.isEmpty()) {
            ItemEntity entity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, player.isSneaking() ? getItem() : getItem().split(1));
            if(player.isSneaking()) item = ItemStack.EMPTY;
            if(!player.isCreative()) {
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
            return ActionResult.success(world.isClient());
        }
        if(!knife.isEmpty()) {
            player.inventory.offerOrDrop(world, getKnife());
            knife = ItemStack.EMPTY;
            return ActionResult.success(world.isClient());
        }
        return ActionResult.PASS;
    }

    private static Hand isHolding(PlayerEntity player, Item item) {
        return player.getStackInHand(Hand.MAIN_HAND).getItem() == item ? Hand.MAIN_HAND : player.getStackInHand(Hand.OFF_HAND).getItem() == item ? Hand.OFF_HAND : null;
    }

    public void update() {
        Util.sync(this, this.world);
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if(tag.contains("Items")) {
            DefaultedList<ItemStack> list = DefaultedList.ofSize(1, ItemStack.EMPTY);
            Inventories.fromTag(tag, list);
            item = list.get(0);
        } else {
            item = ItemStack.fromTag(tag.getCompound("Item"));
        }
        knife = ItemStack.fromTag(tag.getCompound("Knife"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Item", item.toTag(new CompoundTag()));
        tag.put("Knife", knife.toTag(new CompoundTag()));
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

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return slot == 0 || (slot == 1 && stack.getItem().isIn(Sandwichable.KNIVES) && knife.isEmpty());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 0 || (slot == 1 && dir == Direction.DOWN);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty() && knife.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == 1 ? knife : item;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack r = getStack(slot).split(amount);
        update();
        return r;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, getStack(slot).getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot == 1 && stack.getItem().isIn(Sandwichable.KNIVES)) this.knife = stack;
        else this.item = stack;
        update();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.item = ItemStack.EMPTY;
        this.knife = ItemStack.EMPTY;
    }
}
