package uk.protonull.synapsefix.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import uk.protonull.synapsefix.common.SynapseFixMod;
import uk.protonull.synapsefix.common.hooks.ModPlatform;

public class FabricSynapseFixMod extends SynapseFixMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    @Override
    public boolean isModAvailable(
            final @NotNull String modId
    ) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public @NotNull ModPlatform getPlatform() {
        return ModPlatform.FABRIC;
    }
}
