package uk.protonull.synapsefix.common.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

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
}
