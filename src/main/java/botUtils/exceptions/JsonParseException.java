package botUtils.exceptions;

/**
 * Defines an exception that occurs when the bot attempts to read a Json file but something doesn't work right.
 */
public class JsonParseException extends Exception {
    public JsonParseException() {
        super();
    }

    /**
     * Creates a JsonParseException object with the custom message prefaced by "Failed while reading Json.\n"
     *
     * @param message the part of the message that comes after "Failed while reading Json.\n"
     */
    public JsonParseException(String message) {
        super("Failed while reading Json.\n" + message);
    }
}
