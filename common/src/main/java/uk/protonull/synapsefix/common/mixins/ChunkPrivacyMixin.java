package uk.protonull.synapsefix.common.mixins;

import gjum.minecraft.civ.synapse.features.cartography.Cartography;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Cartography.class, remap = false)
public class ChunkPrivacyMixin {
    @Inject(method = "handleChunkPacket", at = @At("HEAD"), cancellable = true)
    public void INJECT_handleChunkPacket(
            final ClientboundLevelChunkWithLightPacket packet,
            final CallbackInfo ci
    ) {
        ci.cancel();
    }

    @Inject(method = "handleTick", at = @At("HEAD"), cancellable = true)
    public void INJECT_handleTick(
            final CallbackInfo ci
    ) {
        ci.cancel();
    }
}
