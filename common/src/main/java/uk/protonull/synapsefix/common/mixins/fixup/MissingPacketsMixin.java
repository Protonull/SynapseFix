package uk.protonull.synapsefix.common.mixins.fixup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.protonull.synapsefix.common.tasks.fixup.MissingPacketsTasks;
import uk.protonull.synapsefix.common.utilities.SynapseIntegrations;

/**
 * Transforms unimplemented packets given the effective abandonment of Synapse in favour of Nexum.
 */
@Mixin(
        value = WebSocketClient.class,
        remap = false
)
public class MissingPacketsMixin {
    @ModifyVariable(
            method = "onWebsocketMessage(Lorg/java_websocket/WebSocket;Ljava/lang/String;)V",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    public String sf$onWebsocketMessage(
            final String message
    ) {
        if (!SynapseIntegrations.isSynapseWebsocket((WebSocketClient) (Object) this)) {
            return message;
        }

        final JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        return switch (json.get("msgType").getAsString()) {
            case MissingPacketsTasks.SELF_LOCATION -> MissingPacketsTasks.transformSelfLocation(json);
            case MissingPacketsTasks.SNITCH_HIT -> MissingPacketsTasks.transformSnitchHit(json);
            default -> message;
        };
    }
}
