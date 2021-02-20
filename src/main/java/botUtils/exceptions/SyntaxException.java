package botUtils.exceptions;

import botUtils.commandsSystem.types.function.Syntax;

/**
 * Reports a problem with a specific Syntax being parsed
 */
public class SyntaxException extends Exception {
    private final Syntax syntax;

    public SyntaxException(String message) {
        super(message);
        this.syntax = null;
    }

    public SyntaxException(String message, Syntax syntax) {
        super(message);
        this.syntax = syntax;
    }

    public Syntax getSyntax() {
        return syntax;
    }
}
