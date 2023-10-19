package uk.protonull.synapsefix.common;

import org.jetbrains.annotations.NotNull;

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
}
