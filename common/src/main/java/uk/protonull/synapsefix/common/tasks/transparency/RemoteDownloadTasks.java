package uk.protonull.synapsefix.common.tasks.transparency;

import gjum.minecraft.civ.synapse.common.SynapseMod;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.jetbrains.annotations.NotNull;
import uk.protonull.synapsefix.common.utilities.Shortcuts;
import uk.protonull.synapsefix.common.utilities.SynapseIntegrations;

public final class RemoteDownloadTasks {
    public static @NotNull InputStream newThrowImmediatelyInputStream(
            final @NotNull IOException exception
    ) {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                throw exception;
            }
        };
    }

    public static byte @NotNull [] injectFabricModJson(
            final byte @NotNull [] jarBytes
    ) throws IOException {
        final byte[] modifiedJarBytes;
        try (final var modifiedJarByteStream = new ByteArrayOutputStream()) {
            try (final var zipOutputStream = new ZipOutputStream(modifiedJarByteStream)) {
                try (final var zipInputStream = new ZipInputStream(new ByteArrayInputStream(jarBytes))) {
                    copyAllZipEntries(zipInputStream, zipOutputStream);
                }

                // Check with https://fabricmc.net/wiki/documentation:fabric_mod_json_spec
                // on which fields are mandatory
                addNewUncompressedZipEntry(zipOutputStream, "fabric.mod.json", """
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
            throw new IOException("Could not add fabric.mod.json to jar!", thrown);
        }

        return modifiedJarBytes;
    }

    /**
     * Copies all files and their contents from one zip to another.
     */
    public static void copyAllZipEntries(
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
            fromInputStream.closeEntry();
            toOutputStream.closeEntry();
        }
    }

    /**
     * Adds a new uncompressed file to a given zip.
     */
    public static void addNewUncompressedZipEntry(
            final @NotNull ZipOutputStream zip,
            final @NotNull String name,
            final byte @NotNull [] data
    ) throws IOException {
        final var entry = new ZipEntry(name);
        entry.setSize(data.length);
        zip.putNextEntry(entry);
        zip.write(data);
        zip.closeEntry();
    }

    public static @NotNull File generateDecompileDir() {
        final ZonedDateTime now = Shortcuts.now();
        return new File(
                SynapseIntegrations.getSynapseDir(),
                "decompiled/%d-%02d-%02d-%05d".formatted(
                        now.getYear(),
                        now.getMonthValue(),
                        now.getDayOfMonth(),
                        now.get(ChronoField.SECOND_OF_DAY)
                )
        );
    }

    public static void decompileJar(
            final @NotNull File inputJar,
            final @NotNull File outputFolder
    ) throws IOException {
        try {
            org.benf.cfr.reader.Main.main(new String[] {
                    inputJar.getAbsolutePath(),
                    "--outputpath", outputFolder.getAbsolutePath(),
                    "--clobber", "true",
                    "--silent", "true"
            });
        }
        catch (final Exception thrown) {
            throw new IOException("Could not decompile " + inputJar.getName() + "!", thrown);
        }
    }
}
