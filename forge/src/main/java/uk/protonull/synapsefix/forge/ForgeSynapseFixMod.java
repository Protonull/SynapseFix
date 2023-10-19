package uk.protonull.synapsefix.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import uk.protonull.synapsefix.common.SynapseFixMod;

@Mod("synapsefix")
public class ForgeSynapseFixMod extends SynapseFixMod {
    public ForgeSynapseFixMod() {

    }

    @Override
    public boolean isModAvailable(
            final @NotNull String modId
    ) {
        return ModList.get().isLoaded(modId);
    }
}
