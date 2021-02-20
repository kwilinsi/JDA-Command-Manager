package botUtils.exceptions;

public class CommandClassException extends Exception {
    /**
     * Creates an exception with a message prefaced by the text "Error creating Command.\n"
     *
     * @param message the text to put after the preface
     */
    public CommandClassException(String message) {
        super("Error creating Command.\n" + message);
    }
}
