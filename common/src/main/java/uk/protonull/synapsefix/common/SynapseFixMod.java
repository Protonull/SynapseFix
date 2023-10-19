package uk.protonull.synapsefix.common;

import org.jetbrains.annotations.NotNull;
import uk.protonull.synapsefix.common.hooks.ModPlatform;

public abstract class SynapseFixMod {
    protected SynapseFixMod() {
        if (instance != null) {
            throw new IllegalStateException("Cannot have more than one " + getClass().getSimpleName() + "!");
        }
        instance = this;
    }

    private static SynapseFixMod instance;
    public static @NotNull SynapseFixMod getInstance() {
        return instance;
    }

    /**
     * Determines whether a given modId represents a loaded mod; that its classes are available. Users should
     * nonetheless check that the mod is ready.
     */
    public abstract boolean isModAvailable(
            @NotNull String modId
    );

    /**
     * Declares which platform this running is on. It's a PITA but necessary requirement since certain mods have
     * different constants (ಠ_ಠ) based on what platform it's on.
     */
    public abstract @NotNull ModPlatform getPlatform();
}
