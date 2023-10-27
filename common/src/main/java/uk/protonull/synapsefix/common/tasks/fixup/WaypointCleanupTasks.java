package uk.protonull.synapsefix.common.tasks.fixup;

import java.util.List;
import java.util.regex.Pattern;

public final class WaypointCleanupTasks {
    /**
     * Keep an eye on {@link gjum.minecraft.civ.synapse.waypoints.AccountWaypoint#namePattern} for the regex Synapse
     * uses to match its waypoints. It's copied here directly rather than reflected or accessor'd because it's
     * contained within the remote-jar, which may not necessarily be loaded at server-login. Synapse was  notorious for
     * silently failing to connect during the <a href="https://civwiki.org/wiki/Butternut-SEC_War">Buttsecs War</a>,
     * and so players were left with unreliable data left on their maps.
     */
    public static final Pattern PLAYER_WAYPOINT_REGEX = Pattern.compile("^(?:Pearl of )?(?:\\[[^]]+] *)?!?~?\\*?(?<account>[_A-Za-z0-9]{3,17})!?~?\\*?(?: *\\[[^]]+])?(?: ?[*+(]\\S+\\)?!?~?\\*?)?(?: *\\[[^]]+])? +\\(?(?<age>now|(?:[0-9]+h ?|[0-9]+min ?|[0-9]+s)+|[0-9]+/[0-9]+ [0-9]+:[0-9]+|old)\\)? *$");

    /** All the VoxelMap icons Synapse currently uses. */
    public static final List<String> VOXELMAP_WAYPOINT_ICONS = List.of(
            "person",
            "camera",
            "record",
            "small"
    );
}
