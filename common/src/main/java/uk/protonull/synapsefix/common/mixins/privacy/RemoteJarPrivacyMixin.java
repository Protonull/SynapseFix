package uk.protonull.synapsefix.common.mixins.privacy;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import org.apache.http.client.utils.URIBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = SynapseMod.class, remap = false)
public class RemoteJarPrivacyMixin {
    /**
     * Prevents Synapse from serving you a user-specific jar.
     */
    @ModifyVariable(method = "requestUpdate(Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lorg/apache/http/client/utils/URIBuilder;build()Ljava/net/URI;"))
    private URIBuilder INJECT_requestUpdate(
            final URIBuilder builder
    ) {
        // TODO: Make this configurable instead of hard coded
        // TODO: Make this work (it resulted in a 403)
        //builder.setParameter("username", "Shadno");
        return builder;
    }
}
