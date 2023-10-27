package uk.protonull.synapsefix.common.mixins.fixup;

import java.util.TreeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.protonull.synapsefix.common.tasks.fixup.WaypointCleanupTasks;
import uk.protonull.synapsefix.common.utilities.Shortcuts;

/** {@link com.mamiyaotaru.voxelmap.WaypointManager} */
@Mixin(targets = "com.mamiyaotaru.voxelmap.WaypointManager")
public abstract class VoxelMapWaypointCleanupMixin {
    /**
     * This prevents the loading / registering of waypoints if they can be 'reflectively' considered a Synapse
     * waypoint. This will mean that a user's map will be relatively bare until Synapse decides to re-send them and
     * 'update' to the waypoint, ie, recreating the waypoint, which I do not believe Synapse does upon connection.
     */
    @Inject(method = "loadWaypoint", at = @At("HEAD"), cancellable = true, remap = false)
    private void sf_inject$loadWaypoint(
            final String name,
            final int x,
            final int z,
            final int y,
            final boolean enabled,
            final float red,
            final float green,
            final float blue,
            final String suffix,
            final String world,
            @SuppressWarnings("rawtypes") final TreeSet dimensions,
            final CallbackInfo ci
    ) {
        if (!WaypointCleanupTasks.VOXELMAP_WAYPOINT_ICONS.contains(suffix)) {
            return;
        }
        if (!Shortcuts.matchesRegex(WaypointCleanupTasks.PLAYER_WAYPOINT_REGEX, name)) {
            return;
        }
        ci.cancel();
    }
}
