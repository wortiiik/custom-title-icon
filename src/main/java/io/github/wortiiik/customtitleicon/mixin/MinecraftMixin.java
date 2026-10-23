package io.github.wortiiik.customtitleicon.mixin;

import io.github.wortiiik.customtitleicon.title.WindowTitle;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {

    @Inject(method = "createTitle", at = @At("HEAD"), cancellable = true)
    private void customtitleicon$createTitle(CallbackInfoReturnable<String> callback) {
        WindowTitle.compose((Minecraft) (Object) this).ifPresent(callback::setReturnValue);
    }
}
