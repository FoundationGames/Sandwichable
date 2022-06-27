package io.github.foundationgames.sandwichable.blocks.entity;

import com.google.common.base.Objects;
import io.github.foundationgames.sandwichable.Sandwichable;
import io.github.foundationgames.sandwichable.blocks.BlocksRegistry;
import io.github.foundationgames.sandwichable.config.SandwichableConfig;
import io.github.foundationgames.sandwichable.items.ItemsRegistry;
import io.github.foundationgames.sandwichable.items.KitchenKnifeItem;
import io.github.foundationgames.sandwichable.recipe.CuttingRecipe;
import io.github.foundationgames.sandwichable.util.Util;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CuttingBoardBlockEntity extends BlockEntity implements SyncedBlockEntity, SidedInventory {
    private ItemStack item = ItemStack.EMPTY;
    private ItemStack knife = ItemStack.EMPTY;

    private int knifeAnimationTicks = 0;

    private int lastItemCount = 0;

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.CUTTINGBOARD_BLOCKENTITY, pos, state);
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
        SandwichableConfig cfg = Util.getConfig();
        for (SandwichableConfig.KitchenKnifeOption opt : cfg.itemOptions.knives) {
            Identifier id = Identifier.tryParse(opt.itemId);
            if (id != null) {
                Item item = Registry.ITEM.get(id);
                if (player.getStackInHand(Hand.MAIN_HAND).getItem() == item || player.getStackInHand(Hand.OFF_HAND).getItem() == item) {
                    knifeHand = player.getStackInHand(Hand.MAIN_HAND).getItem() == item ? Hand.MAIN_HAND : Hand.OFF_HAND;
                    itemHand = knifeHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
                    cut = KitchenKnifeItem.getItemCutAmount(player.getStackInHand(knifeHand));
                }
            }
        }
        ItemStack iStack = player.getStackInHand(itemHand);
        boolean hasKnife = knifeHand != null;
        boolean hasItem = !iStack.isEmpty();
        if (hasItem) {
            if (getItem().getCount() < getItem().getMaxCount() && (!hasKnife || getItem().getCount() < cut) && (getItem().isEmpty() || (getItem().isItemEqual(iStack) && Objects.equal(getItem().getNbt(), iStack.getNbt())))) {
                if (!getItem().isEmpty()) {
                    getItem().setCount(getItem().getCount() + 1);
                    if (!player.isCreative()) iStack.decrement(1);
                }
                else if (player.isCreative()) {
                    item = iStack.copy();
                    item.setCount(1);
                } else item = iStack.split(1);
                if (itemHand == Hand.OFF_HAND) player.swingHand(Hand.OFF_HAND);
                return ActionResult.success(itemHand == Hand.MAIN_HAND && world.isClient());
            }
        }
        if (hasKnife && !item.isEmpty() && knife.isEmpty()) {
            slice(cut);
            if (cut > 0 && player instanceof ServerPlayerEntity) {
                Sandwichable.CUT_ITEM.trigger((ServerPlayerEntity) player);
            }
            if (!player.isCreative()) KitchenKnifeItem.processCut(player.getStackInHand(knifeHand), cut);
            if(knifeHand == Hand.OFF_HAND) player.swingHand(knifeHand);
            return ActionResult.success(knifeHand != Hand.OFF_HAND && world.isClient());
        }
        if (hasKnife && knife.isEmpty()) {
            this.knife = player.getStackInHand(knifeHand).split(1);
            player.getStackInHand(knifeHand).decrement(1);
            return ActionResult.success(world.isClient());
        }
        if (!getItem().isEmpty() && knife.isEmpty()) {
            ItemEntity entity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, player.isSneaking() ? getItem() : getItem().split(1));
            if(player.isSneaking()) item = ItemStack.EMPTY;
            if(!player.isCreative()) {
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
            return ActionResult.success(world.isClient());
        }
        if(!knife.isEmpty()) {
            player.getInventory().offerOrDrop(getKnife());
            knife = ItemStack.EMPTY;
            return ActionResult.success(world.isClient());
        }
        return ActionResult.PASS;
    }

    private static Hand isHolding(PlayerEntity player, Item item) {
        return player.getStackInHand(Hand.MAIN_HAND).getItem() == item ? Hand.MAIN_HAND : player.getStackInHand(Hand.OFF_HAND).getItem() == item ? Hand.OFF_HAND : null;
    }

    public void update() {
        Util.sync(this);
        world.updateComparators(pos, world.getBlockState(pos).getBlock());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if(nbt.contains("Items")) {
            DefaultedList<ItemStack> list = DefaultedList.ofSize(1, ItemStack.EMPTY);
            Inventories.readNbt(nbt, list);
            item = list.get(0);
        } else {
            item = ItemStack.fromNbt(nbt.getCompound("Item"));
        }
        knife = ItemStack.fromNbt(nbt.getCompound("Knife"));
        knifeAnimationTicks = nbt.getInt("knifeAnim");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("Item", item.writeNbt(new NbtCompound()));
        tag.put("Knife", knife.writeNbt(new NbtCompound()));
        tag.putInt("knifeAnim", knifeAnimationTicks);
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

    public void trySliceWithKnife() {
        if (!this.knife.isEmpty()) {
            this.knifeAnimationTicks = 10;
            int cut = 0;
            SandwichableConfig cfg = Util.getConfig();
            SandwichableConfig.KitchenKnifeOption opt = cfg.getKnifeOption(this.knife.getItem());
            if (opt != null) {
                cut = KitchenKnifeItem.getItemCutAmount(this.knife);
                KitchenKnifeItem.processCut(this.knife, cut);
            }
            this.slice(cut);
            update();
        }
    }

    private void slice(int amount) {
        amount = Math.min(amount, getItem().getCount());
        SimpleInventory inv = new SimpleInventory(getItem());
        Optional<CuttingRecipe> match = world.getRecipeManager().getFirstMatch(CuttingRecipe.Type.INSTANCE, inv, world);
        if (match.isPresent()) {
            final ItemStack output = match.get().craft(inv).copy();
            int nc = output.getCount() * amount;
            int maxCount = output.getItem().getMaxCount();
            for (int i = 0; i < Math.ceil((float)nc / maxCount); i++) {
                ItemStack result = output.copy();
                result.setCount(Math.min(maxCount, (nc - (i * maxCount)) % (maxCount)));
                ItemEntity entity = new ItemEntity(world, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, result);
                BlockPos dir = BlockPos.ORIGIN.offset(world.getBlockState(pos).get(Properties.HORIZONTAL_FACING).getOpposite());
                entity.setVelocity((dir.getX() * 0.15) + ((world.random.nextDouble() - 0.5) * 0.08), 0.17, (dir.getZ() * 0.15) + ((world.random.nextDouble() - 0.5) * 0.08));
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            }
            particles(output, amount);
            getItem().decrement(amount);
            if (amount > 0) world.playSound(null, pos.getX()+0.5, pos.getY()+0.3, pos.getZ()+0.5, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 0.7f, 0.8f);
        }
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return stack.getItem() != ItemsRegistry.SANDWICH && Util.getConfig().getKnifeOption(stack.getItem()) == null;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return knife.isEmpty();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return item;
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
        this.item = stack;
        update();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.item = ItemStack.EMPTY;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CuttingBoardBlockEntity self) {
        if (self.item.getCount() != self.lastItemCount) {
            self.update();
        }
        self.lastItemCount = self.item.getCount();
        if (self.knifeAnimationTicks > 0) self.knifeAnimationTicks--;
    }

    public int getKnifeAnimationTicks() {
        return knifeAnimationTicks;
    }

    private void particles(ItemStack stack, int depth) {
        if (!world.isClient()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeItemStack(stack);
            buf.writeInt(this.getItem().getCount());
            buf.writeInt(depth);
            buf.writeBlockPos(pos);
            for (PlayerEntity player : world.getPlayers()) {
                if (player instanceof ServerPlayerEntity && player.getPos().distanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ())) < 100) {
                    ServerPlayNetworking.send((ServerPlayerEntity)player, Util.id("cutting_board_particles"), buf);
                }
            }
        }
    }
}
