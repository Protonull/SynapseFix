package uk.protonull.synapsefix.common.mixins;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import uk.protonull.synapsefix.common.utilities.HashUtils;

@Mixin(value = SynapseMod.class, remap = false)
public abstract class RemoteDownloadMixin {
    /** https://fabricmc.net/wiki/tutorial:mixin_examples#modifying_a_parameter */
    @ModifyVariable(method = "installUpdate", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private InputStream INJECT_installUpdate(
            final InputStream inputStream
    ) {
        final byte[] jarBytes;
        try {
            jarBytes = inputStream.readAllBytes();
            inputStream.close();
        }
        catch (final IOException thrown) {
            // Since this injection must return an InputStream, this will allow
            // the exception to be caught by installUpdate's own try-catch.
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    throw thrown;
                }
            };
        }
        synapseFix$modifyAndSaveRemoteJar(jarBytes);
        return new ByteArrayInputStream(jarBytes);
    }

    @Unique
    private static void synapseFix$modifyAndSaveRemoteJar(
            final byte @NotNull [] jarBytes
    ) {
        final Logger logger = LoggerFactory.getLogger(RemoteDownloadMixin.class);

        // We need to add a "fabric.mod.json" for Loom to consider it a mod, and therefore
        // automatically remap it. It's a massive faff, but better to do it here.

        final byte[] modifiedJarBytes;
        try (final var modifiedJarByteStream = new ByteArrayOutputStream()) {
            try (final var zipOutputStream = new ZipOutputStream(modifiedJarByteStream)) {
                try (final var zipInputStream = new ZipInputStream(new ByteArrayInputStream(jarBytes))) {
                    synapseFix$copyAllEntries(zipInputStream, zipOutputStream);
                }

                // Check with https://fabricmc.net/wiki/documentation:fabric_mod_json_spec
                // on which fields are mandatory
                synapseFix$addNewUncompressedEntry(zipOutputStream, "fabric.mod.json", """
                {
                    "schemaVersion": 1,
                    "id": "synapse-remote",
                    "version": "%s",
                    "description": "We love remote-code execution!"
                }
                """.formatted(SynapseMod.VERSION).getBytes(StandardCharsets.UTF_8));

                zipOutputStream.finish();
            }

            modifiedJarBytes = modifiedJarByteStream.toByteArray();
        }
        catch (final IOException thrown) {
            logger.warn("Could not add fabric.mod.json to remote jar!");
            return;
        }

        try {
            FileUtils.writeByteArrayToFile(
                    Path.of(
                            Minecraft.getInstance().gameDirectory.getAbsolutePath(),
                            "Synapse",
                            "synapse-remote.jar"
                    ).toFile(),
                    modifiedJarBytes
            );
        }
        catch (final IOException thrown) {
            logger.warn("Could not save modified remote jar to file!");
            return;
        }

        synapseFix$saveRemoteJarHash(modifiedJarBytes);
    }

    @Unique
    private static void synapseFix$saveRemoteJarHash(
            final byte @NotNull [] jarBytes
    ) {
        final Logger logger = LoggerFactory.getLogger(RemoteDownloadMixin.class);

        final String jarHash; {
            final MessageDigest hasher = HashUtils.sha1();
            hasher.update(jarBytes);
            jarHash = HashUtils.toHex(hasher);
        }

        try {
            FileUtils.writeByteArrayToFile(
                    Path.of(
                            Minecraft.getInstance().gameDirectory.getAbsolutePath(),
                            "Synapse",
                            "synapse-remote.jar.sha1"
                    ).toFile(),
                    jarHash.getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (final IOException thrown) {
            logger.warn("Could not save remote-jar hash to file!");
            //return;
        }
    }

    /**
     * Copies all files and their contents from one zip to another.
     */
    @Unique
    private static void synapseFix$copyAllEntries(
            final @NotNull ZipInputStream fromInputStream,
            final @NotNull ZipOutputStream toOutputStream
    ) throws IOException {
        final var dataChunk = new byte[1024];
        int numberOfBytesRead;
        for (ZipEntry entry; (entry = fromInputStream.getNextEntry()) != null;) {
            toOutputStream.putNextEntry(entry);
            while ((numberOfBytesRead = fromInputStream.read(dataChunk)) > 0) {
                toOutputStream.write(dataChunk, 0, numberOfBytesRead);
            }
            toOutputStream.closeEntry();
        }
    }

    /**
     * Adds a new uncompressed file to a given zip.
     */
    @Unique
    private static void synapseFix$addNewUncompressedEntry(
            final @NotNull ZipOutputStream zip,
            final @NotNull String name,
            final byte @NotNull [] data
    ) throws IOException {
        final var entry = new ZipEntry(name);
        entry.setSize(data.length);
        entry.setTime(System.currentTimeMillis());
        zip.putNextEntry(entry);
        zip.write(data);
        zip.closeEntry();
    }
}
