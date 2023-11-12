package uk.protonull.synapsefix.common.mixins.illegal;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.java_websocket.client.WebSocketClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.protonull.synapsefix.common.utilities.SynapseIntegrations;

/**
 * Prevents Synapse from sending radar data to Synapse, regardless of config setting.
 */
@Mixin(
        value = WebSocketClient.class,
        remap = false
)
public abstract class PreventRadarSyncingMixin {
    @ModifyVariable(
            method = "send(Ljava/lang/String;)V",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    public String sf$send(
            final String message
    ) {
        if (!SynapseIntegrations.isSynapseWebsocket((WebSocketClient) (Object) this)) {
            return message;
        }

        final JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        if (!"Bulk".equals(json.get("msgType").getAsString())) {
            return message;
        }

        final String playerName, playerUuid; {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) { // Shouldn't happen, but just in case
                json.add("accountLocations", new JsonArray());
                return json.toString();
            }
            final GameProfile profile = player.connection.getLocalGameProfile();
            playerName = profile.getName();
            playerUuid = profile.getId().toString();
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
