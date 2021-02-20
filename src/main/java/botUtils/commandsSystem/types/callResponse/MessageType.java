package botUtils.commandsSystem.types.callResponse;

import java.util.Locale;

public class MessageType {
    public final static int TEXT = 0;
    public final static int EMBED_BUILDER = 1;

    /**
     * Converts the integer form of a message type ({@link MessageType#TEXT} or {@link MessageType#EMBED_BUILDER}) to
     * a {@link String} equivalent. This is useful for writing message types to JSON files, where an integer would
     * be less readable by a user inspecting the Json. Only pass integer message types defined in this class,
     * otherwise an exception may be thrown. Note that output will always be all lowercase.
     *
     * @param type the int form of the message type (one of the constants defined in this class)
     * @return the String form of the message type
     * @throws IllegalArgumentException if the provided type int does not match a static constant in this class
     */
    public static String getTypeStr(int type) {
        return switch (type) {
            case TEXT -> "text";
            case EMBED_BUILDER -> "embed";
            default -> throw new IllegalArgumentException("Integer type " + type + " not recognized.");
        };
    }

    /**
     * Converts the String form of a message type ("text" or "embed") to a int equivalent. This is useful for
     * converting the user-friendly string types written in JSON files to the integer types that are more easily
     * compared in methods. Only pass one of the accepted types defined in this method, or else an exception will be
     * thrown that the input type was not recognized. Note that the type is <i>not</i> case sensitive.
     *
     * @param type the int form of the message type (one of the constants defined in this class)
     * @return the String form of the message type
     * @throws IllegalArgumentException if the provided type int does not match a static constant in this class
     */
    public static int getTypeInt(String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "text" -> TEXT;
            case "embed" -> EMBED_BUILDER;
            default -> throw new IllegalArgumentException("String type '" + type + "' not recognized.");
        };
    }
}