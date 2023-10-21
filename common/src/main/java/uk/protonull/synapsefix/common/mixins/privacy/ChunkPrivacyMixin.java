package uk.protonull.synapsefix.common.mixins.privacy;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SynapseMod.class, remap = false)
public class ChunkPrivacyMixin {
    @Inject(method = "handlePacketReceiving", at = @At("HEAD"), cancellable = true)
    public void INJECT_handlePacketReceiving(
            final Packet<?> packet,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        if (packet instanceof ClientboundLevelChunkWithLightPacket) {
            cir.setReturnValue(false); // false, otherwise the packet will be dropped entirely
        }
    }
}
