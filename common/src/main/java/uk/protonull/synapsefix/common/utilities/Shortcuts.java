package uk.protonull.synapsefix.common.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HexFormat;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a utility class specifically intended for small, miscellaneous utilities.
 */
public final class Shortcuts {
    /**
     * All Java versions and platforms are expected to implement SHA-1, so the {@link NoSuchAlgorithmException} should
     * never be thrown, so returning a valid {@link MessageDigest} should be a given.
     */
    public static @NotNull MessageDigest sha1() {
        try {
            return MessageDigest.getInstance("SHA-1");
        }
        catch (final NoSuchAlgorithmException thrown) {
            throw new IllegalStateException("This should never happen!", thrown);
        }
    }

    public static @NotNull String toHexString(
            final byte @NotNull [] data
    ) {
        return HexFormat.of().formatHex(data);
    }

    public static boolean matchesRegex(
            final @NotNull Pattern pattern,
            final @NotNull String value
    ) {
        return pattern.matcher(value).matches();
    }

    /**
     * Gets a file's string content, returning null if the file does not exist.
     */
    public static @Nullable String readStringFromFile(
            final @NotNull File file
    ) throws IOException {
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        }
        catch (final FileNotFoundException ignored) {
            return null;
        }
    }

    public static void writeStringToFile(
            final @NotNull File file,
            final @NotNull String data
    ) throws IOException {
        FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
    }

    /**
     * Returns an instance of Java's new fancy date/time interfaces, aligned to UTC.
     */
    public static @NotNull ZonedDateTime now() {
        return ZonedDateTime.now(Clock.systemUTC());
    }
}
