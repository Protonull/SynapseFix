package uk.protonull.synapsefix.common.mixins.privacy;

import com.mojang.blaze3d.pipeline.RenderTarget;
import gjum.minecraft.civ.synapse.common.SynapseMod;
import java.io.File;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SynapseMod.class, remap = false)
public class ScreenshotPrivacyMixin {
    @Inject(method = "handleGrabScreenshot", at = @At("HEAD"), cancellable = true)
    public void INJECT_handleGrabScreenshot(
            final File file,
            final @Nullable String string,
            final RenderTarget renderTarget,
            final Consumer<Component> consumer,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(false);
    }
}
