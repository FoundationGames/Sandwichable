package io.github.foundationgames.sandwichable.util;

import net.minecraft.block.Block;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {

    public static String MOD_ID = "sandwichable";

    public static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }

    public static Identifier idd(String name) { return new Identifier(MOD_ID+":"+name); }

    public static void scatterBlockDust(World world, BlockPos pos, Block block, int intensity, int density) {
        Random random = new Random();
        for (int i = 0; i < density; i++) {
            double ox, oy, oz, vx, vy, vz;
            ox = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oy = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            oz = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vx = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vy = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            vz = (double)(random.nextInt(intensity * 2) - intensity) / 10;
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, block.getDefaultState()), pos.getX()+0.5+ox, pos.getY()+0.5+oy, pos.getZ()+0.5+oz, 0.0D+vx, 0.0D+vy, 0.0D+vz);
        }
    }
}
