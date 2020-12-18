package io.github.foundationgames.sandwichable.mixin;

import io.github.foundationgames.sandwichable.util.LowDetailItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModels.class)
public abstract class ItemModelsMixin {
    @Shadow public abstract Sprite getSprite(ItemConvertible itemConvertible);

    @Inject(method = "reloadModels", at = @At("TAIL"))
    public void reloadItemColors(CallbackInfo ci) {
        for(ItemConvertible i : Registry.ITEM) {
            Sprite sprite = this.getSprite(i);
            SpriteAccess s = (SpriteAccess) sprite;
            NativeImage n = s.getImages()[Math.min(4, s.getImages().length - 1)];
            int c = n.getPixelColor(0, 0);
            int fb = (c >> 16) & 0xFF;
            int fg = (c >> 8) & 0xFF;
            int fr = c & 0xFF;
            LowDetailItemRenderer.ITEM_COLOR_MAP.put(i, ((fr & 0xFF) << 16) | ((fg & 0xFF) << 8) | ((fb & 0xFF)));
        }
    }
}
