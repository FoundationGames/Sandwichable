package io.github.foundationgames.sandwichable.mixin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class SandwichableMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return true; }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        tryMapInsertion(mixinClassName, "io.github.foundationgames.sandwichable.mixin.OxidizableMixin", "method_34740", targetClass,
                "io/github/foundationgames/sandwichable/blocks/BlocksRegistry", "OXIDIZABLES"
        );
        tryMapInsertion(mixinClassName, "io.github.foundationgames.sandwichable.mixin.HoneycombItemMixin", "method_34723", targetClass,
                "io/github/foundationgames/sandwichable/blocks/BlocksRegistry", "WAXABLES"
        );
    }

    public static void tryMapInsertion(String mixinCurrent, String mixinTarget, String methodName, ClassNode targetClass, String mapFieldClass, String mapField) {
        if (mixinTarget.equals(mixinCurrent)) {
            targetClass.methods.forEach(method -> {
                if (methodName.equals(method.name)) {
                    var before = method.instructions.get(method.instructions.size() - 6);
                    var insns = new InsnList();
                    insns.add(new FieldInsnNode(
                            Opcodes.GETSTATIC, mapFieldClass, mapField, "Lcom/google/common/collect/BiMap;")
                    );
                    insns.add(new MethodInsnNode(
                            Opcodes.INVOKEVIRTUAL,
                            "com/google/common/collect/ImmutableBiMap$Builder",
                            "putAll",
                            "(Ljava/util/Map;)Lcom/google/common/collect/ImmutableBiMap$Builder;")
                    );
                    method.instructions.insertBefore(before, insns);
                }
            });
        }
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
