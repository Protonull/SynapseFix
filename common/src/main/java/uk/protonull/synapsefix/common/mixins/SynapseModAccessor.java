package uk.protonull.synapsefix.common.mixins;

import gjum.minecraft.civ.synapse.common.ISynapse;
import gjum.minecraft.civ.synapse.common.SynapseMod;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SynapseMod.class, remap = false)
public interface SynapseModAccessor {
    @Accessor(value = "synapse", remap = false)
    @Nullable ISynapse getSynapseInstance();
}
