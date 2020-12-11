package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.entity.EntitiesRegistry;
import io.github.foundationgames.sandwichable.entity.SandwichTableMinecartEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    @ModifyVariable(method = "onEntitySpawn", at = @At(value = "JUMP", opcode = Opcodes.IFNULL, ordinal = 3, shift = At.Shift.BEFORE), index = 8)
    public Entity changeEntityToSpawn(Entity old, EntitySpawnS2CPacket packet) {
        EntityType<?> entityType = packet.getEntityTypeId();
        if(entityType == EntitiesRegistry.SANDWICH_TABLE_MINECART) {
            SandwichTableMinecartEntity e = new SandwichTableMinecartEntity(world, packet.getX(), packet.getY(), packet.getZ());
            return e;
        }
        return old;
    }
}
