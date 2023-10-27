package uk.protonull.synapsefix.common.mixins.privacy;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.protonull.synapsefix.common.utilities.Shortcuts;
import uk.protonull.synapsefix.common.utilities.SynapseIntegrations;

@Mixin(value = SynapseMod.class, remap = false)
public class ChatPrivacyMixin {
    @ModifyVariable(method = "handleChat", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    public Component INJECT_handleChat(
            final Component message
    ) {
        final String chatMessage = message.toString().replaceAll("§.", "");
        if (Shortcuts.matchesRegex(SynapseIntegrations.GROUP_CHAT_REGEX, chatMessage)) {
            return TextComponent.EMPTY;
        }
        if (Shortcuts.matchesRegex(SynapseIntegrations.PRIVATE_MESSAGE_REGEX, chatMessage)) {
            return TextComponent.EMPTY;
        }
        if (Shortcuts.matchesRegex(SynapseIntegrations.LOCAL_CHAT_REGEX, chatMessage)) {
            // TODO: Maybe allow a de-content'd message so that Synapse can
            //       alert of nearby baddies when they speak in local.
            return TextComponent.EMPTY;
        }
        return message;
    }

    /** Prevent Synapse from intercepting outgoing chat messages */
    @Inject(method = "handleOutgoingChat", at = @At("HEAD"), cancellable = true)
    public void INJECT_handleOutgoingChat(
            final String message,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(false);
    }

    /** Prevent Synapse from intercepting outgoing chat packets */
    @Inject(method = "handlePacketSending", at = @At("HEAD"), cancellable = true)
    public void INJECT_handlePacketSending(
            final Packet<?> packet,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        if (packet instanceof ServerboundChatPacket) {
            cir.setReturnValue(false); // false, otherwise the packet will be dropped entirely
        }
    }

    /** Prevent Synapse from intercepting incoming chat packets outside of 'handleChat' */
    @Inject(method = "handlePacketReceiving", at = @At("HEAD"), cancellable = true)
    public void INJECT_handlePacketReceiving(
            final Packet<?> packet,
            final CallbackInfoReturnable<Boolean> cir
    ) {
        if (packet instanceof ClientboundChatPacket) {
            cir.setReturnValue(false); // false, otherwise the packet will be dropped entirely
        }
    }
}
