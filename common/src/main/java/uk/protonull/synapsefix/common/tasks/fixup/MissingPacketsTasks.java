package uk.protonull.synapsefix.common.tasks.fixup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class MissingPacketsTasks {
    public static final String SELF_LOCATION = "SelfLocation";
    public static final String SNITCH_HIT = "SnitchHit";

    /**
     * Pretend that the SelfLocation is an AccountLocation within a Bulk packet.
     */
    public static @NotNull String transformSelfLocation(
            final @NotNull JsonObject json
    ) {
        final var bulkPacket = new JsonObject();
        bulkPacket.addProperty("msgType", "Bulk");
        bulkPacket.add("accountInfos", new JsonArray());
        bulkPacket.add("accountStatuses", new JsonArray());
        final var accountLocations = new JsonArray(); {
            final var accountLocation = new JsonObject();
            bulkPacket.addProperty("msgType", "AccountLocation");
            bulkPacket.add("ts", json.get("ts"));
            bulkPacket.add("uuid", json.get("uuid"));
            bulkPacket.add("mcName", json.get("mcName"));
            bulkPacket.add("world", json.get("world"));
            bulkPacket.add("x", json.get("x"));
            bulkPacket.add("y", json.get("y"));
            bulkPacket.add("z", json.get("z"));
            bulkPacket.addProperty("tolerance", 0);
            bulkPacket.add("isLogout", json.get("isLogout"));
            accountLocations.add(accountLocation);
        }
        bulkPacket.add("accountLocations", accountLocations);
        return bulkPacket.toString();
    }

    /** TODO: Figure out what to do with this packet. Until then, just return empty Bulk. */
    public static @NotNull String transformSnitchHit(
            final @NotNull JsonObject json
    ) {
        final var bulkPacket = new JsonObject();
        bulkPacket.addProperty("msgType", "Bulk");
        bulkPacket.add("accountInfos", new JsonArray());
        bulkPacket.add("accountStatuses", new JsonArray());
        bulkPacket.add("accountLocations", new JsonArray());
        return bulkPacket.toString();
    }
}
