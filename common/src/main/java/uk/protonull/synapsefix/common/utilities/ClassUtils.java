package uk.protonull.synapsefix.common.utilities;

import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;

public final class ClassUtils {
    public static boolean isSynapseWebsocket(
            final @NotNull WebSocketClient client
    ) {
        /** {@link gjum.minecraft.civ.synapse.comms.CommsConnection} */
        return "gjum.minecraft.civ.synapse.comms.CommsConnection".equals(client.getClass().getName());
    }
}
