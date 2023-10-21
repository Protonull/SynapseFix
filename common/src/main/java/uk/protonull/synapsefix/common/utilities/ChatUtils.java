package uk.protonull.synapsefix.common.utilities;

import java.util.regex.Pattern;

public final class ChatUtils {
    public static final Pattern GROUP_CHAT_REGEX = Pattern.compile("^\\[.+?] [a-zA-Z0-9_]{3,16}: .+?$");
    public static final Pattern LOCAL_CHAT_REGEX = Pattern.compile("^<[a-zA-Z0-9_]{3,16}> .+?$");
    public static final Pattern PRIVATE_MESSAGE_REGEX = Pattern.compile("^<[a-zA-Z0-9_]{3,16}> .+?$");
}
