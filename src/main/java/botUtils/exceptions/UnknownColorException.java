package botUtils.exceptions;

import botUtils.tools.Colors;

/**
 * Indicates that a given color was not recognized. Used by conversion methods in {@link Colors}.
 */
public class UnknownColorException extends Exception {
    public UnknownColorException(String message) {
        super(message);
    }
}
