package uk.protonull.synapsefix.common.mixins.illegal;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.java_websocket.client.WebSocketClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.protonull.synapsefix.common.utilities.ClassUtils;

/**
 * Prevents Synapse from sending radar data to Synapse, regardless of config setting.
 */
@Mixin(value = WebSocketClient.class, remap = false)
public abstract class PreventRadarSyncingMixin {
    @ModifyVariable(method = "send(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true, remap = false)
    public String sf_inject$send(
            final String message
    ) {
        if (!ClassUtils.isSynapseWebsocket((WebSocketClient) (Object) this)) {
            return message;
        }

        final JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        if (!"Bulk".equals(json.get("msgType").getAsString())) {
            return message;
        }

        final String playerName, playerUuid; {
            final User user = Minecraft.getInstance().getUser();
            playerName = user.getName();
            playerUuid = user.getUuid();
        }

        Iterables.removeIf(json.getAsJsonArray("accountLocations"), (element) -> {
            final JsonObject trackedLocation = element.getAsJsonObject();
            return !"AccountLocation".equals(trackedLocation.get("msgType").getAsString())
                    || !playerUuid.equals(trackedLocation.get("uuid").getAsString())
                    || !playerName.equals(trackedLocation.get("mcName").getAsString());
        });

        return json.toString();
    }
}
