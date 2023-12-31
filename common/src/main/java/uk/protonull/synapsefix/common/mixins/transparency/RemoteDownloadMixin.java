package uk.protonull.synapsefix.common.mixins.transparency;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.protonull.synapsefix.common.tasks.transparency.RemoteDownloadTasks;
import uk.protonull.synapsefix.common.utilities.Shortcuts;
import uk.protonull.synapsefix.common.utilities.SynapseIntegrations;

@Mixin(
        value = SynapseMod.class,
        remap = false
)
public abstract class RemoteDownloadMixin {
    @ModifyVariable(
            method = "installUpdate",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private InputStream INJECT_installUpdate(
            final InputStream inputStream
    ) {
        final byte[] jarBytes;
        try {
            jarBytes = inputStream.readAllBytes();
            inputStream.close();
        }
        catch (final IOException thrown) {
            return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                    "Could not read the remote jar's bytes!",
                    thrown
            ));
        }

        final File synapseDir = SynapseIntegrations.getSynapseDir();
        synapseDir.mkdirs(); // Shouldn't be necessary, but just in case

        // Get the SHA1 hash for jarBytes
        final String remoteJarHash; {
            final MessageDigest hasher = Shortcuts.sha1();
            hasher.update(jarBytes);
            remoteJarHash = Shortcuts.toHexString(hasher.digest());
        }

        // Check whether that SHA1 hash matches the last known hash
        final File lastKnownRemoteJarHashFile = new File(synapseDir, "synapse-remote.jar.sha1");
        final String lastKnownRemoteJarHash;
        try {
            lastKnownRemoteJarHash = Shortcuts.readStringFromFile(lastKnownRemoteJarHashFile);
        }
        catch (final IOException thrown) {
            return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                    "Could not read last known remote jar hash file!",
                    thrown
            ));
        }

        // If the hashes do not match, save the remote jar
        if (!StringUtils.equals(remoteJarHash, lastKnownRemoteJarHash)) {
            // Save the remote jar as-is
            final var remoteJarFile = new File(synapseDir, "synapse-remote.jar");
            try {
                FileUtils.writeByteArrayToFile(remoteJarFile, jarBytes);
            }
            catch (final IOException thrown) {
                return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                        "Could not save remote jar to '" + remoteJarFile.getAbsolutePath() + "'!",
                        thrown
                ));
            }

            // Re[set] the last known hash
            try {
                Shortcuts.writeStringToFile(
                        lastKnownRemoteJarHashFile,
                        remoteJarHash
                );
            }
            catch (final IOException thrown) {
                return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                        "Could not save last known hash to '" + lastKnownRemoteJarHashFile.getAbsolutePath() + "'!",
                        thrown
                ));
            }

            // Create a decompilation destination for the remote jar
            final File decompileDir = RemoteDownloadTasks.generateDecompileDir();
            decompileDir.mkdirs();

            // Decompile the remote jar so that it can be inspected with Meld
            try {
                RemoteDownloadTasks.decompileJar(remoteJarFile, decompileDir);
            }
            catch (final IOException thrown) {
                return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                        "Could not decompile to '" + decompileDir.getAbsolutePath() + "'!",
                        thrown
                ));
            }

            // Saving the remote jar with an injected fabric.mod.json file
            final var modifiedRemoteJarFile = new File(synapseDir, "synapse-remote.dev.jar");
            try {
                FileUtils.writeByteArrayToFile(
                        modifiedRemoteJarFile,
                        RemoteDownloadTasks.injectFabricModJson(jarBytes)
                );
            }
            catch (final IOException thrown) {
                return RemoteDownloadTasks.newThrowImmediatelyInputStream(new IOException(
                        "Could not modify / save remote jar to '" + modifiedRemoteJarFile.getAbsolutePath() + "'!",
                        thrown
                ));
            }

            // Notify the user whenever a new Synapse jar is downloaded
            Minecraft.getInstance().gui.handleChat(
                    ChatType.SYSTEM,
                    new TextComponent("New Synapse Version!")
                            .withStyle(ChatFormatting.UNDERLINE)
                            .withStyle((style) -> style.withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    new TextComponent("Click to view: " + decompileDir.getAbsolutePath())
                            )))
                            .withStyle((style) -> style.withClickEvent(new ClickEvent(
                                    ClickEvent.Action.OPEN_URL,
                                    decompileDir.getAbsolutePath()
                            ))),
                    new UUID(0L, 0L)
            );
        }

        return new ByteArrayInputStream(jarBytes);
    }
}
