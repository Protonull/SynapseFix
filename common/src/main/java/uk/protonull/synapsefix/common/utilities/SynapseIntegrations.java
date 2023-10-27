package uk.protonull.synapsefix.common.utilities;

import java.util.regex.Pattern;
import org.java_websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;

/**
 * This is a utility class specifically meant for easier Synapse detection and integration.
 */
public final class SynapseIntegrations {
    public static final Pattern GROUP_CHAT_REGEX = Pattern.compile("^\\[.+?] [a-zA-Z0-9_]{3,16}: .+?$");
    public static final Pattern LOCAL_CHAT_REGEX = Pattern.compile("^<[a-zA-Z0-9_]{3,16}> .+?$");
    public static final Pattern PRIVATE_MESSAGE_REGEX = Pattern.compile("^<[a-zA-Z0-9_]{3,16}> .+?$");

    public static boolean isSynapseWebsocket(
            final @NotNull WebSocketClient client
    ) {
        /** {@link gjum.minecraft.civ.synapse.comms.CommsConnection} */
        return "gjum.minecraft.civ.synapse.comms.CommsConnection".equals(client.getClass().getName());
    }
}
