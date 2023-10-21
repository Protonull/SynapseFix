package uk.protonull.synapsefix.common.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.jetbrains.annotations.NotNull;

public final class HashUtils {
    public static @NotNull MessageDigest sha1() {
        try {
            return MessageDigest.getInstance("SHA-1");
        }
        catch (final NoSuchAlgorithmException thrown) {
            throw new IllegalStateException("This should never happen!", thrown);
        }
    }

    public static @NotNull String toHex(
            final @NotNull MessageDigest hasher
    ) {
        return HexFormat.of().formatHex(hasher.digest());
    }
}
